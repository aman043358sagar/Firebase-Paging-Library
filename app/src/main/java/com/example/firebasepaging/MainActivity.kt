package com.example.firebasepaging

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.example.firebasepaging.adapter.PersonPagingAdapter
import com.example.firebasepaging.databinding.ActivityMainBinding
import com.example.firebasepaging.databinding.LayoutAddPersonBinding
import com.example.firebasepaging.model.Person
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var adapter : PersonPagingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnAdd.setOnClickListener {
            showAddPersonDialog()
        }
        loadRecyclerView()
    }

    private fun loadRecyclerView() {
        val mDatabase: Query = FirebaseDatabase.getInstance().reference.child("person")
        val config = PagedList.Config.Builder().setEnablePlaceholders(false).setPrefetchDistance(5)
            .setPageSize(8).build()

        val options: DatabasePagingOptions<Person> =
            DatabasePagingOptions.Builder<Person>().setLifecycleOwner(this)
                .setQuery(mDatabase, config, Person::class.java)
                .setLifecycleOwner(this)
                .build()
        adapter = PersonPagingAdapter(options, binding.swipeRefreshLayout)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.swipeRefreshLayout.setOnRefreshListener { adapter.refresh() }
    }

    private fun showAddPersonDialog() {
        val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
        val binding = LayoutAddPersonBinding.inflate(layoutInflater)
        alertDialog.setTitle("Person Detail")
        alertDialog.setView(binding.root)

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"Save") { dialogInterface, which ->
            val person = Person(
                binding.etName.text.toString(),
                binding.etAge.text.toString(),
                binding.etOccupation.text.toString()
            )

            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("person")

            myRef.push().setValue(person).addOnSuccessListener {
                adapter.refresh()
            }
        }

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel") { dialogInterface, which ->
            alertDialog.dismiss()
        }

        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}