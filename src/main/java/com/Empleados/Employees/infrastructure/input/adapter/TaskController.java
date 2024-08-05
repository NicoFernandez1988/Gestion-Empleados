package com.Empleados.Employees.infrastructure.input.adapter;

import com.Empleados.Employees.application.TaskService;
import com.Empleados.Employees.domain.dto.TaskDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/all")
    @Operation(summary = "Obtener todas las Tareas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Obtener todas las tareas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    public List<TaskDTO> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/categorized")
    @Operation(summary = "Obtener todas las tareas categorizadas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Obtener todas las tareas categorizadas",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    public Map<String, List<TaskDTO>> getTasksByCategory() {
        return taskService.getCategorizedTasks();
    }
}
