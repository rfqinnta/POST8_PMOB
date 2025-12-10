package com.nanta.post8

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nanta.post8.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tasksRef: DatabaseReference
    private lateinit var adapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase ref
        tasksRef = FirebaseDatabase.getInstance().getReference("tasks")

        // Recycler
        adapter = TaskAdapter(taskList, tasksRef) { task ->
            // klik item -> edit
            EditTaskDialog(this, tasksRef).show(task)
        }

        binding.recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTasks.adapter = adapter

        // Tombol +
        binding.fabAdd.setOnClickListener {
            AddTaskDialog(this, tasksRef).show()
        }

        // Listener data Firebase
        tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<Task>()
                for (child in snapshot.children) {
                    val task = child.getValue(Task::class.java)
                    task?.id = child.key
                    if (task != null) newList.add(task)
                }
                adapter.setData(newList)

                binding.textEmpty.visibility =
                    if (newList.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        )
    }
}