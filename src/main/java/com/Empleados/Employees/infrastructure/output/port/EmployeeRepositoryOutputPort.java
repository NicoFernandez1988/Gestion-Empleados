package com.Empleados.Employees.infrastructure.output.port;

import com.Empleados.Employees.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepositoryOutputPort extends JpaRepository<Employee, Long> {

    List<Employee> findByNameStartingWithOrLastNameStartingWith(String name, String lastName);

    Optional<Employee> findByNameAndLastName(String name, String lastName);

    Optional<Employee> findEmployeeById(Long id);

    List<Employee> findBySupervisor(Employee supervisor);

}