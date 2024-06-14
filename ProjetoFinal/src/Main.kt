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
    }

    private fun createUI() {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(800, 600)
        layout = BorderLayout()

        val panel = JPanel(BorderLayout())
        val formPanel = JPanel(GridLayout(0, 2, 5, 5))

        val titleField = JTextField()
        val descriptionField = JTextField()
        val priorityField = JTextField()

        // Configurar JDatePicker
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
        formPanel.add(priorityField)

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

        add(panel)

        addButton.addActionListener {
            taskManager.addTask(
                titleField.text,
                descriptionField.text,
                datePicker.model.value.toString(),
                priorityField.text
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
                    priorityField.text
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

        isVisible = true
    }

    private fun updateTable() {
        tableModel.setRowCount(0)
        taskManager.getTasks().forEach { task ->
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

    // Formatter para o JDatePicker
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
