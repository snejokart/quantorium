
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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

        scheduleViewModel = ViewModelProvider(requireActivity())[ScheduleViewModel::class.java]

        binding.scheduleRecyclerView.layoutManager = LinearLayoutManager(context)
        scheduleAdapter = ScheduleAdapter(mutableListOf())
        binding.scheduleRecyclerView.adapter = scheduleAdapter

        binding.scheduleRecyclerView.isVisible = false
        binding.progressBar.isVisible = true

        loadSchedule()
    }

    private fun loadSchedule() {
        scheduleViewModel.loadSchedule()
        scheduleViewModel.scheduleLiveData.observe(viewLifecycleOwner, Observer { scheduleItems ->
            binding.scheduleRecyclerView.isVisible = true
            binding.progressBar.isVisible = false
            if (scheduleItems != null) {
                scheduleAdapter.addData(scheduleItems)
            } else {
                // Handle the case where scheduleItems is null (e.g., display an error message)
                Log.e("FragmentSchedule", "Schedule items is null!")
                binding.scheduleRecyclerView.isVisible = false
                binding.progressBar.isVisible = false
                // TODO: Show an error message or a placeholder layout
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
