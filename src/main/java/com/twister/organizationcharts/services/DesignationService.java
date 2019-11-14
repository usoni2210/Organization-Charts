package com.twister.organizationcharts.services;

import com.twister.organizationcharts.Model.Designation;
import com.twister.organizationcharts.Model.DesignationAdd;
import com.twister.organizationcharts.Repository.DesignationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
public class DesignationService {
    private final DesignationRepo designationRepo;

    public DesignationService(@Autowired DesignationRepo designationRepo) {
        this.designationRepo = designationRepo;
    }

    public List<Designation> getAllDesignation() {
        return designationRepo.findAll();
    }

    public Designation getDesignation(int id) {
        return designationRepo.findById(id).orElse(null);
    }

    public Designation addDesignationData(Designation designation) {
        return designationRepo.save(designation);
    }

    @Transactional
    public void deleteDesignationData(int id) {
        designationRepo.deleteById(id);
    }

    public Designation convertToDesignation(DesignationAdd designationAdd) {
        return new Designation(designationAdd.getName(), designationAdd.getLevel());
    }

    Designation getDesignationByName(String jobTitle) {
        return designationRepo.getDesignationByName(jobTitle);
    }

    List<Designation> getDesignationsByNameIn(Set<String> jobTitles) {
        return designationRepo.getDesignationsByNameIn(jobTitles);
    }
}
