package com.Empleados.Employees.infrastructure.input.port;

import com.Empleados.Employees.application.exceptions.TaskNotFoundException;
import com.Empleados.Employees.domain.dto.TaskDTO;
import java.util.List;
import java.util.Map;

public interface TaskInputPort {

    List<TaskDTO> getAllTasks();

    TaskDTO getTaskById(Long id) throws TaskNotFoundException;

    Map<String, List<TaskDTO>> getCategorizedTasks();

    void saveTask(TaskDTO taskDTO);

}
