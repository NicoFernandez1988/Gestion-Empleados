package com.Empleados.Employees.application;


import com.Empleados.Employees.application.exceptions.TaskNotFoundException;
import com.Empleados.Employees.domain.Task;
import com.Empleados.Employees.domain.TaskCategory;
import com.Empleados.Employees.domain.dto.TaskDTO;
import com.Empleados.Employees.infrastructure.input.port.TaskInputPort;
import com.Empleados.Employees.infrastructure.output.port.TaskCategoryOutputRepository;
import com.Empleados.Employees.infrastructure.output.port.TaskRepositoryOuputPort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskService implements TaskInputPort {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);


    private final TaskRepositoryOuputPort taskRepositoryOuputPort;

    private final TaskCategoryOutputRepository taskCategoryOutputRepository;

    public TaskService(TaskRepositoryOuputPort taskRepositoryOuputPort, TaskCategoryOutputRepository taskCategoryOutputRepository) {
        this.taskRepositoryOuputPort = taskRepositoryOuputPort;
        this.taskCategoryOutputRepository = taskCategoryOutputRepository;
    }


    @Override
    public List<TaskDTO> getAllTasks() {
        logger.info("Fetching all tasks");
        return taskRepositoryOuputPort.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO getTaskById(Long id) throws TaskNotFoundException {
        logger.info("Fetching task with id: {}", id);
        return taskRepositoryOuputPort.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @Override
    public void saveTask(TaskDTO taskDTO) {
        logger.info("Creating task");
        Task task = convertToEntity(taskDTO);
        taskRepositoryOuputPort.save(task);
    }

    @Override
    public Map<String, List<TaskDTO>> getCategorizedTasks() {
        logger.info("Fetching categorized tasks");
        List<TaskDTO> allTasks = getAllTasks();
        return Map.of(
                "Pc de escritorio:", filterTasksByCategory(allTasks, "Pc de escritorio"),
                "Notebook:", filterTasksByCategory(allTasks, "Notebook"),
                "Celulares:", filterTasksByCategory(allTasks, "Celulares"),
                "Atencion al cliente:", filterTasksByCategory(allTasks, "Atencion al cliente")
        );
    }

    private List<TaskDTO> filterTasksByCategory(List<TaskDTO> tasks, String category) {
        logger.info("Creating task with category: {}", category);
        return tasks.stream()
                .filter(task -> category.equals(getCategoryNameById(task.getCategoryId())))
                .collect(Collectors.toList());
    }

    private String getCategoryNameById(Long categoryId) {
        logger.info("Fetching category with id: {}", categoryId);
        return taskCategoryOutputRepository.findById(categoryId)
                .map(TaskCategory::getName)
                .orElse(null);
    }

    public TaskDTO convertToDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setDescription(task.getDescription());
        dto.setCategoryId(task.getCategory() != null ? task.getCategory().getId() : null);
        return dto;
    }

    public Task convertToEntity(TaskDTO dto) {
        Task task = new Task();
        task.setId(dto.getId());
        task.setDescription(dto.getDescription());
        task.setCategory(dto.getCategoryId() != null ? taskCategoryOutputRepository.findById(dto.getCategoryId()).orElse(null) : null);
        return task;
    }
}
