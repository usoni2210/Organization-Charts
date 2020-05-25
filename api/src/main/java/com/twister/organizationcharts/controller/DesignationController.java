package com.twister.organizationcharts.controller;

import com.twister.organizationcharts.model.Designation;
import com.twister.organizationcharts.model.exceptions.DesignationException;
import com.twister.organizationcharts.model.input.DesignationAdd;
import com.twister.organizationcharts.services.DesignationService;
import com.twister.organizationcharts.services.DesignationValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/designation")
public class DesignationController {
    private final DesignationService designationService;
    private final DesignationValidation designationValidation;

    public DesignationController(@Autowired DesignationService designationService, @Autowired DesignationValidation designationValidation) {
        this.designationService = designationService;
        this.designationValidation = designationValidation;
    }

    @GetMapping("")
    public ResponseEntity<List<Designation>> getListOfDesignations() {
        return new ResponseEntity<>(designationService.getAllDesignation(), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Designation> getDesignationInfo(@PathVariable("id") int id) {
        designationValidation.validId(id);
        return new ResponseEntity<>(designationService.getDesignation(id), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<Designation> addDesignation(@Valid @RequestBody DesignationAdd designationAdd) {
        if (designationAdd.getSuperiorId() == null || designationAdd.getSuperiorId() != 0)
            designationValidation.validId(designationAdd.getSuperiorId());
        return new ResponseEntity<>(designationService.addDesignationData(designationService.convertToDesignation(designationAdd), designationAdd.getParallel()), HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") int id) {
        if (designationValidation.ableToDeleteId(id)) {
            designationService.deleteDesignationData(id);
            return new ResponseEntity<>("Designation Deleted", HttpStatus.NO_CONTENT);
        } else
            throw new DesignationException("Some Employee Have this Designation", HttpStatus.BAD_REQUEST);
    }
}
