package com.example.note

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private lateinit var todos: MutableList<String>

    private lateinit var sharedPreferences: SharedPreferences
    private val TASKS_KEY = "tasks_list"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)

        val listView: ListView = findViewById(R.id.list_view)
        val userData: EditText = findViewById(R.id.user_data)
        val button: Button = findViewById(R.id.button)

        // Загрузка сохранённых задач — присваиваем результат полю класса
        val savedTasksJson = sharedPreferences.getString(TASKS_KEY, null)
        todos = if (savedTasksJson != null) {
            val type = object : TypeToken<MutableList<String>>() {}.type
            Gson().fromJson(savedTasksJson, type)
        } else {
            mutableListOf()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, todos)
        listView.adapter = adapter

        listView.setOnItemClickListener { adapterView, view, i, l ->
            val text = listView.getItemAtPosition(i).toString()
            adapter.remove(text)
            saveTasks() // Сохранение после удаления
            Toast.makeText(this, "Мы удалили: $text", Toast.LENGTH_SHORT).show()
        }

        button.setOnClickListener {
            val text = userData.text.toString().trim()
            if (text != "") {
                adapter.insert(text, 0)
                saveTasks() // Сохранение после добавления
                userData.text.clear() // Очистка поля ввода
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun saveTasks() {
        val json = Gson().toJson(todos)
        sharedPreferences.edit().putString(TASKS_KEY, json).apply()
    }
}
