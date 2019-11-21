package com.twister.organizationcharts.repository;

import com.twister.organizationcharts.model.Designation;
import com.twister.organizationcharts.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EmployeeRepo extends JpaRepository<Employee, Integer> {
    List<Employee> getEmployeesByManagerId(Integer managerId);

    int countEmployeesByDesignation(Designation designationId);

    List<Employee> getEmployeesByManagerIsNull();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Employee SET manager = ?2 WHERE manager = ?1")
    void updateManagerIdOfEmployees(Employee oldMangerId, Employee newManagerId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Employee SET manager = ?1 WHERE manager IS NULL AND id <> ?1")
    void updateManagerIdForOldTopEmployee(Employee newManagerId);

}
