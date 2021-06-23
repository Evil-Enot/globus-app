package com.technopolis_education.globusapp.ui.profile.activity

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
import com.technopolis_education.globusapp.databinding.FragmentProfileUserActivityBinding
import com.technopolis_education.globusapp.logic.adapter.empty.profile.activity.EmptyProfileActivityAdapter
import com.technopolis_education.globusapp.logic.adapter.error.InternetErrorAdapter
import com.technopolis_education.globusapp.logic.adapter.profile.ProfileUserActivityAdapter
import com.technopolis_education.globusapp.logic.check.InternetConnectionCheck
import com.technopolis_education.globusapp.model.OneEmailRequest
import com.technopolis_education.globusapp.model.PostRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserActivityFragment : Fragment() {

    private val webClient = WebClient().getApi()
    private var userActivityList: ArrayList<PostRequest> = ArrayList()

    private lateinit var userActivityViewModel: UserActivityViewModel
    private var _binding: FragmentProfileUserActivityBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userActivityList.clear()
        userActivityViewModel =
            ViewModelProvider(this).get(UserActivityViewModel::class.java)

        _binding = FragmentProfileUserActivityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //------------------------------------------------------//
        // User email
        val userEmailSP = context?.getSharedPreferences("USER EMAIL", Context.MODE_PRIVATE)
        var userEmail = ""
        if (userEmailSP?.contains("UserEmail") == true) {
            userEmail = userEmailSP.getString("UserEmail", "").toString()
        }
        //------------------------------------------------------//

        //------------------------------------------------------//
        // User activity and filter
        val userActivity: RecyclerView = binding.userActivity

        val emailRequest = OneEmailRequest(
            userEmail
        )

        showUserActivity(emailRequest, userActivity)
        //------------------------------------------------------//

        return root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showUserActivity(
        oneEmailRequest: OneEmailRequest,
        userActivity: RecyclerView
    ) {
        if (!InternetConnectionCheck().isOnline(context)) {
            Toast.makeText(
                context,
                getString(R.string.error_no_internet_connection),
                Toast.LENGTH_LONG
            ).show()
            printActivity(userActivity, userActivityList)
        } else {
            userActivityList.clear()
            val callUserActivity = webClient.findAllPost(oneEmailRequest)
            callUserActivity.enqueue(object : Callback<ArrayList<PostRequest>> {
                override fun onResponse(
                    call: Call<ArrayList<PostRequest>>,
                    response: Response<ArrayList<PostRequest>>
                ) {
                    for (i in response.body()!!.size - 1 downTo 0) {
                        val userGroup = response.body()!![i]
                        userActivityList.add(userGroup)
                    }
                    printActivity(userActivity, userActivityList)
                }

                override fun onFailure(call: Call<ArrayList<PostRequest>>, t: Throwable) {
                    Log.i("test", "error $t")
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun printActivity(
        userActivity: RecyclerView,
        userActivityList: ArrayList<PostRequest>
    ) {
        userActivity.layoutManager =
            LinearLayoutManager(context)
        if (!InternetConnectionCheck().isOnline(context)) {
            Toast.makeText(
                context,
                getString(R.string.error_no_internet_connection),
                Toast.LENGTH_LONG
            ).show()

            userActivity.adapter = InternetErrorAdapter()
        } else {
            if (userActivityList.isEmpty()) {
                userActivity.adapter = EmptyProfileActivityAdapter()
            } else {
                userActivity.adapter = ProfileUserActivityAdapter(userActivityList, context)

                val adapter = ProfileUserActivityAdapter(userActivityList, context)
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

                userActivity.adapter = adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}