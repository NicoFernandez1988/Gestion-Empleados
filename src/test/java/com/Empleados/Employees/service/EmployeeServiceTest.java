package com.Empleados.Employees.service;

import com.Empleados.Employees.application.EmployeeAssignmentService;
import com.Empleados.Employees.application.EmployeeService;
import com.Empleados.Employees.application.EmployeeValidationService;
import com.Empleados.Employees.application.exceptions.EmployeeDuplicateException;
import com.Empleados.Employees.application.exceptions.EmployeeNotFoundException;
import com.Empleados.Employees.domain.Employee;
import com.Empleados.Employees.domain.dto.EmployeeDTO;
import com.Empleados.Employees.domain.enums.Gender;
import com.Empleados.Employees.infrastructure.output.port.EmployeeRepositoryOutputPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepositoryOutputPort employeeRepositoryOutputPort;

    @Mock
    private EmployeeValidationService employeeValidationService;

    @Mock
    private EmployeeAssignmentService employeeAssignmentService;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    @DisplayName("Obtiene un empleado por Id")
    void getEmployee_withId_returnEmployee() throws EmployeeNotFoundException {
        Employee employee = new Employee();
        employee.setId(1L);

        when(employeeRepositoryOutputPort.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Devuelve un EmployeeNotFoundException cuando no se encuentra un candidato por el id solicitado")
    void getEmployee_withInvalidId_returnNotFoundEmployee() throws EmployeeNotFoundException {
        when(employeeRepositoryOutputPort.findById(100L)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployeeById(100L);
        });
        assertEquals("Employee not found with id: 100", exception.getMessage());
    }

    @Test
    @DisplayName("Obtiene una lista de todos los empleados")
    void getAllEmployees_withValidData_returnAllEmployees() {
        Employee employee1 = createTestExistingEmployee(1L);
        Employee employee2 = createTestExistingEmployee(2L);

        List<Employee> employees = Arrays.asList(employee1, employee2);
        when(employeeRepositoryOutputPort.findAll(Sort.by("name", "lastName"))).thenReturn(employees);

        List<Employee> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getName());
        assertEquals("John", result.get(1).getName());
        verify(employeeRepositoryOutputPort).findAll(Sort.by("name", "lastName"));
    }

    @Test
    @DisplayName("Obtiene una lista vacia cuando no hay empleados")
    void getAllEmployees_withEmptyList_returnZeroEmployees() {
        when(employeeRepositoryOutputPort.findAll(Sort.by("name", "lastName"))).thenReturn(Collections.emptyList());

        List<Employee> result = employeeService.getAllEmployees();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(employeeRepositoryOutputPort).findAll(Sort.by("name", "lastName"));
    }

    @Test
    @DisplayName("Crea un empleado correctamente")
    void createEmployee_withValidData_createsEmployeeSuccessfully() throws EmployeeNotFoundException, EmployeeDuplicateException {
        EmployeeDTO employeeDTO = createTestEmployeeDTO();
        Employee employee = createTestEmployee();

        EmployeeService spyEmployeeService = spy(employeeService);
        doReturn(employee).when(spyEmployeeService).convertToEntity(employeeDTO);

        doNothing().when(employeeValidationService).validateEmployeeNameAndLastName(employeeDTO);
        doNothing().when(employeeAssignmentService).assignSupervisor(employeeDTO);
        doNothing().when(employeeAssignmentService).assignTasks(employeeDTO);

        when(employeeRepositoryOutputPort.save(employee)).thenReturn(employee);

        Employee result = spyEmployeeService.saveEmployee(employeeDTO);

        assertNotNull(result);
        assertEquals("John", result.getName());
        assertEquals("Doe", result.getLastName());

        verify(spyEmployeeService).convertToEntity(employeeDTO);
        verify(employeeValidationService).validateEmployeeNameAndLastName(employeeDTO);
        verify(employeeAssignmentService).assignSupervisor(employeeDTO);
        verify(employeeAssignmentService).assignTasks(employeeDTO);
        verify(employeeRepositoryOutputPort).save(employee);
    }

    @Test
    @DisplayName("Retorna una EmployeeDuplicateException cuando ya existe un empleado registrado con el mismo nombre y apellido")
    void createCandidate_WithSameNameAndLastName_CandidateAlreadyExists() throws EmployeeDuplicateException, EmployeeNotFoundException {
        EmployeeDTO employeeDTO = createTestEmployeeDTO();

        doThrow(new EmployeeDuplicateException("Already exists an employee with the same name and last name."))
                .when(employeeValidationService).validateEmployeeNameAndLastName(employeeDTO);

        assertThrows(EmployeeDuplicateException.class, () -> {
            employeeService.saveEmployee(employeeDTO);
        });

        verify(employeeValidationService).validateEmployeeNameAndLastName(employeeDTO);

        verify(employeeAssignmentService, never()).assignSupervisor(any());
        verify(employeeAssignmentService, never()).assignTasks(any());
        verify(employeeRepositoryOutputPort, never()).save(any());
    }

    @Test
    @DisplayName("Actualiza un empleado existente")
    void updateEmployee_WithValidId_existingEmployee() throws EmployeeNotFoundException, EmployeeDuplicateException {
        Long employeeId = 1L;

        EmployeeDTO employeeDTO = createTestEmployeeDTO();
        Employee existingEmployee = createTestExistingEmployee(employeeId);
        Employee updatedEmployee = createTestUpdatedEmployee(employeeId);

        when(employeeRepositoryOutputPort.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        doNothing().when(employeeValidationService).validateEmployeeNameAndLastName(employeeDTO);
        doNothing().when(employeeAssignmentService).assignSupervisor(employeeDTO);
        doNothing().when(employeeAssignmentService).assignTasks(employeeDTO);
        when(employeeRepositoryOutputPort.save(existingEmployee)).thenReturn(updatedEmployee);

        Employee result = employeeService.updateEmployee(employeeId, employeeDTO);

        assertNotNull(result);
        assertEquals("Jane", result.getName());
        assertEquals("Doe", result.getLastName());
        assertEquals("123456789", result.getCellPhone());

        verify(employeeRepositoryOutputPort).findById(employeeId);
        verify(employeeValidationService).validateEmployeeNameAndLastName(employeeDTO);
        verify(employeeAssignmentService).assignSupervisor(employeeDTO);
        verify(employeeAssignmentService).assignTasks(employeeDTO);
        verify(employeeRepositoryOutputPort).save(existingEmployee);
    }

    @Test
    @DisplayName("Retorna una exception tipo EmployeeNotFoundException cuando no existe el empleado que se quiere actualizar")
    void updateEmployee_WithInvalidId_existingEmployee() {
        EmployeeDTO employeeDTO = createTestEmployeeDTO();
        when(employeeRepositoryOutputPort.findById(100L)).thenReturn(Optional.empty());

        EmployeeNotFoundException exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.updateEmployee(100L, employeeDTO);
        });
        assertEquals("Employee not found with id: 100", exception.getMessage());
    }

    @Test
    @DisplayName("Elimina un empleado con exito")
    void deleteEmployee_WithValidId_EmployeeDeletedSuccessfully() throws EmployeeNotFoundException {
        Long employeeId = 1L;
        Employee employee = createTestExistingEmployee(employeeId);
        List<Employee> supervisedEmployees = Collections.singletonList(createTestExistingEmployee(2L));

        when(employeeRepositoryOutputPort.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepositoryOutputPort.findBySupervisor(employee)).thenReturn(supervisedEmployees);

        employeeService.deleteEmployee(employeeId);

        verify(employeeRepositoryOutputPort).findById(employeeId);
        verify(employeeRepositoryOutputPort).findBySupervisor(employee);
        verify(employeeRepositoryOutputPort).save(any(Employee.class));
        verify(employeeRepositoryOutputPort).delete(employee);

    }

    @Test
    @DisplayName("Lanza EmployeeNotFoundException cuando el empleado no existe")
    void deleteEmployee_WithInvalidId_EmployeeNotFound() {
        Long employeeId = 1L;

        when(employeeRepositoryOutputPort.findById(employeeId)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(employeeId));

        verify(employeeRepositoryOutputPort).findById(employeeId);
        verify(employeeRepositoryOutputPort, never()).findBySupervisor(any(Employee.class));
        verify(employeeRepositoryOutputPort, never()).save(any(Employee.class));
        verify(employeeRepositoryOutputPort, never()).delete(any(Employee.class));
    }


    private static EmployeeDTO createTestEmployeeDTO() {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setName("Jane");
        employeeDTO.setLastName("Doe");
        employeeDTO.setCellPhone("123456789");
        employeeDTO.setGender(Gender.valueOf("FEMENINO"));
        return employeeDTO;
    }

    private static Employee createTestExistingEmployee(Long id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName("John");
        employee.setLastName("Doe");
        employee.setCellPhone("987654321");
        employee.setGender(Gender.valueOf("MASCULINO"));
        return employee;


    }

    private static Employee createTestUpdatedEmployee(Long id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName("Jane");
        employee.setLastName("Doe");
        employee.setCellPhone("123456789");
        employee.setGender(Gender.valueOf("FEMENINO"));
        return employee;
    }

    private static Employee createTestEmployee() {
        Employee employee = new Employee();
        employee.setName("John");
        employee.setLastName("Doe");
        return employee;
    }


}