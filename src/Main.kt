import org.jdatepicker.impl.JDatePanelImpl
import org.jdatepicker.impl.JDatePickerImpl
import org.jdatepicker.impl.SqlDateModel
import java.awt.*
import java.util.Properties
import javax.swing.*
import javax.swing.table.DefaultTableModel

fun main() {
    SwingUtilities.invokeLater {
        TaskApp()
    }
}

class TaskApp : JFrame("Task Manager") {
    private val taskManager = TaskManager()
    private val tableModel = DefaultTableModel(arrayOf("ID", "Título", "Descrição", "Data de Vencimento", "Prioridade", "Concluída"), 0)

    init {
        createUI()
        updateTable()
        addWindowListener(object : java.awt.event.WindowAdapter() {
            override fun windowClosing(e: java.awt.event.WindowEvent?) {
                taskManager.saveTasksToFile()
                System.exit(0)
            }
        })
    }

    private fun createUI() {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(800, 600)
        layout = BorderLayout()

        val panel = JPanel(BorderLayout())
        val formPanel = JPanel(GridLayout(0, 2, 5, 5))

        val titleField = JTextField()
        val descriptionField = JTextField()
        val priorityDropdown = JComboBox(arrayOf("1-(Pouco)", "2-(Médio)", "3-(Muito)"))

        val dateModel = SqlDateModel()
        val datePanel = JDatePanelImpl(dateModel, Properties())
        val datePicker = JDatePickerImpl(datePanel, DateLabelFormatter())

        formPanel.add(JLabel("Título:"))
        formPanel.add(titleField)
        formPanel.add(JLabel("Descrição:"))
        formPanel.add(descriptionField)
        formPanel.add(JLabel("Data de Vencimento:"))
        formPanel.add(datePicker)
        formPanel.add(JLabel("Prioridade:"))
        formPanel.add(priorityDropdown)

        val addButton = JButton("Adicionar Tarefa")
        val editButton = JButton("Editar Tarefa")
        val deleteButton = JButton("Excluir Tarefa")
        val completeButton = JButton("Marcar como Concluída")

        formPanel.add(addButton)
        formPanel.add(editButton)
        formPanel.add(deleteButton)
        formPanel.add(completeButton)

        panel.add(formPanel, BorderLayout.NORTH)

        val table = JTable(tableModel)
        panel.add(JScrollPane(table), BorderLayout.CENTER)

        val searchPanel = JPanel(GridLayout(1, 2, 5, 5))
        val searchField = JTextField()
        val searchButton = JButton("Pesquisar")
        searchPanel.add(searchField)
        searchPanel.add(searchButton)
        panel.add(searchPanel, BorderLayout.SOUTH)

        add(panel)

        addButton.addActionListener {
            taskManager.addTask(
                titleField.text,
                descriptionField.text,
                datePicker.model.value.toString(),
                priorityDropdown.selectedItem.toString()
            )
            updateTable()
        }

        editButton.addActionListener {
            val selectedRow = table.selectedRow
            if (selectedRow != -1) {
                val taskId = tableModel.getValueAt(selectedRow, 0) as Int
                taskManager.editTask(
                    taskId,
                    titleField.text,
                    descriptionField.text,
                    datePicker.model.value.toString(),
                    priorityDropdown.selectedItem.toString()
                )
                updateTable()
            }
        }

        deleteButton.addActionListener {
            val selectedRow = table.selectedRow
            if (selectedRow != -1) {
                val taskId = tableModel.getValueAt(selectedRow, 0) as Int
                taskManager.deleteTask(taskId)
                updateTable()
            }
        }

        completeButton.addActionListener {
            val selectedRow = table.selectedRow
            if (selectedRow != -1) {
                val taskId = tableModel.getValueAt(selectedRow, 0) as Int
                taskManager.completeTask(taskId)
                updateTable()
            }
        }

        searchButton.addActionListener {
            val query = searchField.text
            updateTable(query)
        }

        isVisible = true
    }

    private fun updateTable(query: String = "") {
        tableModel.setRowCount(0)
        val tasks = if (query.isEmpty()) taskManager.getTasks() else taskManager.searchTasks(query)
        tasks.forEach { task ->
            tableModel.addRow(arrayOf(
                task.id,
                task.title,
                task.description,
                task.dueDate,
                task.priority,
                if (task.isCompleted) "Sim" else "Não"
            ))
        }
    }

    class DateLabelFormatter : JFormattedTextField.AbstractFormatter() {
        private val dateFormat = java.text.SimpleDateFormat("dd-MM-yyyy")

        override fun stringToValue(text: String?): Any? {
            return dateFormat.parseObject(text)
        }

        override fun valueToString(value: Any?): String? {
            return if (value != null) {
                dateFormat.format(value)
            } else null
        }
    }
}
