package com.twister.organizationcharts.Repository;

import com.twister.organizationcharts.Model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public interface DesignationRepo extends JpaRepository<Designation, Integer> {
    Designation getDesignationByName(String name);
    List<Designation> getDesignationsByNameIn(Set<String> names);
}
