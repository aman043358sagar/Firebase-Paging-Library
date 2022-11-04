package com.example.firebasepaging.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.firebasepaging.databinding.LayoutPersonBinding
import com.example.firebasepaging.model.Person
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseError
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter
import com.shreyaspatil.firebase.recyclerpagination.LoadingState

class PersonPagingAdapter(
    options: DatabasePagingOptions<Person>,
    swipeRefreshLayout: SwipeRefreshLayout,
) :
    FirebaseRecyclerPagingAdapter<Person, PersonPagingAdapter.ViewHolder>(options) {
    var swipeRefreshLayout: SwipeRefreshLayout

    init {
        this.swipeRefreshLayout = swipeRefreshLayout
    }

    override fun onBindViewHolder(
        viewHolder: ViewHolder,
        position: Int,
        Person: Person,
    ) {
        viewHolder.bind(position, Person)
    }

    override fun onLoadingStateChanged(state: LoadingState) {
        when (state) {
            LoadingState.LOADING_INITIAL, LoadingState.LOADING_MORE ->
                swipeRefreshLayout.isRefreshing = true
            LoadingState.FINISHED -> {
                Snackbar.make(swipeRefreshLayout,"No more data",Snackbar.LENGTH_SHORT).show()
                swipeRefreshLayout.isRefreshing = false
            }
            LoadingState.LOADED, LoadingState.ERROR ->
                // Stop Animation
                swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onError(databaseError: DatabaseError) {
        super.onError(databaseError)
        Log.d("sgsdge", "onError: " + databaseError.message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutPersonBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    inner class ViewHolder(private val binding: LayoutPersonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, person: Person) {
            binding.tvName.text = person.name
            binding.tvAge.text = person.age
            binding.tvOccupation.text = person.occupation
            binding.ivDelete.setOnClickListener {
                getRef(position).removeValue()
                this@PersonPagingAdapter.refresh()
            }
        }
    }
}
