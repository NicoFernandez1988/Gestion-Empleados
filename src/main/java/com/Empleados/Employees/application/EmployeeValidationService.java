package com.Empleados.Employees.application;
import com.Empleados.Employees.application.exceptions.EmployeeDuplicateException;
import com.Empleados.Employees.domain.Employee;
import com.Empleados.Employees.domain.dto.EmployeeDTO;
import com.Empleados.Employees.infrastructure.output.port.EmployeeRepositoryOutputPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeValidationService {


    private final EmployeeRepositoryOutputPort employeeRepositoryOutputPort;

    public EmployeeValidationService(EmployeeRepositoryOutputPort employeeRepositoryOutputPort) {
        this.employeeRepositoryOutputPort = employeeRepositoryOutputPort;
    }

    public void validateEmployeeNameAndLastName(EmployeeDTO employeeDTO) throws EmployeeDuplicateException {
        Optional<Employee> existingEmployee = employeeRepositoryOutputPort.findByNameAndLastName(employeeDTO.getName(), employeeDTO.getLastName());
        if (existingEmployee.isPresent() && !existingEmployee.get().getId().equals(employeeDTO.getId())) {
            throw new EmployeeDuplicateException("Already exists an employee with the same name and last name.");
        }
    }
}