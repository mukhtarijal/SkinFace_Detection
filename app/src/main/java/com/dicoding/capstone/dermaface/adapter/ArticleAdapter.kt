package com.dicoding.capstone.dermaface.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.capstone.dermaface.R
import com.dicoding.capstone.dermaface.data.model.OGS8pWLl3rnbNgRzzItem
import com.dicoding.capstone.dermaface.databinding.ItemArticleBinding
import com.dicoding.capstone.dermaface.ui.ArticleActivity

class ArticleAdapter(
    private val context: Context,
    private var articles: List<OGS8pWLl3rnbNgRzzItem>
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.binding.tvArticleTitle.text = article.title ?: context.getString(R.string.no_title)
        holder.binding.tvArticleDescription.text = article.desc ?: context.getString(R.string.no_description)

        Glide.with(holder.itemView.context)
            .load(article.imageUrl)
            .into(holder.binding.ivArticleImage)

        holder.binding.root.setOnClickListener {
            val intent = Intent(context, ArticleActivity::class.java).apply {
                putExtra(ArticleActivity.EXTRA_ARTICLE_URL, article.link)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = articles.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateArticles(newArticles: List<OGS8pWLl3rnbNgRzzItem>) {
        this.articles = newArticles
        notifyDataSetChanged()
    }

    class ArticleViewHolder(val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root)
}
