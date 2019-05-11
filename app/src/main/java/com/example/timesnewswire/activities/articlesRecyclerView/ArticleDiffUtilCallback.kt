package com.example.timesnewswire.activities.articlesRecyclerView

import androidx.recyclerview.widget.DiffUtil
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity

class ArticleDiffUtilCallback: DiffUtil.ItemCallback<ArticleEntity>() {
    override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
        return oldItem.equals(newItem)
    }

    override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
        return oldItem.title == newItem.title &&
                oldItem.sectionName == newItem.sectionName &&
                oldItem.createdDateTime.time == newItem.createdDateTime.time
    }
}