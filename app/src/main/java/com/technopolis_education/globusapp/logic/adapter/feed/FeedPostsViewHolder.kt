package com.technopolis_education.globusapp.logic.adapter.feed

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.technopolis_education.globusapp.databinding.ItemFeedPostBinding

class FeedPostsViewHolder(
    binding: ItemFeedPostBinding
) : RecyclerView.ViewHolder(binding.root) {
    var title: TextView = binding.activityTitle
    var content: TextView = binding.activityContent
    var creator: TextView = binding.activityAuthor
    var date: TextView = binding.activityDate
    var country: TextView = binding.activityCountry
}