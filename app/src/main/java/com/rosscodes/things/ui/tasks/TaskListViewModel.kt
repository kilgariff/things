package com.rosscodes.things.ui.tasks
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rosscodes.things.Task

class TaskListViewModel : ViewModel() {

    val tasks: MutableLiveData<List<Task>> by lazy {
        MutableLiveData<List<Task>>()
    }
}