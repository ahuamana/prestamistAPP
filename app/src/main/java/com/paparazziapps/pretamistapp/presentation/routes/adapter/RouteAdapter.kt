package com.paparazziapps.pretamistapp.presentation.routes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.paparazziapps.pretamistapp.R
import com.paparazziapps.pretamistapp.data.sources.route.RouteDomainSource
import com.paparazziapps.pretamistapp.databinding.ItemRouteBinding

class RouteAdapter(
    private val listener: OnRouteClickListener
): RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

    var routes: MutableList<RouteDomainSource> = mutableListOf()

    fun setData(listRoute: MutableList<RouteDomainSource>) {
        routes = listRoute
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RouteAdapter.RouteViewHolder {
        val binding = ItemRouteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RouteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RouteAdapter.RouteViewHolder, position: Int) {
        val currentItem = routes[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return routes.size
    }

    inner class RouteViewHolder(
        private val binding : ItemRouteBinding
    ): RecyclerView.ViewHolder(binding.root){


        fun bind(item: RouteDomainSource){

            itemView.apply {
                binding.routeNameTextView.text = item.name
            }

            itemView.rootView.setOnClickListener {
                listener.onRouteClick(item.id)
            }

        }
    }

}