package com.rosscodes.things

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.rosscodes.things.Task

class DataManager(context: Context) {
    private val db : SQLiteDatabase = context.openOrCreateDatabase("Things", Context.MODE_PRIVATE, null)

    fun init() {
        val tasksCreateQuery = "CREATE TABLE IF NOT EXISTS `Tasks` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `desc` TEXT NOT NULL, `due` TEXT)"
        db.execSQL(tasksCreateQuery)
    }

    fun add(task: Task) : Long {
        val values = ContentValues().apply {
            put("desc", task.description)
            put("due", task.dueDateAndTime)
        }
        return db.insert("Tasks", null, values)
    }

    fun allTasks() : List<Task> {

        val tasks = mutableListOf<Task>();

        val cursor = db.rawQuery("SELECT * FROM Tasks", null);
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow("desc"))
                val due = cursor.getString(cursor.getColumnIndexOrThrow("due"))
                val task = Task(id, desc, due)
                println("Got task ${id}: ${desc}")
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tasks
    }

    fun updateTask(task: Task) {
        val query = "UPDATE `Tasks` SET `desc` = '${task.description}', `due`='${task.dueDateAndTime}' WHERE `id` = ${task.id}"
        db.execSQL(query)
    }

    fun countTasksWithId(taskId: Long) {
        val countQuery = "SELECT COUNT(*) FROM `Tasks` WHERE `id` = $taskId"
        val cursor = db.rawQuery(countQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val count = cursor.getInt(cursor.getColumnIndexOrThrow("COUNT(*)"))
                println(count)
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    fun removeTask(taskId: Long) {

        countTasksWithId(taskId)
        val query = "DELETE FROM `Tasks` WHERE `id` = $taskId"
        println(query)
        db.execSQL(query)
        countTasksWithId(taskId)
    }
}