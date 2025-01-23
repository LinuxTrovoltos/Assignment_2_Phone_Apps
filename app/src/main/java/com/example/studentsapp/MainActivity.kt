package com.example.studentsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Data class to represent a student
data class Student(var name: String, var id: String, var isChecked: Boolean)

// In-memory database object
object StudentDatabase {
    val students = mutableListOf<Student>()
}

// Main Activity: Displays the list of students
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = StudentsAdapter(StudentDatabase.students) { student ->
            val intent = Intent(this, StudentDetailsActivity::class.java)
            intent.putExtra("studentIndex", StudentDatabase.students.indexOf(student))
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.addStudentButton).setOnClickListener {
            startActivity(Intent(this, NewStudentActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        (findViewById<RecyclerView>(R.id.recyclerView).adapter as StudentsAdapter).notifyDataSetChanged()
    }
}

// Activity to add a new student
class NewStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_student)

        findViewById<Button>(R.id.saveButton).setOnClickListener {
            val name = findViewById<EditText>(R.id.nameInput).text.toString()
            val id = findViewById<EditText>(R.id.idInput).text.toString()
            if (name.isNotBlank() && id.isNotBlank()) {
                StudentDatabase.students.add(Student(name, id, false))
                finish()
            }
        }
    }
}

// Activity to display student details
class StudentDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)

        val studentIndex = intent.getIntExtra("studentIndex", -1)
        val student = StudentDatabase.students[studentIndex]

        findViewById<TextView>(R.id.nameDetail).text = student.name
        findViewById<TextView>(R.id.idDetail).text = student.id
        findViewById<ImageView>(R.id.studentImage).setImageResource(R.drawable.student_pic)

        findViewById<Button>(R.id.editButton).setOnClickListener {
            val intent = Intent(this, EditStudentActivity::class.java)
            intent.putExtra("studentIndex", studentIndex)
            startActivity(intent)
        }
    }
}


// Adapter for the RecyclerView
class StudentsAdapter(
    private val students: List<Student>,
    private val onClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentsAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.studentName)
        val idView: TextView = itemView.findViewById(R.id.studentId)
        val checkBox: CheckBox = itemView.findViewById(R.id.studentCheckbox)

        init {
            itemView.setOnClickListener {
                onClick(students[adapterPosition])
            }
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                students[adapterPosition].isChecked = isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): StudentViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.nameView.text = student.name
        holder.idView.text = student.id
        holder.checkBox.isChecked = student.isChecked
    }

    override fun getItemCount() = students.size
}

// Activity to edit a student
class EditStudentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_student)

        val studentIndex = intent.getIntExtra("studentIndex", -1)
        val student = StudentDatabase.students[studentIndex]

        val nameInput = findViewById<EditText>(R.id.editNameInput)
        val idInput = findViewById<EditText>(R.id.editIdInput)
        nameInput.setText(student.name)
        idInput.setText(student.id)

        findViewById<Button>(R.id.updateButton).setOnClickListener {
            student.name = nameInput.text.toString()
            student.id = idInput.text.toString()
            finish()
        }

        findViewById<Button>(R.id.deleteButton).setOnClickListener {
            StudentDatabase.students.removeAt(studentIndex)
            finish()
        }
    }
}
