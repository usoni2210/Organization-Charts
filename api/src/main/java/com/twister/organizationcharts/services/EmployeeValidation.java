package com.twister.organizationcharts.services;

import com.twister.organizationcharts.model.Designation;
import com.twister.organizationcharts.model.Employee;
import com.twister.organizationcharts.model.exceptions.DesignationException;
import com.twister.organizationcharts.model.exceptions.EmployeeException;
import com.twister.organizationcharts.repository.EmployeeRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class EmployeeValidation {

    private final EmployeeRepo employeeRepo;
    private final DesignationService designationService;

    public EmployeeValidation(EmployeeRepo employeeRepo, DesignationService designationService) {
        this.employeeRepo = employeeRepo;
        this.designationService = designationService;
    }

    // Validate Employee ID
    public void validId(int id) {
        if (id < 0)
            throw new EmployeeException("Employee ID should be positive integer", HttpStatus.BAD_REQUEST);
        if (!employeeRepo.existsById(id))
            throw new EmployeeException();
    }

    // Validate Employee Data before adding it to database
    public void isEmployeeAddDataValid(Employee employee) {
        List<Employee> superiorEmployeeList = employeeRepo.getEmployeesByManagerIsNull();

        if (employee.getManager() == null && employee.getDesignation() != null && !designationService.isTopDesignation(employee.getDesignation()))
            throw new EmployeeException("managerId Not found", HttpStatus.BAD_REQUEST);
        else if (!superiorEmployeeList.isEmpty() && employee.getDesignation() != null && designationService.isTopDesignation(employee.getDesignation())) {
            for (Employee emp : superiorEmployeeList) {
                if (designationService.isTopDesignation(emp.getDesignation()))
                    throw new EmployeeException("Only one " + employee.getDesignation().getName() + " is allowed", HttpStatus.BAD_REQUEST);
            }
        }
        isValidateEmployeeData(employee);
    }

    // validate employee data before updating or replacing employee
    public void isEmployeeDataUpdatable(int id, Employee employee) {
        //Validation for Manager and Employee Designation

        isValidateEmployeeData(employee);

        if (id == employee.getManager().getId())
            throw new EmployeeException("Employee ID and Manager Id should not same", HttpStatus.BAD_REQUEST);

        // Validation for Employee Designation and Employees Reporting To
        Designation designation = employee.getDesignation();
        Set<Integer> levels = new HashSet<>();
        employeeRepo.getEmployeesByManagerId(id).forEach((Employee e) -> levels.add(e.getDesignation().getLevel()));

        if (!levels.isEmpty() && designation.getLevel() >= Collections.min(levels))
            throw new DesignationException("Can`t make Lower or equal Level Employee to Higher Level Employee Supervisor", HttpStatus.BAD_REQUEST);
    }

    // Common validation for adding and updating employee for manager and employee designation hierarchy
    private void isValidateEmployeeData(Employee employee) {
        Designation designation = employee.getDesignation();

        Designation managerDesignation = null;
        if (employee.getManager() != null)
            managerDesignation = employee.getManager().getDesignation();

        if (designation == null)
            throw new DesignationException("Designation Not Found", HttpStatus.BAD_REQUEST);
        else if ((employee.getManager() == null && !designationService.isTopDesignation(designation)) || (employee.getManager() != null && managerDesignation == null))
            throw new EmployeeException("Not a Valid Manager ID", HttpStatus.BAD_REQUEST);
        else if (managerDesignation != null && designation.getLevel() <= managerDesignation.getLevel())
            throw new DesignationException("Can`t assign lower or equal level supervisor", HttpStatus.BAD_REQUEST);

    }

    // Employee data validation before deleting
    public boolean isDeletableEmployee(int id) {
        this.validId(id);
        Employee employee = employeeRepo.findById(id).orElse(null);
        return employee != null && employee.getManager() != null;
    }


}
