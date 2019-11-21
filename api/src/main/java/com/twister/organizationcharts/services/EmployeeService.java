package com.twister.organizationcharts.services;

import com.twister.organizationcharts.model.Designation;
import com.twister.organizationcharts.model.Employee;
import com.twister.organizationcharts.model.exceptions.EmployeeException;
import com.twister.organizationcharts.model.input.EmployeeAdd;
import com.twister.organizationcharts.repository.DesignationRepo;
import com.twister.organizationcharts.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final DesignationRepo designationRepo;

    public EmployeeService(@Autowired EmployeeRepo employeeRepo, @Autowired DesignationRepo designationRepo) {
        this.employeeRepo = employeeRepo;
        this.designationRepo = designationRepo;
    }


    public List<Employee> getEmployeeList() {
        List<Employee> employeeList = employeeRepo.findAll();
        employeeList.sort(Employee::compareTo);
        return employeeList;
    }

    public Map<String, Object> getEmployeeOrgChart(int id) {
        Employee employee = getEmployee(id);
        Employee manager = employee.getManager();

        List<Employee> colleague = new ArrayList<>();
        if (manager != null) {
            colleague = employeeRepo.getEmployeesByManagerId(manager.getId());
            colleague.remove(employee);
            colleague.sort(Employee::compareTo);
        }
        List<Employee> reportingTo = employeeRepo.getEmployeesByManagerId(id);
        reportingTo.sort(Employee::compareTo);

        Map<String, Object> map = new HashMap<>();
        map.put("employee", employee);
        map.put("manager", manager);
        map.put("colleagues", colleague);
        map.put("subordinates", reportingTo);
        return map;
    }

    @Transactional
    public Employee addEmployeeDetails(Employee employee) {
        employee = employeeRepo.save(employee);
        if (designationRepo.getFirstByOrderByLevel().getLevel() == employee.getDesignation().getLevel())
            employeeRepo.updateManagerIdForOldTopEmployee(employee);
        return employee;
    }

    @Transactional
    public Employee updateOrReplaceEmployeeDetails(Employee oldEmployee, Employee employee, boolean replace) {
        if (replace) {
            employee = employeeRepo.save(employee);
            employeeRepo.updateManagerIdOfEmployees(oldEmployee, employee);
            employeeRepo.delete(oldEmployee);
        } else {
            employee.setId(oldEmployee.getId());
            employeeRepo.save(employee);
        }
        return employee;
    }

    @Transactional
    public void deleteEmployeeData(int id) {
        Employee employee = employeeRepo.getOne(id);
        employeeRepo.updateManagerIdOfEmployees(employee, employee.getManager());
        employeeRepo.delete(employee);
    }


    boolean employeeExistUnderDesignation(Designation designation) {
        return employeeRepo.countEmployeesByDesignation(designation) != 0;
    }


    public Employee convertToEmployee(EmployeeAdd employeeAdd) {
        if (employeeAdd.getManagerId() == null || employeeAdd.getManagerId() != 0 && employeeAdd.getJobTitle().equals(designationRepo.getFirstByOrderByLevel().getName()))
            throw new EmployeeException("For top rank employee managerId has to be 0", HttpStatus.BAD_REQUEST);
        return new Employee(employeeAdd.getName(), getEmployee(employeeAdd.getManagerId()), designationRepo.getDesignationByName(employeeAdd.getJobTitle()));
    }

    public Employee getEmployee(int id) {
        return employeeRepo.findById(id).orElse(null);
    }
}
