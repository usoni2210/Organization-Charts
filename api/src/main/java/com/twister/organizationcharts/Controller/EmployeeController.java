package com.twister.organizationcharts.Controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.twister.organizationcharts.Model.Employee;
import com.twister.organizationcharts.Model.EmployeeAdd;
import com.twister.organizationcharts.Model.EmployeeReplace;
import com.twister.organizationcharts.Model.Exceptions.EmployeeException;
import com.twister.organizationcharts.View.EmployeeJsonView;
import com.twister.organizationcharts.services.EmployeeService;
import com.twister.organizationcharts.services.EmployeeValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    @JsonView(EmployeeJsonView.EmployeeExternal.class)
    public ResponseEntity<List<Employee>> getALLEmployeesInfo() {
        return new ResponseEntity<>(employeeService.getEmployeeList(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    @JsonView(EmployeeJsonView.EmployeeExternal.class)
    public ResponseEntity<Map<String, Object>> getEmployeeInfo(@PathVariable("id") int id) {
        employeeValidation.validId(id);
        return new ResponseEntity<>(employeeService.getEmployeeOrgChart(id), HttpStatus.OK);
    }

    @PostMapping("")
    @JsonView(EmployeeJsonView.EmployeeExternal.class)
    public ResponseEntity<Employee> addEmployee(@Valid @RequestBody EmployeeAdd employeeAdd) {
        Employee employee = employeeService.convertToEmployee(employeeAdd);
        employeeValidation.isEmployeeAddDataValid(employee);
        return new ResponseEntity<>(employeeService.addEmployeeDetails(employee), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    @JsonView(EmployeeJsonView.EmployeeInternal.class)
    public ResponseEntity<Employee> updateOrReplaceEmployee(@PathVariable("id") @Min(1) int id, @Valid @RequestBody EmployeeReplace employeeReplace) {
        employeeValidation.validId(id);

        if (employeeReplace.getManagerId() == null)
            employeeReplace.setManagerId(employeeService.getEmployee(id).getManagerId());

        Employee employee = employeeService.convertToEmployee(employeeReplace);
        employeeValidation.isEmployeeDataUpdatable(id, employee);

        return new ResponseEntity<>(employeeService.updateOrReplaceEmployeeDetails(id, employee, employeeReplace.getReplace()), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") int id) {
        if (employeeValidation.isDeletableEmployee(id)) {
            employeeService.deleteEmployeeData(id);
            return new ResponseEntity<>("Employee Deleted", HttpStatus.NO_CONTENT);
        } else {
            throw new EmployeeException("This Employee Can`t be Deleted without replacing him/her", HttpStatus.BAD_REQUEST);
        }
    }
}
