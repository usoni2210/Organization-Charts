package com.twister.organizationcharts.Model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Entity
@Table(name = "designation")
public class Designation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(1)
    private int designationId;
    @Size(min = 2, message = "Designation Name should have atleast 2 Characters")
    private String name;
    @Min(value = 1, message = "Designation minimum value should be 1")
    private int level;

    public Designation() {
    }

    public Designation(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public int getDesignationId() {
        return designationId;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public void setDesignationId(int designationId) {
        this.designationId = designationId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Designation{" +
                "designationId=" + designationId +
                ", name='" + name + '\'' +
                ", level=" + level +
                '}';
    }
}
