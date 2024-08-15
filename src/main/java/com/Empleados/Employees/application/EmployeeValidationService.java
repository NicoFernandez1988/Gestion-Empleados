package com.Empleados.Employees.application;
import com.Empleados.Employees.application.exceptions.EmployeeDuplicateException;
import com.Empleados.Employees.domain.Employee;
import com.Empleados.Employees.domain.dto.EmployeeDTO;
import com.Empleados.Employees.infrastructure.output.port.EmployeeRepositoryOutputPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeValidationService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepositoryOutputPort employeeRepositoryOutputPort;

    public EmployeeValidationService(EmployeeRepositoryOutputPort employeeRepositoryOutputPort) {
        this.employeeRepositoryOutputPort = employeeRepositoryOutputPort;
    }

    public void validateEmployeeNameAndLastName(EmployeeDTO employeeDTO) throws EmployeeDuplicateException {
        logger.info("Fetching employee with id: {}", employeeDTO.getId());
        try {
            Optional<Employee> existingEmployee = employeeRepositoryOutputPort.findByNameAndLastName(employeeDTO.getName(), employeeDTO.getLastName());
            if (existingEmployee.isPresent() && !existingEmployee.get().getId().equals(employeeDTO.getId())) {
                throw new EmployeeDuplicateException("Already exists an employee with the same name and last name.");
            }
        }catch (EmployeeDuplicateException e) {
            logger.error("Duplicate employee detected during the update operation: {}", e.getMessage());
            throw e;
        }
    }
}