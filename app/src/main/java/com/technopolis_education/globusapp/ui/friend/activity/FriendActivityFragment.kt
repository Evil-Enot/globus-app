package com.technopolis_education.globusapp.ui.friend.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.technopolis_education.globusapp.R
import com.technopolis_education.globusapp.api.WebClient
import com.technopolis_education.globusapp.databinding.FragmentFriendActivityBinding
import com.technopolis_education.globusapp.logic.adapter.empty.friends.activity.EmptyFriendActivityAdapter
import com.technopolis_education.globusapp.logic.adapter.error.InternetErrorAdapter
import com.technopolis_education.globusapp.logic.adapter.profile.ProfileUserActivityAdapter
import com.technopolis_education.globusapp.logic.check.InternetConnectionCheck
import com.technopolis_education.globusapp.model.OneEmailRequest
import com.technopolis_education.globusapp.model.PostRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendActivityFragment : Fragment() {
    private val webClient = WebClient().getApi()
    private var friendActivityList: ArrayList<PostRequest> = ArrayList()

    private lateinit var friendActivityViewModel: FriendActivityViewModel
    private var _binding: FragmentFriendActivityBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        friendActivityList.clear()
        friendActivityViewModel =
            ViewModelProvider(this).get(FriendActivityViewModel::class.java)

        _binding = FragmentFriendActivityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //------------------------------------------------------//
        // Friend email
        val friendEmailSP = context?.getSharedPreferences("FRIEND EMAIL", Context.MODE_PRIVATE)
        var friendEmailText = ""
        if (friendEmailSP?.contains("FriendEmail") == true) {
            friendEmailText = friendEmailSP.getString("FriendEmail", "").toString()
        }
        Log.i("test", friendEmailText)
        //------------------------------------------------------//

        //------------------------------------------------------//
        // Friend activity and filter
        val userActivity: RecyclerView = binding.friendActivity

        val emailRequest = OneEmailRequest(
            friendEmailText
        )

        showUserActivity(emailRequest, userActivity)
        //------------------------------------------------------//

        return root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showUserActivity(
        oneEmailRequest: OneEmailRequest,
        friendActivity: RecyclerView
    ) {
        if (!InternetConnectionCheck().isOnline(context)) {
            Toast.makeText(
                context,
                getString(R.string.error_no_internet_connection),
                Toast.LENGTH_LONG
            ).show()
            printActivity(friendActivity, friendActivityList)
        } else {
            friendActivityList.clear()
            val callFriendActivity = webClient.findAllPost(oneEmailRequest)
            callFriendActivity.enqueue(object : Callback<ArrayList<PostRequest>> {
                override fun onResponse(
                    call: Call<ArrayList<PostRequest>>,
                    response: Response<ArrayList<PostRequest>>
                ) {
                    for (i in 0 until response.body()!!.size) {
                        val userGroup = response.body()!![i]
                        friendActivityList.add(userGroup)
                    }
                    printActivity(friendActivity, friendActivityList)
                }

                override fun onFailure(call: Call<ArrayList<PostRequest>>, t: Throwable) {
                    Log.i("test", "error $t")
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun printActivity(
        friendActivity: RecyclerView,
        friendActivityList: ArrayList<PostRequest>
    ) {
        friendActivity.layoutManager =
            LinearLayoutManager(context)

        if (!InternetConnectionCheck().isOnline(context)) {
            Toast.makeText(
                context,
                getString(R.string.error_no_internet_connection),
                Toast.LENGTH_LONG
            ).show()

            friendActivity.adapter = InternetErrorAdapter()
        } else {
            if (friendActivityList.isEmpty()) {
                friendActivity.adapter = EmptyFriendActivityAdapter()
            } else {
                friendActivity.adapter = ProfileUserActivityAdapter(friendActivityList, context)

                val adapter = ProfileUserActivityAdapter(friendActivityList, context)
                binding.activitySearch.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(newFriend: String?): Boolean {
                        binding.activitySearch.clearFocus()
                        return false
                    }

                    override fun onQueryTextChange(newFriend: String?): Boolean {
                        adapter.filter.filter(newFriend)
                        return false
                    }
                })

                friendActivity.adapter = adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}