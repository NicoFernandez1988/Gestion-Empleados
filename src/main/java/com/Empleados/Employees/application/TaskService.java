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
        try {
            return taskRepositoryOuputPort.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }catch (Exception e){
            logger.error("Error fetching tasks. {}", e.getMessage());
            throw e;
        }

    }

    @Override
    public TaskDTO getTaskById(Long id) throws TaskNotFoundException {
        logger.info("Fetching task with id: {}", id);
        try {
            return taskRepositoryOuputPort.findById(id)
                    .map(this::convertToDTO)
                    .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
        }catch (TaskNotFoundException e){
            logger.error("Error fetching task: {}", e.getMessage());
            throw e;
        }

    }

    @Override
    public void saveTask(TaskDTO taskDTO) {
        logger.info("Creating task");
        try {
            Task task = convertToEntity(taskDTO);
            taskRepositoryOuputPort.save(task);
        }catch (Exception e) {
            logger.error("An unexpected error occurred during the save task operation: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while saving task", e);
        }

    }

    @Override
    public Map<String, List<TaskDTO>> getCategorizedTasks() {
        logger.info("Fetching categorized tasks");
        try {
            List<TaskDTO> allTasks = getAllTasks();
            return Map.of(
                    "Pc de escritorio:", filterTasksByCategory(allTasks, "Pc de escritorio"),
                    "Notebook:", filterTasksByCategory(allTasks, "Notebook"),
                    "Celulares:", filterTasksByCategory(allTasks, "Celulares"),
                    "Atencion al cliente:", filterTasksByCategory(allTasks, "Atencion al cliente")
            );
        }catch (Exception e){
            logger.error("Error fetching categorized tasks: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while fetching categorized tasks", e);
        }

    }

    private List<TaskDTO> filterTasksByCategory(List<TaskDTO> tasks, String category) {
        logger.info("Creating task with category: {}", category);
        try {
            return tasks.stream()
                    .filter(task -> category.equals(getCategoryNameById(task.getCategoryId())))
                    .collect(Collectors.toList());
        }catch (Exception e){
            logger.error("Error creating task with category: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while creating categorized task", e);
        }

    }

    private String getCategoryNameById(Long categoryId) {
        logger.info("Fetching category with id: {}", categoryId);
        try {
            return taskCategoryOutputRepository.findById(categoryId)
                    .map(TaskCategory::getName)
                    .orElse(null);
        }catch (Exception e){
            logger.error("Error fetching category: {}", e.getMessage());
            throw new RuntimeException("Error occurred fetching category with id", e);
        }

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
