package com.Empleados.Employees.application;

import com.Empleados.Employees.application.exceptions.EmployeeNotFoundException;
import com.Empleados.Employees.domain.Employee;
import com.Empleados.Employees.domain.Task;
import com.Empleados.Employees.domain.dto.EmployeeDTO;
import com.Empleados.Employees.infrastructure.output.port.EmployeeRepositoryOutputPort;
import com.Empleados.Employees.infrastructure.output.port.TaskRepositoryOuputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeAssignmentService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeAssignmentService.class);

    private final EmployeeRepositoryOutputPort employeeRepositoryOutputPort;

    private final TaskRepositoryOuputPort taskRepositoryOuputPort;

    public EmployeeAssignmentService(EmployeeRepositoryOutputPort employeeRepositoryOutputPort, TaskRepositoryOuputPort taskRepositoryOuputPort) {
        this.employeeRepositoryOutputPort = employeeRepositoryOutputPort;
        this.taskRepositoryOuputPort = taskRepositoryOuputPort;
    }

    public void assignSupervisor(EmployeeDTO employeeDTO) throws EmployeeNotFoundException {
        logger.info("Assigning supervisor to employee");
        if (employeeDTO.getSupervisorId() != null) {
            Employee supervisor = employeeRepositoryOutputPort.findById(employeeDTO.getSupervisorId())
                    .orElseThrow(() -> new EmployeeNotFoundException("Supervisor not found"));
            employeeDTO.setSupervisorId(supervisor.getId());
        } else {
            employeeDTO.setSupervisorId(null);
        }
    }

    public void assignTasks(EmployeeDTO employeeDTO) {
        logger.info("Assigning tasks to employee");
        if (employeeDTO.getTaskIds() != null && !employeeDTO.getTaskIds().isEmpty()) {
            List<Task> tasks = taskRepositoryOuputPort.findAllById(employeeDTO.getTaskIds());
            employeeDTO.setTaskIds(tasks.stream().map(Task::getId).collect(Collectors.toList()));
        } else {
            employeeDTO.setTaskIds(new ArrayList<>());
        }
    }
}
