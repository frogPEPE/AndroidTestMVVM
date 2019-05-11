package com.example.timesnewswire.core.mvvm

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * @param T Type of the PagedLists this Adapter will receive.
 * @param V A class that extends ViewHolder that will be used by the adapter.
 */
abstract class AdapterRV <T, V:RecyclerView.ViewHolder> (diffCallback: DiffUtil.ItemCallback<T>)
    :PagedListAdapter<T, V>(diffCallback)