package com.twister.organizationcharts.Repository;

import com.twister.organizationcharts.Model.Designation;
import com.twister.organizationcharts.Model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface EmployeeRepo extends JpaRepository<Employee, Integer> {
    List<Employee> getEmployeesByManagerIdAndIdIsNotLike(Integer managerId, Integer employeeId);
    List<Employee> getEmployeeByManagerId(Integer managerId);
    int countEmployeesByManagerId(Integer managerId);

    int countEmployeesByDesignation(Designation designationId);

    @Modifying(clearAutomatically = true)
    @Query("update Employee set managerId = ?2 where managerId = ?1")
    void updateManagerIdOfEmployees(Integer oldMangerId, Integer newManagerId);
}
