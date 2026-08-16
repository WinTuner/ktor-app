package com.example.repositories

import com.example.models.Task
import com.example.models.TaskRequest
import java.util.concurrent.atomic.AtomicInteger

object TaskRepository {
    private val tasks = mutableListOf<Task>()
    private val idCounter = AtomicInteger(1)

    fun getAll(): List<Task> {
        return tasks.toList()
    }

    fun getById(id: Int): Task? {
        return tasks.find { it.id == id }
    }

    fun add(request: TaskRequest): Task {
        val newTask = Task(
            id = idCounter.getAndIncrement(),
            content = request.content,
            isDone = request.isDone
        )
        tasks.add(newTask)
        return newTask
    }

    fun add(task: Task) {
        tasks.add(task)
    }

    fun update(id: Int, updatedTask: TaskRequest): Boolean {
        val index = tasks.indexOfFirst { it.id == id }
        if (index == -1) return false
        
        tasks[index] = Task(
            id = id,
            content = updatedTask.content,
            isDone = updatedTask.isDone
        )
        return true
    }

    fun delete(id: Int): Boolean {
        return tasks.removeIf { it.id == id }
    }

    fun clear() {
        tasks.clear()
        idCounter.set(1)
    }
}
