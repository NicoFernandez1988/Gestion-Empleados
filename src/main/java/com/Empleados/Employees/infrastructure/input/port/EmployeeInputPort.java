package com.Empleados.Employees.infrastructure.input.port;

import com.Empleados.Employees.application.exceptions.EmployeeDuplicateException;
import com.Empleados.Employees.application.exceptions.EmployeeNotFoundException;
import com.Empleados.Employees.domain.Employee;
import com.Empleados.Employees.domain.dto.EmployeeDTO;

import java.util.List;

public interface EmployeeInputPort {

    Employee saveEmployee(EmployeeDTO employeeDTO) throws EmployeeNotFoundException, EmployeeDuplicateException;

    List<Employee> getAllEmployees();

    Employee getEmployeeById(Long id) throws EmployeeNotFoundException;

    Employee updateEmployee(Long id, EmployeeDTO employeeDTO) throws EmployeeNotFoundException, EmployeeDuplicateException;

    void deleteEmployee(Long id) throws EmployeeNotFoundException;

}
