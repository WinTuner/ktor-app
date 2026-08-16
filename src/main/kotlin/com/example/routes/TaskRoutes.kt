package com.example.routes

import com.example.models.TaskRequest
import com.example.repositories.TaskRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRouting() {
    route("/tasks") {
        // GET /tasks
        get {
            call.respond(HttpStatusCode.OK, TaskRepository.getAll())
        }

        // GET /tasks/{id}
        get("{id?}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing or malformed id")
                return@get
            }
            val task = TaskRepository.getById(id)
            if (task == null) {
                call.respond(HttpStatusCode.NotFound, "No task with id $id")
                return@get
            }
            call.respond(HttpStatusCode.OK, task)
        }

        // POST /tasks
        post {
            try {
                val request = call.receive<TaskRequest>()
                val createdTask = TaskRepository.add(request)
                call.respond(HttpStatusCode.Created, createdTask)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid payload")
            }
        }

        // PUT /tasks/{id}
        put("{id?}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing or malformed id")
                return@put
            }
            try {
                val request = call.receive<TaskRequest>()
                val updated = TaskRepository.update(id, request)
                if (updated) {
                    call.respond(HttpStatusCode.OK, "Task updated successfully")
                } else {
                    call.respond(HttpStatusCode.NotFound, "No task with id $id")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid payload")
            }
        }

        // DELETE /tasks/{id}
        delete("{id?}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing or malformed id")
                return@delete
            }
            val deleted = TaskRepository.delete(id)
            if (deleted) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound, "No task with id $id")
            }
        }
    }
}
