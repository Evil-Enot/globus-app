package com.technopolis_education.globusapp.logic.adapter.feed

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.technopolis_education.globusapp.databinding.ItemFeedPostBinding
import com.technopolis_education.globusapp.model.PostRequest

class FeedPostsAdapter(
    private val activity: ArrayList<PostRequest>,
    private val context: Context?
) : RecyclerView.Adapter<FeedPostsViewHolder>(), Filterable {

    var activityList: ArrayList<PostRequest> = activity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedPostsViewHolder {
        return FeedPostsViewHolder(
            ItemFeedPostBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FeedPostsViewHolder, position: Int) {
        holder.creator.text =
            "${activityList[position].userName} ${activityList[position].userSurname}"
        holder.title.text = "Title"
        holder.content.text = activityList[position].text


        val latitude = activityList[position].point?.latitude?.toDouble()!!
        val longitude = activityList[position].point?.longitude?.toDouble()!!

        val geocoder = Geocoder(context).getFromLocation(
            latitude,
            longitude,
            1
        )[0]

        Log.i("test", "${geocoder.countryName} ${geocoder.adminArea}")
        holder.country.text = "${geocoder.countryName} ${geocoder.adminArea}"

        holder.date.text = activityList[position].time
    }

    override fun getItemCount(): Int {
        return activityList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                activityList = if (charSearch.isEmpty()) {
                    activity
                } else {
                    val resultList = ArrayList<PostRequest>()
                    for (activity in activityList) {
                        if ((activity.text)
                                .contains(charSearch.toLowerCase())
                        ) {
                            resultList.add(activity)
                        }
                    }
                    resultList
                }
                val filterResult = FilterResults()
                filterResult.values = activityList
                return filterResult
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                activityList = results?.values as ArrayList<PostRequest>
                notifyDataSetChanged()
            }
        }
    }
}
