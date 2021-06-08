package com.technopolis_education.globusapp.logic.adapter.profile

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.technopolis_education.globusapp.databinding.ItemProfileActiivityCardBinding
import com.technopolis_education.globusapp.model.PostRequest

class ProfileUserActivityAdapter(
    private val userActivity: ArrayList<PostRequest>,
    private val context: Context?
) : RecyclerView.Adapter<ProfileUserActivityViewHolder>(), Filterable {

    var userActivityList: ArrayList<PostRequest> = userActivity

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileUserActivityViewHolder {
        return ProfileUserActivityViewHolder(
            ItemProfileActiivityCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProfileUserActivityViewHolder, position: Int) {
        holder.activityAuthor.text =
            "${userActivityList[position].userName} ${userActivityList[position].userSurname}"
        holder.activityTitle.text = "Title"
        holder.activityContent.text = userActivityList[position].text


        val latitude = userActivityList[position].point?.latitude?.toDouble()!!
        val longitude = userActivityList[position].point?.longitude?.toDouble()!!

        val geocoder = Geocoder(context).getFromLocation(
            latitude,
            longitude,
            1
        )[0]

        Log.i("test", "${geocoder.countryName} ${geocoder.adminArea}")
        holder.activityCountry.text = "${geocoder.countryName} ${geocoder.adminArea}"

        holder.activityDate.text = userActivityList[position].time
    }

    override fun getItemCount(): Int {
        return userActivityList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                userActivityList = if (charSearch.isEmpty()) {
                    userActivity
                } else {
                    val resultList = ArrayList<PostRequest>()
                    for (activity in userActivity) {

                        //FIX

                        if ((activity.text)
                                .contains(charSearch.toLowerCase())
                        ) {
                            resultList.add(activity)
                        }
                    }
                    resultList
                }
                val filterResult = FilterResults()
                filterResult.values = userActivityList
                return filterResult
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                userActivityList = results?.values as ArrayList<PostRequest>
                notifyDataSetChanged()
            }
        }
    }
}