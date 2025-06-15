
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quantorium.data.SupabaseUser
import com.example.quantorium.models.ScheduleItem
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class ScheduleViewModel : ViewModel() {
    val scheduleLiveData = MutableLiveData<List<ScheduleItem>>()
    private val maxRetries = 5
    private val retryDelay = 1000L
    private var areSchedulesLoaded = false

    fun loadSchedule() {
        if (!areSchedulesLoaded) {
            loadSchedule(0)
        } else {
            Log.d("ScheduleViewModel", "Расписание уже загружено, пропуск загрузки")
        }
    }

    private fun loadSchedule(attempt: Int) {
        viewModelScope.launch {
            try {
                val data = withContext(Dispatchers.IO) {
                    if (!viewModelScope.isActive) {
                        return@withContext emptyList()
                    }
                    SupabaseUser.supabase.postgrest
                        .from("schedule")
                        .select(columns = Columns.raw(
                            """
                            id,
                            time,
                            day_of_week,
                            cabinet,
                            courses ( name ),
                            teachers ( full_name )
                            """
                        ))
                        .decodeList<ScheduleResponse>()
                        .map { it.toScheduleItem() }
                }

                if (viewModelScope.isActive) {
                    scheduleLiveData.postValue(data)
                    areSchedulesLoaded = true
                    Log.d("ScheduleViewModel", "Расписание успешно загружено")
                }

            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Ошибка загрузки расписания: ${e.message}, попытка = $attempt")
                if (attempt < maxRetries) {
                    Log.d("ScheduleViewModel", "Повторная попытка загрузки расписания через $retryDelay мс")
                    delay(retryDelay)
                    loadSchedule(attempt + 1)
                } else {
                    Log.e("ScheduleViewModel", "Превышено максимальное количество попыток загрузки расписания")
                    scheduleLiveData.postValue(emptyList())
                }
            }
        }
    }

    @Serializable
    data class ScheduleResponse(
        val id: Int,
        val time: String,
        val day_of_week: String,
        val cabinet: String,
        val courses: CourseResponse?,
        val teachers: TeacherResponse?
    ) {
        fun toScheduleItem(): ScheduleItem {
            return ScheduleItem(
                id = id,
                time = time,
                dayOfWeek = day_of_week,
                cabinet = cabinet,
                courseName = courses?.name ?: "Нет названия",
                teacherFullName = teachers?.full_name ?: "Нет имени"
            )
        }
    }

    @Serializable
    data class CourseResponse(val name: String)

    @Serializable
    data class TeacherResponse(val full_name: String)
}
