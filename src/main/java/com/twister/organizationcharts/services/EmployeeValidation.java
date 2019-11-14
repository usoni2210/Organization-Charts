package com.twister.organizationcharts.services;

import com.twister.organizationcharts.Model.Designation;
import com.twister.organizationcharts.Model.Employee;
import com.twister.organizationcharts.Model.Exceptions.DesignationException;
import com.twister.organizationcharts.Model.Exceptions.EmployeeException;
import com.twister.organizationcharts.Repository.EmployeeRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class EmployeeValidation {

    private final EmployeeRepo employeeRepo;
    private final DesignationService designationService;

    public EmployeeValidation(EmployeeRepo employeeRepo, DesignationService designationService) {
        this.employeeRepo = employeeRepo;
        this.designationService = designationService;
    }

    public void validId(int id) {
        if (id < 0)
            throw new DesignationException("Designation ID should be positive integer", HttpStatus.BAD_REQUEST);
        if (!employeeRepo.existsById(id))
            throw new DesignationException();
    }

    public void isEmployeeAddDataValid(Employee employee) {
        if (employee.getManagerId() == null)
            throw new EmployeeException("managerId Not found", HttpStatus.BAD_REQUEST);
        isValidateEmployeeData(employee);
    }

    public void isEmployeeDataUpdatable(@Min(1) int id, Employee employee) {
        Designation designation = designationService.getDesignationByName(employee.getJobTitle());
        //Validation for Manager and Employee Designation
        isValidateEmployeeData(employee);

        // Validation for Employee Designation and Employees Reporting To
        Set<String> jobTitles = new HashSet<>();
        employeeRepo.getEmployeeByManagerId(id).forEach((Employee e) -> jobTitles.add(e.getJobTitle()));
        Set<Integer> levels = new HashSet<>();
        designationService.getDesignationsByNameIn(jobTitles).forEach((Designation d) -> levels.add(d.getLevel()));

        if (levels.size() != 0 && designation.getLevel() >= Collections.min(levels))
            throw new DesignationException("Can`t make Lower or equal Level Employee to Higher Level Employee Supervisor", HttpStatus.BAD_REQUEST);
    }

    private void isValidateEmployeeData(Employee employee) {
        Designation designation = designationService.getDesignationByName(employee.getJobTitle());
        Designation managerDesignation = designationService.getDesignationByName(employeeRepo.findById(employee.getManagerId()).orElse(new Employee()).getJobTitle());
        if (designation == null)
            throw new DesignationException("Designation Not Found", HttpStatus.BAD_REQUEST);
        else if ((employee.getManagerId() == -1 && designation.getLevel() != 1) || (employee.getManagerId() != -1 && managerDesignation == null))
            throw new EmployeeException("Not a Valid Manager ID", HttpStatus.BAD_REQUEST);
        else if (managerDesignation != null && designation.getLevel() <= managerDesignation.getLevel())
            throw new DesignationException("Can`t assign lower or equal level supervisor", HttpStatus.BAD_REQUEST);
    }

    public Boolean isDeletableEmployee(int id) {
        this.validId(id);
        Employee employee = employeeRepo.getOne(id);
        return employee.getManagerId() != -1 || employeeRepo.countEmployeesByManagerId(id) == 0;
    }


}
