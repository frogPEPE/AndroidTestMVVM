package com.example.timesnewswire.activities.articlesRecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.timesnewswire.core.mvvm.AdapterRV
import com.example.timesnewswire.core.CoreTools
import com.example.timesnewswire.models.roomDatabases.timesNewsWireDatabase.articles.ArticleEntity
import com.example.timesnewswire.R

class RVAdapter(diffCallback: DiffUtil.ItemCallback<ArticleEntity>, private val onItemClickListener: OnItemClickListener)
    : AdapterRV<ArticleEntity, RVAdapter.ArticleViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.times_newswire_item, parent, false)

        return ArticleViewHolder(itemView, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    interface OnItemClickListener {
        fun onItemClick(article: ArticleEntity)
    }

    inner class ArticleViewHolder(itemView: View, private val onClickListener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val articleTitle: TextView
                by lazy { itemView.findViewById<TextView>(R.id.mainRecyclerView_Item_TextView_ArticleTitle) }
        val articleSection: TextView
                by lazy { itemView.findViewById<TextView>(R.id.mainRecyclerView_Item_TextView_ArticleSection) }
        val createdTime: TextView
                by lazy { itemView.findViewById<TextView>(R.id.mainRecyclerView_Item_TextView_CreatedTime) }
        val viewSeparator: View
                by lazy { itemView.findViewById<View>(R.id.mainRecyclerView_Item_View_Separate_Upper) }

        init {
            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    val position: Int = adapterPosition
                    if (position != RecyclerView.NO_POSITION)
                        getItem(position)?.let { onClickListener.onItemClick(it) }
                }
            })
        }

        fun bind(article: ArticleEntity) {
            val coreTools = CoreTools()
            articleTitle.text = article.title
            articleSection.text = article.sectionName
            createdTime.text = coreTools.formatTo(
                article.createdDateTime,
                coreTools.dateFormatDefault,
                coreTools.timeZoneDefault
            )
        }
    }
}