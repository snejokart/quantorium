package com.example.quantorium.activities

import NewsViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quantorium.adapters.NewsAdapterRecyclerView
import com.example.quantorium.databinding.FragmentNewsBinding
import kotlinx.coroutines.launch

class FragmentNews : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var adapter: NewsAdapterRecyclerView
    private lateinit var layoutManager: LinearLayoutManager

    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the ViewModel, scoped to the Activity
        newsViewModel = ViewModelProvider(requireActivity())[NewsViewModel::class.java]

        layoutManager = LinearLayoutManager(context)
        binding.recyclerNews.layoutManager = layoutManager
        adapter = NewsAdapterRecyclerView(mutableListOf())
        binding.recyclerNews.adapter = adapter

        binding.recyclerNews.isVisible = false
        binding.progressBar.isVisible = true

        binding.recyclerNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading) {
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                    if (totalItemCount <= (lastVisibleItem + 2)) {
                        Log.d("FragmentNews", "Достигнут конец списка, загружаем больше")
                        loadNews()
                    }
                }
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            loadNews() // Load initial data
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("FragmentNews", "onResume() called")
    }

    private fun loadNews() {
        if (!isLoading) {
            isLoading = true
            Log.d("FragmentNews", "loadNews() вызвана")
            //newsViewModel.resetOffset() // Delete this line

            newsViewModel.loadNews()

            newsViewModel.newsLiveData.observe(viewLifecycleOwner, Observer { news ->
                Log.d("FragmentNews", "Observer сработал, получено ${news.size} новостей")
                if (news != null) {
                    adapter.clearData()
                    adapter.addData(news)
                    binding.recyclerNews.isVisible = true
                    binding.progressBar.isVisible = false
                } else {
                    Log.w("FragmentNews", "newsLiveData is null - Ошибка при загрузке")
                    // Отобразить сообщение об ошибке, если не удалось загрузить
                    // Например, используя TextView или Snackbar
                }
                isLoading = false
            })
        } else {
            Log.d("FragmentNews", "loadNews() проигнорирована, уже идет загрузка")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}