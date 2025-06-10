
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quantorium.databinding.FragmentScheduleBinding

class FragmentSchedule : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        scheduleViewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]

        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(context)
        scheduleAdapter = ScheduleAdapter(mutableListOf())
        binding.scheduleRecyclerView.adapter = scheduleAdapter

        loadSchedule()
    }

    private fun loadSchedule() {
        scheduleViewModel.loadSchedule()
        scheduleViewModel.scheduleLiveData.observe(viewLifecycleOwner) { scheduleItems ->
            scheduleAdapter.addData(scheduleItems)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}