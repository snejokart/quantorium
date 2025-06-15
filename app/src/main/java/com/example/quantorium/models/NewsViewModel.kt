import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quantorium.data.SupabaseUser
import com.example.quantorium.models.ModelNews
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    val newsLiveData = MutableLiveData<List<ModelNews>>()
    private var currentOffset = 0
    private val pageSize = 50
    private val maxRetries = 5
    private val retryDelay = 1000L
    private var areNewsLoaded = false // Добавили флаг

    fun loadNews() {
        if (!areNewsLoaded) { // Проверяем, загружены ли новости
            loadNews(currentOffset, pageSize, 0)
        } else {
            Log.d("NewsViewModel", "Новости уже загружены, пропуск загрузки")
        }
    }

    private fun loadNews(offset: Int, limit: Int, attempt: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("NewsViewModel", "Загрузка новостей с offset=$offset, limit=$limit, попытка=$attempt")
            try {
                val data = SupabaseUser.supabase.postgrest
                    .from("news")
                    .select {
                        range(offset.toLong()..(offset + limit - 1).toLong())
                    }
                    .decodeList<ModelNews>()

                Log.d("NewsViewModel", "Загружено ${data.size} новостей")
                if (viewModelScope.isActive) {
                    newsLiveData.postValue(data)
                }
                currentOffset += limit
                areNewsLoaded = true // Устанавливаем флаг в true при успешной загрузке
            } catch (e: Exception) {
                Log.e("NewsViewModel", "Ошибка загрузки новостей: ${e.message}, попытка=$attempt", e)
                if (attempt < maxRetries) {
                    Log.d("NewsViewModel", "Повторная попытка загрузки новостей через $retryDelay мс")
                    delay(retryDelay)
                    loadNews(offset, limit, attempt + 1)
                } else {
                    Log.e("NewsViewModel", "Превышено максимальное количество попыток загрузки новостей")
                    newsLiveData.postValue(emptyList())
                }
            }
        }
    }

    fun resetOffset() {
        currentOffset = 0
        areNewsLoaded = false // Сбрасываем флаг при сбросе
    }
}