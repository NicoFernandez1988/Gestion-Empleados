package com.Empleados.Employees.infrastructure.input.adapter;
import com.Empleados.Employees.application.EmployeeService;
import com.Empleados.Employees.application.exceptions.EmployeeDuplicateException;
import com.Empleados.Employees.application.exceptions.EmployeeNotFoundException;
import com.Empleados.Employees.domain.Employee;
import com.Empleados.Employees.domain.dto.EmployeeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @Operation(summary = "Crear un empleado exitosamente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Crear Empleado"),
            @ApiResponse(responseCode = "409", description = "Empleado con este nombre y apellido ya existe registrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeDTO employeeDTO) throws EmployeeNotFoundException,
            EmployeeDuplicateException {
            Employee newEmployee = employeeService.saveEmployee(employeeDTO);
            return new ResponseEntity<>(newEmployee, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los Empleados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Obtener todos los empleados",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un empleado por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Obtener empleado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "ID inválido",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Employee getEmployeeById(@PathVariable Long id) throws EmployeeNotFoundException {
        return employeeService.getEmployeeById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un empleado por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado Actualizado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody EmployeeDTO employeeDTO)
            throws EmployeeNotFoundException, EmployeeDuplicateException {
            Employee updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
            return new ResponseEntity<>(updatedEmployee, HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un empleado por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empleado eliminado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))})
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) throws EmployeeNotFoundException {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
