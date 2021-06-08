package com.technopolis_education.globusapp.logic.adapter.empty.profile.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.technopolis_education.globusapp.databinding.ItemProfileActivityEmptyBinding
import com.technopolis_education.globusapp.databinding.ItemProfileFriendsEmptyBinding

class EmptyProfileActivityAdapter(
) : RecyclerView.Adapter<EmptyProfileActivityViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmptyProfileActivityViewHolder {
        return EmptyProfileActivityViewHolder(
            ItemProfileActivityEmptyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holderProfile: EmptyProfileActivityViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 1
    }
}