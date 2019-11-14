package com.twister.organizationcharts.services;

import com.twister.organizationcharts.Model.Designation;
import com.twister.organizationcharts.Model.Employee;
import com.twister.organizationcharts.Model.EmployeeAdd;
import com.twister.organizationcharts.Repository.DesignationRepo;
import com.twister.organizationcharts.Repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final DesignationRepo designationRepo;
    private Comparator<Employee> employeeComparator;

    public EmployeeService(@Autowired EmployeeRepo employeeRepo, @Autowired DesignationRepo designationRepo) {
        this.employeeRepo = employeeRepo;
        this.designationRepo = designationRepo;
        employeeComparator = Comparator.comparingInt((Employee o) -> o.getDesignation().getLevel()).thenComparing(Employee::getName);
    }


    public List<Employee> getEmployeeList() {
        List<Employee> employeeList = employeeRepo.findAll();
        employeeList.sort(employeeComparator);
        return employeeList;
    }

    public Map<String, Object> getEmployeeOrgChart(int id) {
        Employee employee = getEmployee(id);
        Employee manager = employeeRepo.findById(employee.getManagerId()).orElse(null);
        List<Employee> colleague = employeeRepo.getEmployeesByManagerIdAndIdIsNotLike(employee.getManagerId(), id);
        List<Employee> reportingTo = employeeRepo.getEmployeeByManagerId(id);

        colleague.sort(employeeComparator);
        reportingTo.sort(employeeComparator);

        Map<String, Object> map = new HashMap<>();
        map.put("employee", employee);
        if (manager != null)
            map.put("manager", manager);
        map.put("colleagues", colleague);
        map.put("subordinates", reportingTo);
        return map;
    }

    public Employee addEmployeeDetails(Employee employee) {
        return employeeRepo.save(employee);
    }

    @Transactional
    public Employee updateOrReplaceEmployeeDetails(Integer id, Employee employee, Boolean replace) {
        if (replace) {
            employee = addEmployeeDetails(employee);
            employeeRepo.updateManagerIdOfEmployees(id, employee.getId());
        } else {
            employee.setId(id);
            employeeRepo.save(employee);
        }
        return employee;
    }

    @Transactional
    public void deleteEmployeeData(int id) {
        Employee employee = employeeRepo.getOne(id);
        employeeRepo.updateManagerIdOfEmployees(id, employee.getManagerId());
        employeeRepo.delete(employee);
    }


    boolean employeeExistUnderDesignation(Designation designation) {
        return employeeRepo.countEmployeesByDesignation(designation) != 0;
    }


    public Employee convertToEmployee(EmployeeAdd employeeAdd) {
        return new Employee(employeeAdd.getName(), employeeAdd.getManagerId(), designationRepo.getDesignationByName(employeeAdd.getJobTitle()));
    }

    public Employee getEmployee(int id) {
        return employeeRepo.findById(id).orElse(null);
    }
}
