package com.rosscodes.things

import android.icu.util.Calendar
import android.os.Bundle
import android.view.Menu
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rosscodes.things.databinding.ActivityMainBinding
import com.rosscodes.things.databinding.TaskBinding
import com.rosscodes.things.ui.tasks.DatePickerFragment
import com.rosscodes.things.ui.tasks.TaskListViewModel
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var dataManager: DataManager
    var lastChangedTaskId: Long = -1
    var justAddedTaskId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataManager = DataManager(this)
        dataManager.init()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val taskListViewModel = ViewModelProvider(this).get(TaskListViewModel::class.java)

        binding.appBarMain.fabAdd.setOnClickListener { view ->

            var date = ""
            if (taskListViewModel.showTodaysTasks) {
                date = getTodaysDate()
            }
            justAddedTaskId = dataManager.add(Task(0, "Task", date))
            val tasks = dataManager.allTasks()
            taskListViewModel.tasks.value = tasks
        }

        binding.appBarMain.fabRemove.setOnClickListener { view ->

            if (lastChangedTaskId != -1L) {
                dataManager.removeTask(lastChangedTaskId)
                val tasks = dataManager.allTasks()
                taskListViewModel.tasks.value = tasks
            }
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_now, R.id.nav_later), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Pull initial task list:
        taskListViewModel.tasks.value = dataManager.allTasks()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun showDatePicker (v: View) {
        DatePickerFragment().show(supportFragmentManager, "timePicker")
    }

    fun getTodaysDate() : String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return LocalDate.of(year, month + 1, day).toString()
    }

    fun setDueDateOnCurrentTask(year: Int, month: Int, day: Int) {
        val tasks = dataManager.allTasks()
        for (task in tasks) {
            if (task.id == lastChangedTaskId) {
                task.dueDateAndTime = LocalDate.of(year, month + 1, day).toString()
                dataManager.updateTask(task)
                break;
            }
        }

        val taskListViewModel = ViewModelProvider(this).get(TaskListViewModel::class.java)
        taskListViewModel.tasks.value = dataManager.allTasks()
    }
}