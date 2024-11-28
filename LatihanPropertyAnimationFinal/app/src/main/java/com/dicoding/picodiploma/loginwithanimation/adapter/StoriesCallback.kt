package com.dicoding.picodiploma.loginwithanimation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem

class StoryCallback(
    private val oldList: List<ListStoryItem>,
    private val newList: List<ListStoryItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}