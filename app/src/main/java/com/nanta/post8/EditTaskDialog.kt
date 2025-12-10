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

class EditTaskDialog(
    private val context: Context,
    private val tasksRef: DatabaseReference
) {

    fun show(task: Task) {
        val binding = DialogAddTaskBinding.inflate(LayoutInflater.from(context))

        binding.editTextTitle.setText(task.title)
        binding.editTextDescription.setText(task.description)
        binding.editTextDeadline.setText(task.deadline)

        binding.editTextDeadline.setOnClickListener {
            val c = Calendar.getInstance()
            DatePickerDialog(
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
            ).show()
        }

        // Dialog edit
        MaterialAlertDialogBuilder(context)
            .setTitle("Edit Tugas")
            .setView(binding.root)
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Simpan") { _, _ ->

                val title = binding.editTextTitle.text.toString().trim()
                val desc = binding.editTextDescription.text.toString().trim()
                val deadline = binding.editTextDeadline.text.toString().trim()

                if (title.isEmpty() || deadline.isEmpty()) {
                    Toast.makeText(context, "Judul & deadline wajib diisi", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Cek ID
                val id = task.id
                if (id.isNullOrEmpty()) {
                    Toast.makeText(context, "ID task tidak ditemukan", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val updated = task.copy(
                    title = title,
                    description = desc,
                    deadline = deadline
                )

                tasksRef.child(id).setValue(updated)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Tugas berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
            }
            .show()
    }
}
