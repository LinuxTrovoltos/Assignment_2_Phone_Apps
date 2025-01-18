package com.example.studentsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Data Model
data class Student(
    var id: String,
    var name: String,
    var isChecked: Boolean = false
)

// In-Memory Database
object StudentDatabase {
    val students = mutableListOf<Student>()
}

// Main Activity - Students List
class StudentsListActivity<FloatingActionButton : View?> : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students_list)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = StudentsAdapter(StudentDatabase.students) { student ->
            val intent = Intent(this, StudentDetailsActivity::class.java)
            intent.putExtra("studentId", student.id)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddStudent)?.setOnClickListener {
            startActivity(Intent(this, NewStudentActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}

// Adapter for RecyclerView
class StudentsAdapter(
    private val students: List<Student>,
    private val onItemClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentsAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.tvName)
        val id = itemView.findViewById<TextView>(R.id.tvId)
        val checkbox = itemView.findViewById<CheckBox>(R.id.cbChecked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.name.text = student.name
        holder.id.text = student.id
        holder.checkbox.isChecked = student.isChecked
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            student.isChecked = isChecked
        }
        holder.itemView.setOnClickListener { onItemClick(student) }
    }

    override fun getItemCount() = students.size
}

// New Student Activity
class NewStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_student)

        findViewById<Button>(R.id.btnAddStudent).setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString()
            val id = findViewById<EditText>(R.id.etId).text.toString()

            if (name.isNotBlank() && id.isNotBlank()) {
                StudentDatabase.students.add(Student(id, name))
                Toast.makeText(this, "Student added successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// Student Details Activity
class StudentDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)

        val studentId = intent.getStringExtra("studentId")
        val student = StudentDatabase.students.find { it.id == studentId }

        if (student != null) {
            findViewById<TextView>(R.id.tvName).text = student.name
            findViewById<TextView>(R.id.tvId).text = student.id

            findViewById<Button>(R.id.btnEditStudent).setOnClickListener {
                val intent = Intent(this, EditStudentActivity::class.java)
                intent.putExtra("studentId", student.id)
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}

// Edit Student Activity
class EditStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student)

        val studentId = intent.getStringExtra("studentId")
        val student = StudentDatabase.students.find { it.id == studentId }

        if (student != null) {
            val etName = findViewById<EditText>(R.id.etName)
            val etId = findViewById<EditText>(R.id.etId)

            etName.setText(student.name)
            etId.setText(student.id)

            findViewById<Button>(R.id.btnUpdateStudent).setOnClickListener {
                student.name = etName.text.toString()
                student.id = etId.text.toString()
                Toast.makeText(this, "Student updated successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }

            findViewById<Button>(R.id.btnDeleteStudent).setOnClickListener {
                StudentDatabase.students.remove(student)
                Toast.makeText(this, "Student deleted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Student not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}