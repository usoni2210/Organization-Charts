package com.twister.organizationcharts.Controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.twister.organizationcharts.Exceptions.DesignationException;
import com.twister.organizationcharts.Exceptions.EmployeeException;
import com.twister.organizationcharts.Model.Designation;
import com.twister.organizationcharts.Model.Employee;
import com.twister.organizationcharts.Model.ReplaceWrapper;
import com.twister.organizationcharts.Repository.DesignationRepo;
import com.twister.organizationcharts.Repository.EmployeeRepo;
import com.twister.organizationcharts.View.EmployeeJsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.*;

@RestController
@RequestMapping("/rest/employees")
public class EmployeeController {

    private final EmployeeRepo employeeRepo;
    private final DesignationRepo designationRepo;
    private final Comparator<Employee> sortEmployee;

    public EmployeeController(@Autowired EmployeeRepo employeeRepo, @Autowired DesignationRepo designationRepo) {
        this.employeeRepo = employeeRepo;
        this.designationRepo = designationRepo;
        sortEmployee = Comparator.comparingInt((Employee o) -> designationRepo.getDesignationByName(o.getJobTitle()).getLevel()).thenComparing(Employee::getName);
    }

    @GetMapping("")
    @JsonView(EmployeeJsonView.EmployeeExternal.class)
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeRepo.findAll();
        employees.sort(sortEmployee);
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @JsonView(EmployeeJsonView.EmployeeExternal.class)
    public ResponseEntity<Map<String, Object>> getEmployeeInfo(@PathVariable("id") int id) {
        if (id < 1)
            throw new EmployeeException("Employee not found", HttpStatus.BAD_REQUEST);
        if (!employeeRepo.existsById(id))
            throw new EmployeeException();

        Employee employee = employeeRepo.findById(id).orElse(new Employee());
        Employee manager = employeeRepo.findById(employee.getManagerId()).orElse(null);
        List<Employee> colleague = employeeRepo.getEmployeesByManagerIdAndIdIsNotLike(employee.getManagerId(), id);
        List<Employee> reportingTo = employeeRepo.getEmployeeByManagerId(id);

        colleague.sort(sortEmployee);
        reportingTo.sort(sortEmployee);

        Map<String, Object> map = new HashMap<>();
        map.put("employee", employee);
        if (manager != null)
            map.put("manager", manager);
        map.put("colleagues", colleague);
        map.put("subordinates", reportingTo);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Employee> addEmployee(@Valid @RequestBody Employee employee) {
        if (employee.getId() != null)
            throw new EmployeeException("employeeId can`t define", HttpStatus.BAD_REQUEST);
        if (employee.getManagerId() == null)
            throw new EmployeeException("managerId Not found", HttpStatus.BAD_REQUEST);
        validateEmployeeDesignation(employee);

        employeeRepo.save(employee);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @Transactional
    @PutMapping("{id}")
    public ResponseEntity<Employee> updateOrReplaceEmployee(@PathVariable("id") @Min(1) int id, @Valid @RequestBody ReplaceWrapper wrapper) {
        if (!employeeRepo.existsById(id))
            throw new EmployeeException();
        if (wrapper.getId() != null)
            throw new EmployeeException("employeeId can`t define in Json", HttpStatus.BAD_REQUEST);

        Employee employee;
        if (wrapper.getManagerId() == null)
            employee = new Employee(wrapper.getName(), employeeRepo.getOne(id).getManagerId(), wrapper.getJobTitle());
        else
            employee = new Employee(wrapper.getName(), wrapper.getManagerId(), wrapper.getJobTitle());

        Designation designation = designationRepo.getDesignationByName(employee.getJobTitle());
        //Validation for Manager and Employee Designation
        validateEmployeeDesignation(employee);

        // Validation for Employee Designation and Employees Reporting To
        Set<String> jobTitles = new HashSet<>();
        employeeRepo.getEmployeeByManagerId(id).forEach((Employee e) -> jobTitles.add(e.getJobTitle()));
        Set<Integer> levels = new HashSet<>();
        designationRepo.getDesignationsByNameIn(jobTitles).forEach((Designation d) -> levels.add(d.getLevel()));

        if (levels.size() != 0 && designation.getLevel() >= Collections.min(levels))
            throw new DesignationException("Can`t make Lower or equal Level Employee to Higher Level Employee Supervisor", HttpStatus.BAD_REQUEST);

        // Creating aur Update Employee Data
        if (wrapper.getReplace()) {
            employee = employeeRepo.save(employee);
            employeeRepo.updateManagerIdOfEmployees(id, employee.getId());
        } else {
            employee.setId(id);
            employeeRepo.save(employee);
        }
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") int id) {
        if (id < 1)
            throw new EmployeeException("Employee not found", HttpStatus.BAD_REQUEST);
        if (!employeeRepo.existsById(id)) {
            throw new EmployeeException("Employee not found", HttpStatus.NOT_FOUND);
        }

        Employee employee = employeeRepo.getOne(id);
        if (employee.getManagerId() == -1 && employeeRepo.countEmployeesByManagerId(id) != 0) {
            throw new EmployeeException("This Employee Can`t be Deleted without replacing him/her", HttpStatus.BAD_REQUEST);
        }

        employeeRepo.updateManagerIdOfEmployees(id, employee.getManagerId());
        employeeRepo.delete(employee);
        return new ResponseEntity<>("Employee Deleted", HttpStatus.NO_CONTENT);
    }

    private void validateEmployeeDesignation(Employee employee) {
        Designation designation = designationRepo.getDesignationByName(employee.getJobTitle());
        Designation managerDesignation = designationRepo
                .getDesignationByName(employeeRepo.findById(employee.getManagerId()).orElse(new Employee()).getJobTitle());
        if (designation == null)
            throw new DesignationException("Designation Not Found", HttpStatus.BAD_REQUEST);
        else if ((employee.getManagerId() == -1 && designation.getLevel() != 1) || (employee.getManagerId() != -1 && managerDesignation == null))
            throw new EmployeeException("Not a Valid Manager ID", HttpStatus.BAD_REQUEST);
        else if (managerDesignation != null && designation.getLevel() <= managerDesignation.getLevel())
            throw new DesignationException("Can`t assign lower or equal level supervisor", HttpStatus.BAD_REQUEST);
    }
}
