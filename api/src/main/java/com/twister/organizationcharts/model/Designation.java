package com.twister.organizationcharts.model;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Entity
@Table(name = "designation")
public class Designation implements Comparable<Designation> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(value = 0, message = "Designation Id must be greater then 0")
    private int id;
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

    public int getId() {
        return id;
    }

    public void setId(int designationId) {
        this.id = designationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Designation{" +
                "designationId=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                '}';
    }

    @Override
    public int compareTo(Designation o) {
        int diffLevel = this.getLevel() - o.getLevel();
        if (diffLevel == 0) {
            return this.getName().compareTo(o.getName());
        } else
            return diffLevel;
    }
}
