data class Task(
    var id: Int,
    var title: String,
    var description: String,
    var dueDate: String,
    var priority: String,
    var isCompleted: Boolean
)

class TaskManager {
    private val tasks = mutableListOf<Task>()
    private var nextId = 1

    fun addTask(title: String, description: String, dueDate: String, priority: String) {
        tasks.add(Task(nextId++, title, description, dueDate, priority, false))
    }

    fun editTask(id: Int, newTitle: String, newDescription: String, newDueDate: String, newPriority: String) {
        val task = tasks.find { it.id == id }
        task?.let {
            it.title = newTitle
            it.description = newDescription
            it.dueDate = newDueDate
            it.priority = newPriority
        }
    }

    fun deleteTask(id: Int) {
        tasks.removeAll { it.id == id }
    }

    fun completeTask(id: Int) {
        val task = tasks.find { it.id == id }
        task?.isCompleted = true
    }

    fun getTasks(): List<Task> = tasks

    fun searchTasks(query: String): List<Task> {
        return tasks.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    }
}
