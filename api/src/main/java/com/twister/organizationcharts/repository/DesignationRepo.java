package com.twister.organizationcharts.repository;

import com.twister.organizationcharts.model.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;


@Component
public interface DesignationRepo extends JpaRepository<Designation, Integer> {
    Designation getDesignationByName(String name);

    Designation getFirstByOrderByLevel();

    @Modifying
    @Query("UPDATE Designation SET level=level+1 WHERE level >= ?1")
    void updateDesignationLevel(int level);

    Designation getFirstByLevelGreaterThanOrderByLevel(int id);
}
