package com.dicoding.picodiploma.loginwithanimation.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.view.story.StoryDetailActivity

class StoryAdapter : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    private val stories: MutableList<ListStoryItem> = mutableListOf()

    class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.itemImage)
        val name: TextView = view.findViewById(R.id.itemName)
        val description: TextView = view.findViewById(R.id.itemDescription)

        fun bind(story: ListStoryItem) {
            name.text = story.name
            description.text = story.description
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return StoryViewHolder(view).apply {
            itemView.setOnClickListener {
                val story = stories[adapterPosition]
                val intent = Intent(it.context, StoryDetailActivity::class.java)
                intent.putExtra(StoryDetailActivity.EXTRA_ID, story.id)
                it.context.startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(it.context as Activity).toBundle()
                )
            }
        }
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = stories[position]
        holder.bind(story)
    }

    fun setStories(newStories: List<ListStoryItem>) {
        val diffCallback = StoryCallback(stories, newStories)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        stories.clear()
        stories.addAll(newStories)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int = stories.size
}
