package com.example

import com.example.models.Task
import com.example.models.TaskRequest
import com.example.repositories.TaskRepository
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class ApplicationTest {

    @BeforeTest
    fun setup() {
        TaskRepository.clear()
    }

    @Test
    fun testRootEndpoint() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World!", response.bodyAsText())
    }

    @Test
    fun testTasksCrudLifecycle() = testApplication {
        application {
            module()
        }

        // 1. GET /tasks should be empty initially
        val initialResponse = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, initialResponse.status)
        assertEquals("[]", initialResponse.bodyAsText())

        // 2. POST /tasks to create a task
        val taskRequest = TaskRequest(content = "Buy milk", isDone = false)
        val createResponse = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(taskRequest))
        }
        assertEquals(HttpStatusCode.Created, createResponse.status)
        
        val createdTask = Json.decodeFromString<Task>(createResponse.bodyAsText())
        assertEquals(1, createdTask.id)
        assertEquals("Buy milk", createdTask.content)
        assertFalse(createdTask.isDone)

        // 3. GET /tasks should contain the created task
        val getTasksResponse = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, getTasksResponse.status)
        val tasksList = Json.decodeFromString<List<Task>>(getTasksResponse.bodyAsText())
        assertEquals(1, tasksList.size)
        assertEquals(createdTask, tasksList[0])

        // 4. GET /tasks/1 should return the task
        val getTaskResponse = client.get("/tasks/1")
        assertEquals(HttpStatusCode.OK, getTaskResponse.status)
        val retrievedTask = Json.decodeFromString<Task>(getTaskResponse.bodyAsText())
        assertEquals(createdTask, retrievedTask)

        // 5. GET /tasks/999 should return 404 Not Found
        val getNonExistentResponse = client.get("/tasks/999")
        assertEquals(HttpStatusCode.NotFound, getNonExistentResponse.status)

        // 6. PUT /tasks/1 should update the task
        val updateRequest = TaskRequest(content = "Buy organic milk", isDone = true)
        val updateResponse = client.put("/tasks/1") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(updateRequest))
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)
        assertEquals("Task updated successfully", updateResponse.bodyAsText())

        // Verify update persisted
        val getUpdatedResponse = client.get("/tasks/1")
        assertEquals(HttpStatusCode.OK, getUpdatedResponse.status)
        val updatedTask = Json.decodeFromString<Task>(getUpdatedResponse.bodyAsText())
        assertEquals(1, updatedTask.id)
        assertEquals("Buy organic milk", updatedTask.content)
        assertTrue(updatedTask.isDone)

        // 7. DELETE /tasks/1 should delete the task
        val deleteResponse = client.delete("/tasks/1")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        // Verify task was deleted
        val getDeletedResponse = client.get("/tasks/1")
        assertEquals(HttpStatusCode.NotFound, getDeletedResponse.status)

        val getTasksAfterDeleteResponse = client.get("/tasks")
        assertEquals(HttpStatusCode.OK, getTasksAfterDeleteResponse.status)
        assertEquals("[]", getTasksAfterDeleteResponse.bodyAsText())
    }

    @Test
    fun testInvalidRequests() = testApplication {
        application {
            module()
        }

        // POST /tasks with invalid payload
        val invalidPostResponse = client.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody("invalid-json")
        }
        assertEquals(HttpStatusCode.BadRequest, invalidPostResponse.status)

        // GET /tasks/abc with invalid ID type
        val invalidIdResponse = client.get("/tasks/abc")
        assertEquals(HttpStatusCode.BadRequest, invalidIdResponse.status)

        // PUT /tasks/abc with invalid ID type
        val invalidPutIdResponse = client.put("/tasks/abc") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(TaskRequest("test", true)))
        }
        assertEquals(HttpStatusCode.BadRequest, invalidPutIdResponse.status)

        // DELETE /tasks/abc with invalid ID type
        val invalidDeleteIdResponse = client.delete("/tasks/abc")
        assertEquals(HttpStatusCode.BadRequest, invalidDeleteIdResponse.status)
    }
}
