package com.rosscodes.things.ui.tasks
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rosscodes.things.Task

class TaskListViewModel : ViewModel() {

    var showTodaysTasks: Boolean = false

    val tasks: MutableLiveData<List<Task>> by lazy {
        MutableLiveData<List<Task>>()
    }
}