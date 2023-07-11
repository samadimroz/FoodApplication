package com.imroz.foodapplication.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.imroz.foodapplication.R
import com.imroz.foodapplication.adapter.HomeRecyclerAdapter
import com.imroz.foodapplication.model.Restaurants
import org.json.JSONException
import org.json.JSONObject

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    val restaurantList= arrayListOf<Restaurants>()
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_home, container, false)

        recyclerHome= view.findViewById(R.id.restaurantRecycler) as RecyclerView
//        layoutManager= LinearLayoutManager(activity)
//        recyclerAdapter= HomeRecyclerAdapter(activity as Context, restaurantList)
//        recyclerHome.layoutManager= layoutManager
//        recyclerHome.adapter= recyclerAdapter

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        val queue= Volley.newRequestQueue(activity as Context)
        val url= "http://13.235.250.119/v2/restaurants/fetch_result/"

        val jsonObjectRequest= object : JsonObjectRequest(
            Request.Method.GET,url,
            null,
            Response.Listener<JSONObject>{ response ->
                progressLayout.visibility = View.GONE
                try{
                    val data = response.getJSONObject("data")
                    val success = data.getBoolean("success")

                    if (success){
                        val resData= data.getJSONArray("data")
                        for (i in 0 until resData.length()){
                            val restaurantJsonObject= resData.getJSONObject(i)
                            val restaurantObject= Restaurants(
                                restaurantJsonObject.getString("id").toInt(),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one").toInt(),
                                restaurantJsonObject.getString("image_url")
                            )
                            restaurantList.add(restaurantObject)

                            if (activity != null){

                                recyclerAdapter= HomeRecyclerAdapter(activity as Context, restaurantList)
                                val layoutManager= LinearLayoutManager(activity)
                                recyclerHome.layoutManager= layoutManager
                                recyclerHome.adapter= recyclerAdapter
                                recyclerHome.setHasFixedSize(true)

                            }
                        }


                    }
                }catch (e: JSONException){
                    e.printStackTrace()
                    Toast.makeText(activity as Context, e.message, Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {error: VolleyError? ->
                Toast.makeText(activity as Context, error?.message, Toast.LENGTH_SHORT).show()
            }){
            override fun getHeaders(): MutableMap<String, String> {
                val headers= HashMap<String, String>()
                headers["Content-type"]="application/json"
                headers["token"]="d3f7c22906be8c"
                return headers
            }
        }
        queue.add(jsonObjectRequest)

        return view
    }
}