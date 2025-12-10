package com.nanta.post8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference

class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val tasksRef: DatabaseReference,
    private val onEdit: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkDone: CheckBox = itemView.findViewById(R.id.checkDone)
        val textTitle: TextView = itemView.findViewById(R.id.textTitleTask)
        val textDescription: TextView = itemView.findViewById(R.id.textDescriptionTask)
        val textDeadline: TextView = itemView.findViewById(R.id.textDeadlineTask)
        val buttonDelete: ImageView = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.textTitle.text = task.title
        holder.textDescription.text = task.description
        holder.textDeadline.text = task.deadline

        holder.checkDone.setOnCheckedChangeListener(null)
        holder.checkDone.isChecked = task.done

        holder.itemView.alpha = if (task.done) 0.4f else 1f

        holder.itemView.setOnClickListener {
            if (!task.done) onEdit(task)
        }

        holder.checkDone.setOnCheckedChangeListener { _, isChecked ->
            task.done = isChecked
            task.id?.let { id ->
                tasksRef.child(id).child("done").setValue(isChecked)
            }
            holder.itemView.alpha = if (isChecked) 0.4f else 1f
        }

        holder.buttonDelete.setOnClickListener {
            val context = holder.itemView.context
            task.id?.let { id ->
                tasksRef.child(id).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Tugas dihapus", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    fun setData(newList: List<Task>) {
        tasks.clear()
        tasks.addAll(newList)
        notifyDataSetChanged()
    }
}
