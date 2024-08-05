package com.Empleados.Employees.infrastructure.output.port;

import com.Empleados.Employees.domain.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCategoryOutputRepository extends JpaRepository<TaskCategory, Long> {
    TaskCategory findByName(String name);
}
