package com.Empleados.Employees.application;
import com.Empleados.Employees.application.exceptions.EmployeeDuplicateException;
import com.Empleados.Employees.application.exceptions.EmployeeNotFoundException;
import com.Empleados.Employees.domain.Employee;
import com.Empleados.Employees.domain.Task;
import com.Empleados.Employees.domain.dto.EmployeeDTO;
import com.Empleados.Employees.infrastructure.input.port.EmployeeInputPort;
import com.Empleados.Employees.infrastructure.output.port.EmployeeRepositoryOutputPort;
import com.Empleados.Employees.infrastructure.output.port.TaskRepositoryOuputPort;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;


@Service
public class EmployeeService implements EmployeeInputPort {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepositoryOutputPort employeeRepositoryOutputPort;

    private final EmployeeValidationService employeeValidationService;

    private final EmployeeAssignmentService employeeAssignmentService;

    private final TaskRepositoryOuputPort taskRepositoryOuputPort;

    public EmployeeService(EmployeeRepositoryOutputPort employeeRepositoryOutputPort, EmployeeValidationService employeeValidationService, EmployeeAssignmentService employeeAssignmentService, TaskRepositoryOuputPort taskRepositoryOuputPort) {
        this.employeeRepositoryOutputPort = employeeRepositoryOutputPort;
        this.employeeValidationService = employeeValidationService;
        this.employeeAssignmentService = employeeAssignmentService;
        this.taskRepositoryOuputPort = taskRepositoryOuputPort;
    }

    @Override
    public Employee saveEmployee(EmployeeDTO employeeDTO) throws EmployeeNotFoundException, EmployeeDuplicateException {
        logger.info("Creating employee with name: {}", employeeDTO.getName());
        try {
            Employee employee = convertToEntity(employeeDTO);
            employeeValidationService.validateEmployeeNameAndLastName(employeeDTO);
            employeeAssignmentService.assignSupervisor(employeeDTO);
            employeeAssignmentService.assignTasks(employeeDTO);
            return employeeRepositoryOutputPort.save(employee);
        }catch (EmployeeNotFoundException e) {
            logger.error("Employee not found during the save operation: {}", e.getMessage());
            throw e;
        } catch (EmployeeDuplicateException e) {
            logger.error("Duplicate employee detected during the save operation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during the save operation: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while saving employee", e);
        }

    }

    @Override
    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");
        try {
            return employeeRepositoryOutputPort.findAll(Sort.by("name", "lastName"));
        }catch (Exception e){
            logger.error("Error fetching employees. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Employee getEmployeeById(Long id) throws EmployeeNotFoundException {
        logger.info("Fetching employee with id: {}", id);
        try {
            employeeRepositoryOutputPort.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
            return employeeRepositoryOutputPort.findById(id).orElse(null);
        }catch (EmployeeNotFoundException e){
            logger.error("Error fetching employee. {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Employee updateEmployee(Long id, EmployeeDTO employeeDTO) throws EmployeeNotFoundException, EmployeeDuplicateException  {
        logger.info("Updating employee with id: {}", id);
        try {
            Employee existingEmployee = employeeRepositoryOutputPort.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

            Employee employee = convertToEntity(employeeDTO);
            employee.setId(id);
            employeeDTO.setId(id);
            employeeValidationService.validateEmployeeNameAndLastName(employeeDTO);
            employeeAssignmentService.assignSupervisor(employeeDTO);
            employeeAssignmentService.assignTasks(employeeDTO);

            existingEmployee.setName(employee.getName());
            existingEmployee.setLastName(employee.getLastName());
            existingEmployee.setCellPhone(employee.getCellPhone());
            existingEmployee.setGender(employee.getGender());
            existingEmployee.setSupervisor(employee.getSupervisor());
            existingEmployee.setTasks(employee.getTasks());

            return employeeRepositoryOutputPort.save(existingEmployee);
        }catch (EmployeeNotFoundException e) {
            logger.error("Employee not found during the update operation: {}", e.getMessage());
            throw e;
        } catch (EmployeeDuplicateException e) {
            logger.error("Duplicate employee detected during the update operation: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("An unexpected error occurred during the update operation: {}", e.getMessage());
            throw new RuntimeException("Unexpected error occurred while saving employee", e);
        }

    }

    @Override
    public void deleteEmployee(Long id) throws EmployeeNotFoundException {
        logger.info("Deleting employee with id: {}", id);
        try {
            Employee employee = employeeRepositoryOutputPort.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found "));
            List<Employee> supervisedEmployees = employeeRepositoryOutputPort.findBySupervisor(employee);
            for (Employee e : supervisedEmployees) {
                e.setSupervisor(null);
                employeeRepositoryOutputPort.save(e);
            }
            employeeRepositoryOutputPort.delete(employee);
        }catch (EmployeeNotFoundException e){
            logger.error("Error deleting employee. {}", e.getMessage());
            throw e;
        }
    }

    public Employee convertToEntity(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setName(employeeDTO.getName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setCellPhone(employeeDTO.getCellPhone());
        employee.setGender(employeeDTO.getGender());
        employee.setSupervisor(employeeDTO.getSupervisorId() != null ? findSupervisorById(employeeDTO.getSupervisorId()) : null);

        if (employeeDTO.getTaskIds() != null && !employeeDTO.getTaskIds().isEmpty()) {
            List<Task> tasks = taskRepositoryOuputPort.findAllById(employeeDTO.getTaskIds());
            employee.setTasks(tasks);
        } else {
            employee.setTasks(new ArrayList<>());
        }
        return employee;
    }

    public Employee findSupervisorById(Long id){
        logger.info("Fetching supervisor with id: {}", id);
        return employeeRepositoryOutputPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supervisor not found"));
    }
}
