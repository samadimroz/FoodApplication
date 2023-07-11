package com.imroz.foodapplication.adapter

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.imroz.foodapplication.R
import com.imroz.foodapplication.database.RestaurantDatabase
import com.imroz.foodapplication.database.RestaurantEntities
import com.imroz.foodapplication.fragment.RestaurantFragment
import com.imroz.foodapplication.model.Restaurants
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurants>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>(){

    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtRestaurantName: TextView= view.findViewById(R.id.txtRestaurantName) as TextView
        val txtRating: TextView= view.findViewById(R.id.txtRating) as TextView
        val txtCost: TextView= view.findViewById(R.id.txtCost) as TextView
        val imgRestaurant: ImageView= view.findViewById(R.id.imgRestaurant) as ImageView
        val imgIsFav: ImageView= view.findViewById(R.id.imgIsFav) as ImageView
        val cardRestaurant: CardView= view.findViewById(R.id.cardRestaurant) as CardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.home_single_row, parent, false)

        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList.get(position)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.imgRestaurant.clipToOutline = true
        }
        holder.txtRestaurantName.text = restaurant.name
        holder.txtRating.text = restaurant.rating
        val cost = "${restaurant.costForTwo.toString()}/person"
        holder.txtCost.text = cost

//        val restaurant= itemList.get(position)
//        holder.txtRestaurantName.text= restaurant.name
//        holder.txtRating.text= restaurant.rating
//        holder.txtCost.text= restaurant.cost.toString()
        Picasso.get().load(restaurant.image).error(R.drawable.food_logo).into(holder.imgRestaurant)

        val listOfFavourites = GetAllFavAsyncTask(context).execute().get()

        if (listOfFavourites.isNotEmpty() && listOfFavourites.contains(restaurant.id.toString())) {
            holder.imgIsFav.setImageResource(R.drawable.ic_action_favourite_checked)
        } else {
            holder.imgIsFav.setImageResource(R.drawable.ic_action_favourite)
        }

        holder.imgIsFav.setOnClickListener {
            val restaurantEntity = RestaurantEntities(
                restaurant.id,
                restaurant.name,
                restaurant.rating,
                restaurant.costForTwo.toString(),
                restaurant.image
            )

            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async =
                    DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    holder.imgIsFav.setImageResource(R.drawable.ic_action_favourite_checked)
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    holder.imgIsFav.setImageResource(R.drawable.ic_action_favourite)
                }
            }
        }

        holder.cardRestaurant.setOnClickListener {
            val fragment = RestaurantFragment()
            val args = Bundle()
            args.putInt("id", restaurant.id)
            args.putString("name", restaurant.name)
            fragment.arguments = args
            val transaction = (context as FragmentActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, fragment)
            transaction.commit()
            (context as AppCompatActivity).supportActionBar?.title = holder.txtRestaurantName.text.toString()
        }
    }

    class DBAsyncTask(context: Context, val restaurantEntity: RestaurantEntities, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {

                1 -> {
                    val res: RestaurantEntities? =
                        db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                    db.close()
                    return res != null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }

    class GetAllFavAsyncTask(context: Context): AsyncTask<Void, Void, List<String>>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): List<String> {

            val list = db.restaurantDao().getAllRestaurants()
            val listOfIds = arrayListOf<String>()
            for (i in list) {
                listOfIds.add(i.id.toString())
            }
            return listOfIds
        }
    }

}