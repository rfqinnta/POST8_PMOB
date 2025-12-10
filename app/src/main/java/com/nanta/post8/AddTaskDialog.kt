package com.nanta.post8

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.nanta.post8.databinding.DialogAddTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddTaskDialog(
    private val context: Context,
    private val tasksRef: DatabaseReference
) {

    fun show() {
        val binding = DialogAddTaskBinding.inflate(LayoutInflater.from(context))

        // DatePicker untuk deadline
        binding.editTextDeadline.setOnClickListener {
            val c = Calendar.getInstance()
            val dialog = DatePickerDialog(
                context,
                { _, y, m, d ->
                    val cal = Calendar.getInstance()
                    cal.set(y, m, d)
                    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.editTextDeadline.setText(format.format(cal.time))
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }

        MaterialAlertDialogBuilder(context)
            .setTitle("Tambah Tugas Baru")
            .setView(binding.root)
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Simpan") { dialog, _ ->
                val title = binding.editTextTitle.text.toString()
                val desc = binding.editTextDescription.text.toString()
                val deadline = binding.editTextDeadline.text.toString()

                if (title.isEmpty() || deadline.isEmpty()) {
                    Toast.makeText(context, "Judul & deadline wajib diisi", Toast.LENGTH_SHORT).show()
                } else {
                    saveToFirebase(title, desc, deadline)
                }
            }
            .show()
    }

    private fun saveToFirebase(title: String, desc: String, deadline: String) {
        val id = tasksRef.push().key ?: return
        val task = Task(
            id = id,
            title = title,
            description = desc,
            deadline = deadline,
            done = false
        )
        tasksRef.child(id).setValue(task)
            .addOnSuccessListener {
                Toast.makeText(context, "Tugas ditambahkan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}