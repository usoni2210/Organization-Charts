package com.twister.organizationcharts.controller;

import com.twister.organizationcharts.model.Employee;
import com.twister.organizationcharts.model.exceptions.EmployeeException;
import com.twister.organizationcharts.model.input.EmployeeAdd;
import com.twister.organizationcharts.model.input.EmployeeReplace;
import com.twister.organizationcharts.services.EmployeeService;
import com.twister.organizationcharts.services.EmployeeValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeValidation employeeValidation;

    public EmployeeController(@Autowired EmployeeService employeeService, EmployeeValidation employeeValidation) {
        this.employeeService = employeeService;
        this.employeeValidation = employeeValidation;
    }

    @GetMapping("")
    public ResponseEntity<List<Employee>> getALLEmployeesInfo() {
        return new ResponseEntity<>(employeeService.getEmployeeList(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Map<String, Object>> getEmployeeInfo(@PathVariable("id") int id) {
        employeeValidation.validId(id);
        return new ResponseEntity<>(employeeService.getEmployeeOrgChart(id), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Employee> addEmployee(@Valid @RequestBody EmployeeAdd employeeAdd) {
        Employee employee = employeeService.convertToEmployee(employeeAdd);
        employeeValidation.isEmployeeAddDataValid(employee);
        return new ResponseEntity<>(employeeService.addEmployeeDetails(employee), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<Employee> updateOrReplaceEmployee(@PathVariable("id") int id, @Valid @RequestBody EmployeeReplace employeeReplace) {
        employeeValidation.validId(id);
        Employee oldEmployee = employeeService.getEmployee(id);
        if (employeeReplace.getManagerId() == null)
            employeeReplace.setManagerId(oldEmployee.getManager().getId());

        Employee employee = employeeService.convertToEmployee(employeeReplace);
        employeeValidation.isEmployeeDataUpdatable(id, employee);

        return new ResponseEntity<>(employeeService.updateOrReplaceEmployeeDetails(oldEmployee, employee, employeeReplace.getReplace()), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") int id) {
        if (!employeeValidation.isDeletableEmployee(id))
            throw new EmployeeException("This Employee Can`t be Deleted without replacing him/her", HttpStatus.BAD_REQUEST);

        employeeService.deleteEmployeeData(id);
        return new ResponseEntity<>("Employee Deleted", HttpStatus.NO_CONTENT);
    }
}
