package com.twister.organizationcharts.services;

import com.twister.organizationcharts.Model.Designation;
import com.twister.organizationcharts.Model.Exceptions.DesignationException;
import com.twister.organizationcharts.Repository.DesignationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class DesignationValidation {

    private final DesignationRepo designationRepo;
    private final EmployeeService employeeService;

    public DesignationValidation(@Autowired DesignationRepo designationRepo, EmployeeService employeeService) {
        this.designationRepo = designationRepo;
        this.employeeService = employeeService;
    }

    public void validId(int id) {
        if (id < 0)
            throw new DesignationException("Designation ID should be positive integer", HttpStatus.BAD_REQUEST);
        if (!designationRepo.existsById(id))
            throw new DesignationException();
    }

    public Boolean ableToDeleteId(int id) {
        this.validId(id);
        return !employeeService.employeeExistUnderDesignation(designationRepo.findById(id).orElse(new Designation()));
    }
}
