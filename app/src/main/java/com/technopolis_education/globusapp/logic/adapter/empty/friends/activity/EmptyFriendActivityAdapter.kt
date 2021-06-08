package com.technopolis_education.globusapp.logic.adapter.empty.friends.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.technopolis_education.globusapp.databinding.ItemFriendsActivityEmptyBinding
import com.technopolis_education.globusapp.databinding.ItemFriendsFriendsEmptyBinding

class EmptyFriendActivityAdapter(
) : RecyclerView.Adapter<EmptyFriendActivityViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmptyFriendActivityViewHolder {
        return EmptyFriendActivityViewHolder(
            ItemFriendsActivityEmptyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holderFriend: EmptyFriendActivityViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 1
    }
}