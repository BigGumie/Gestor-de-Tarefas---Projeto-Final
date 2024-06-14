import java.io.File

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

    init {
        loadTasksFromFile()
    }

    fun addTask(title: String, description: String, dueDate: String, priority: String) {
        tasks.add(Task(nextId++, title, description, dueDate, priority, false))
        saveTasksToFile()
    }

    fun editTask(id: Int, newTitle: String, newDescription: String, newDueDate: String, newPriority: String) {
        val task = tasks.find { it.id == id }
        task?.let {
            it.title = newTitle
            it.description = newDescription
            it.dueDate = newDueDate
            it.priority = newPriority
        }
        saveTasksToFile()
    }

    fun deleteTask(id: Int) {
        tasks.removeAll { it.id == id }
        saveTasksToFile()
    }

    fun completeTask(id: Int) {
        val task = tasks.find { it.id == id }
        task?.isCompleted = true
        saveTasksToFile()
    }

    fun getTasks(): List<Task> = tasks

    fun searchTasks(query: String): List<Task> {
        return tasks.filter { it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    }

    fun sortById(ascending: Boolean): List<Task> {
        return if (ascending) tasks.sortedBy { it.id } else tasks.sortedByDescending { it.id }
    }

    fun sortByDueDate(ascending: Boolean): List<Task> {
        return if (ascending) tasks.sortedBy { it.dueDate } else tasks.sortedByDescending { it.dueDate }
    }

    fun sortByPriority(ascending: Boolean): List<Task> {
        return if (ascending) tasks.sortedBy { it.priority } else tasks.sortedByDescending { it.priority }
    }

    fun saveTasksToFile() {
        val file = File("tasks.txt")
        file.bufferedWriter().use { out ->
            tasks.forEach { task ->
                out.write("${task.id},${task.title},${task.description},${task.dueDate},${task.priority},${task.isCompleted}\n")
            }
        }
    }

    private fun loadTasksFromFile() {
        val file = File("tasks.txt")
        if (file.exists()) {
            file.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parts = line.split(",")
                    if (parts.size == 6) {
                        val task = Task(
                            id = parts[0].toInt(),
                            title = parts[1],
                            description = parts[2],
                            dueDate = parts[3],
                            priority = parts[4],
                            isCompleted = parts[5].toBoolean()
                        )
                        tasks.add(task)
                        if (task.id >= nextId) {
                            nextId = task.id + 1
                        }
                    }
                }
            }
        }
    }
}
