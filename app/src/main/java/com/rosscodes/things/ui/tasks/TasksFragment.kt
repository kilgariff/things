package com.rosscodes.things.ui.tasks

import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.rosscodes.things.MainActivity
import com.rosscodes.things.Task
import com.rosscodes.things.databinding.FragmentTasksBinding
import com.rosscodes.things.databinding.TaskBinding
import java.time.LocalDate


class TasksFragment : Fragment() {

    private var _binding: FragmentTasksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val linearLayout: LinearLayout = binding.tasksLayout

        val taskListViewModel: TaskListViewModel by activityViewModels()
        val mainActivity = activity as MainActivity

        // Calculate today's due date string
        val todaysDate = mainActivity.getTodaysDate()
        val showTodaysTasks = arguments?.getBoolean("showTodaysTasks")
        if (showTodaysTasks != null) {
            taskListViewModel.showTodaysTasks = showTodaysTasks
        } else {
            taskListViewModel.showTodaysTasks = false
        }

        taskListViewModel.tasks.observe(viewLifecycleOwner, Observer { tasks ->

            linearLayout.removeAllViews()

            for (task in tasks) {

                if (taskListViewModel.showTodaysTasks != (task.dueDateAndTime == todaysDate)) {
                    continue
                }

                val taskView = TaskBinding.inflate(inflater, linearLayout, true)

                taskView.taskName.setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        mainActivity.lastChangedTaskId = task.id
                        println("Last changed: ${task.id}")
                    }
                }

                taskView.taskName.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (s.isNullOrEmpty()) {
                            // Do nothing.
                        } else {
                            val updatedTask: Task = task
                            updatedTask.description = s.toString()
                            mainActivity.dataManager.updateTask(task)
                        }
                    }
                })

                taskView.taskDue.setOnClickListener { view ->
                    mainActivity.lastChangedTaskId = task.id
                    mainActivity.showDatePicker(view)
                }

                taskView.taskDue.text = task.dueDateAndTime

                val textView = taskView.taskName
                textView.setText(task.description)

                if (task.id == mainActivity.justAddedTaskId) {
                    mainActivity.justAddedTaskId = 0
                    textView.requestFocus()

                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm?.showSoftInput(textView, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}