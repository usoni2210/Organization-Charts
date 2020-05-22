package com.twister.organizationcharts.services;

import com.twister.organizationcharts.model.exceptions.DesignationException;
import com.twister.organizationcharts.repository.DesignationRepo;
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

    public void validId(Integer id) {
        if (id < 0)
            throw new DesignationException("Designation ID should be positive integer", HttpStatus.BAD_REQUEST);
        if (!designationRepo.existsById(id))
            throw new DesignationException();
    }

    public boolean ableToDeleteId(int id) {
        this.validId(id);
        return !employeeService.employeeExistUnderDesignation(designationRepo.findById(id).orElse(null));
    }
}
