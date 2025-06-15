import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quantorium.databinding.ItemSheduleBinding
import com.example.quantorium.models.ScheduleItem

class ScheduleAdapter(private val scheduleItems: MutableList<ScheduleItem>) :
    RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(val binding: ItemSheduleBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val binding = ItemSheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val scheduleItem = scheduleItems[position]
        with(holder.binding) {
            timeTextView.text = scheduleItem.time
            dayOfWeekTextView.text = scheduleItem.dayOfWeek
            courseNameTextView.text = scheduleItem.courseName
            teacherFullNameTextView.text = scheduleItem.teacherFullName
            cabinetTextView.text = scheduleItem.cabinet
        }
    }

    override fun getItemCount(): Int = scheduleItems.size

    fun addData(newData: List<ScheduleItem>) {
        val currentSize = scheduleItems.size
        val newItems = newData.filter { !scheduleItems.contains(it) }
        scheduleItems.addAll(newItems)
        notifyItemRangeInserted(currentSize, newItems.size)
    }

    fun clearData() {
        scheduleItems.clear()
        notifyDataSetChanged()
    }
}