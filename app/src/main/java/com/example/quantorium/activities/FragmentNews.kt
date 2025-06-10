package com.example.quantorium.activities

import NewsViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.quantorium.adapters.NewsAdapterRecyclerView
import com.example.quantorium.databinding.FragmentNewsBinding

class FragmentNews : Fragment() {

    private var binding: FragmentNewsBinding? = null
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var adapter: NewsAdapterRecyclerView
    private var offset = 0
    private val pageSize = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        binding?.recyclerNews?.layoutManager = LinearLayoutManager(context)

        // Initialize adapter
        adapter = NewsAdapterRecyclerView(mutableListOf())
        binding?.recyclerNews?.adapter = adapter

        swipeRefreshLayout = binding?.root?.findViewById(com.example.quantorium.R.id.swipe_refresh_layout)!!
        swipeRefreshLayout.setOnRefreshListener {
            refreshNews()
        }

        // Проверка: убедитесь, что OnClickListener установлен правильно
        binding?.loadMoreButton?.setOnClickListener {
            Log.d("FragmentNews", "Кнопка 'Показать больше' нажата")  // Добавляем лог
            loadNews()
        }

        loadNews() // Загружаем первую партию новостей
    }

    private fun loadNews() {
        swipeRefreshLayout.isRefreshing = true
        Log.d("FragmentNews", "loadNews() вызвана. offset = $offset, pageSize = $pageSize") // Добавляем лог
        newsViewModel.loadNews(offset, pageSize) // Важно: передаем offset
        newsViewModel.newsLiveData.observe(viewLifecycleOwner) { news ->
            if (news.isNotEmpty()) {
//                adapter.clearData() // Clear existing data
                adapter.addData(news) // Add new data
                offset += pageSize // Важно: увеличиваем offset
                Log.d("FragmentNews", "Новости загружены. Новый offset = $offset") // Добавляем лог
            }
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun refreshNews() {
        offset = 0 // Сбрасываем offset при обновлении
        adapter.clearData() // Очищаем список новостей
        loadNews() // Загружаем первую страницу новостей
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}