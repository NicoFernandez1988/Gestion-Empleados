package com.Empleados.Employees.infrastructure.output.port;

import com.Empleados.Employees.domain.Task;
import com.Empleados.Employees.domain.TaskCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepositoryOuputPort extends JpaRepository<Task, Long>{

        List<Task> findByCategory(TaskCategory category);
}
