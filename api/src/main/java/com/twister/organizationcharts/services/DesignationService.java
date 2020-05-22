package com.twister.organizationcharts.services;

import com.twister.organizationcharts.model.Designation;
import com.twister.organizationcharts.model.input.DesignationAdd;
import com.twister.organizationcharts.repository.DesignationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class DesignationService {
    private final DesignationRepo designationRepo;

    public DesignationService(@Autowired DesignationRepo designationRepo) {
        this.designationRepo = designationRepo;
    }

    public List<Designation> getAllDesignation() {
        List<Designation> designationList = designationRepo.findAll();
        designationList.sort(Designation::compareTo);
        return designationList;
    }

    public Designation getDesignation(int id) {
        return designationRepo.findById(id).orElse(null);
    }

    @Transactional
    public Designation addDesignationData(Designation designation, boolean parallel) {
        if (!parallel)
            designationRepo.updateDesignationLevel(designation.getLevel());
        return designationRepo.save(designation);
    }

    @Transactional
    public void deleteDesignationData(int id) {
        designationRepo.deleteById(id);
    }

    public Designation convertToDesignation(DesignationAdd designationAdd) {
        Designation superior = designationRepo.findById(designationAdd.getSuperiorId()).orElse(null);
        int superiorLevel = superior != null ? superior.getLevel() : 0;
        return new Designation(designationAdd.getName(), superiorLevel + 1);
    }

    public boolean isTopDesignation(Designation designation) {
        return designation.getId() == designationRepo.getFirstByOrderByLevel().getId();
    }
}
