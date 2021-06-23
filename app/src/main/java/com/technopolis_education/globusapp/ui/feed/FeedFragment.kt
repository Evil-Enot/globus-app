package com.technopolis_education.globusapp.ui.feed

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
import com.technopolis_education.globusapp.databinding.FragmentFeedBinding
import com.technopolis_education.globusapp.logic.adapter.empty.profile.activity.EmptyProfileActivityAdapter
import com.technopolis_education.globusapp.logic.adapter.error.InternetErrorAdapter
import com.technopolis_education.globusapp.logic.adapter.feed.FeedPostsAdapter
import com.technopolis_education.globusapp.logic.check.InternetConnectionCheck
import com.technopolis_education.globusapp.model.OneEmailRequest
import com.technopolis_education.globusapp.model.PostRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedFragment : Fragment() {

    private val webClient = WebClient().getApi()
    private var userFeedList: ArrayList<PostRequest> = ArrayList()

    private lateinit var galleryViewModel: FeedViewModel
    private var _binding: FragmentFeedBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userFeedList.clear()
        galleryViewModel =
            ViewModelProvider(this).get(FeedViewModel::class.java)

        _binding = FragmentFeedBinding.inflate(inflater, container, false)
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
        // User feed and filter
        val userActivity: RecyclerView = binding.feedRecyclerView

        val emailRequest = OneEmailRequest(
            userEmail
        )

        showUserFeed(emailRequest, userActivity)
        //------------------------------------------------------//

        return root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showUserFeed(
        oneEmailRequest: OneEmailRequest,
        userFeed: RecyclerView
    ) {
        if (!InternetConnectionCheck().isOnline(context)) {
            Toast.makeText(
                context,
                getString(R.string.error_no_internet_connection),
                Toast.LENGTH_LONG
            ).show()
            printFeed(userFeed, userFeedList)
        } else {
            userFeedList.clear()
            val callUserActivity = webClient.getNews(oneEmailRequest)
            callUserActivity.enqueue(object : Callback<ArrayList<PostRequest>> {
                override fun onResponse(
                    call: Call<ArrayList<PostRequest>>,
                    response: Response<ArrayList<PostRequest>>
                ) {
                    for (i in 0 until response.body()!!.size) {
                        val userGroup = response.body()!![i]
                        userFeedList.add(userGroup)
                    }
                    printFeed(userFeed, userFeedList)
                }

                override fun onFailure(call: Call<ArrayList<PostRequest>>, t: Throwable) {
                    Log.i("test", "error $t")
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun printFeed(
        userFeed: RecyclerView,
        userFeedList: ArrayList<PostRequest>
    ) {
        userFeed.layoutManager = LinearLayoutManager(context)

        if (!InternetConnectionCheck().isOnline(context)) {
            Toast.makeText(
                context,
                getString(R.string.error_no_internet_connection),
                Toast.LENGTH_LONG
            ).show()

            userFeed.adapter = InternetErrorAdapter()
        } else {
            if (userFeedList.isEmpty()) {
                userFeed.adapter = EmptyProfileActivityAdapter()
            } else {
                userFeed.adapter = FeedPostsAdapter(userFeedList, context)

                val adapter = FeedPostsAdapter(userFeedList, context)
                binding.activitySearch.setOnQueryTextListener(object :
                    SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        binding.activitySearch.clearFocus()
                        return false
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        adapter.filter.filter(p0)
                        return false
                    }
                })
                userFeed.adapter = adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}