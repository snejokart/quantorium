package com.example.quantorium.adapters

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quantorium.databinding.ItemNewsBinding
import com.example.quantorium.models.ModelNews
import java.text.SimpleDateFormat
import java.util.Locale

class NewsAdapterRecyclerView(
    private val news: MutableList<ModelNews>
) : RecyclerView.Adapter<NewsAdapterRecyclerView.VH>() {

    class VH(val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = news.size

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val model = news[position]
        with(holder.binding) {
            titleNews.text = model.title
            textNews.text = model.description

            // Format the date
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) // Replace with your input format
            val outputFormat = SimpleDateFormat("d MMMM yyyy'г.'", Locale("ru")) // Use "d MMMM yyyy'г.'" and Russian locale

            try {
                val date = inputFormat.parse(model.date) // Parse the date string
                val formattedDate = outputFormat.format(date) // Format the date

                dateNews.text = formattedDate // Set the formatted date to TextView
            } catch (e: Exception) {
                dateNews.text = "Ошибка формата даты" // Display an error message if parsing fails
            }

            val url = model.link

            linkNews.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    ContextCompat.startActivity(holder.itemView.context, intent, null)
                } catch (e: ActivityNotFoundException){
                    // Handle the exception gracefully
                    Log.e("NewsAdapter", "No activity found to handle this intent: ${e.message}")
                    Toast.makeText(holder.itemView.context, "No app found to open the link", Toast.LENGTH_SHORT).show()
                }
                catch (e: Exception){
                    Log.e("NewsAdapter", "An unexpected error occured: ${e.message}")
                }
            }
        }
    }

    fun addData(newData: List<ModelNews>) {
        val currentSize = news.size
        val newNews = newData.filter { !news.contains(it) } // Filter out duplicates
        news.addAll(newNews)
        notifyItemRangeInserted(currentSize, newNews.size)
        Log.d("NewsAdapter", "Added ${newNews.size} new items, total size: ${news.size}")
    }

    fun clearData() {
        news.clear()
        notifyDataSetChanged()
        Log.d("NewsAdapter", "Cleared all items")
    }
}