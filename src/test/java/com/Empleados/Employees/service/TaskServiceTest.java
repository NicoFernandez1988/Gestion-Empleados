package com.Empleados.Employees.service;

import com.Empleados.Employees.application.TaskService;
import com.Empleados.Employees.application.exceptions.TaskNotFoundException;
import com.Empleados.Employees.domain.Employee;
import com.Empleados.Employees.domain.Task;
import com.Empleados.Employees.domain.TaskCategory;
import com.Empleados.Employees.domain.dto.TaskDTO;
import com.Empleados.Employees.infrastructure.output.port.TaskRepositoryOuputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepositoryOuputPort taskRepositoryOuputPort;

    @InjectMocks
    private TaskService taskService;

    @Test
    @DisplayName("Obtiene una tarea por Id")
    void getTask_withId_returnTask() throws TaskNotFoundException  {
        Task task = createTestTask();

        when(taskRepositoryOuputPort.findById(1L)).thenReturn(Optional.of(task));

        TaskDTO result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Devuelve un TaskNotFoundException cuando no se encuentra una tarea por el id solicitado")
    void getTask_withInvalidId_returnNotFoundTask() throws TaskNotFoundException {
        when(taskRepositoryOuputPort.findById(100L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTaskById(100L);
        });
        assertEquals("Task not found with id: 100", exception.getMessage());
    }


    private static TaskDTO createTestTaskDTO() {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setDescription("Notebook");
        taskDTO.setCategoryId(1L);
        return taskDTO;
    }

    private static Task createTestExistingTask(Long id) {
        Task task = new Task();
        task.setId(id);
        task.setDescription("Notebook");

        TaskCategory category = new TaskCategory();
        category.setId(1L);
        task.setCategory(category);

        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setName("John Doe");

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setName("Jane Smith");

        List<Employee> employees = new ArrayList<>();
        employees.add(employee1);
        employees.add(employee2);

        task.setEmployees(employees);

        return task;
    }


    private static Task createTestUpdatedTask(Long id) {
        Task task = new Task();
        task.setId(id);
        task.setDescription("Cellphones");

        TaskCategory category = new TaskCategory();
        category.setId(1L);
        task.setCategory(category);

        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setName("Jane Doe");

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setName("John Smith");

        List<Employee> employees = new ArrayList<>();
        employees.add(employee1);
        employees.add(employee2);

        task.setEmployees(employees);

        return task;
    }

    private static Task createTestTask() {
        Task task = new Task();
        task.setId(1L);
        task.setDescription("Notebook");
        return task;
    }

}
