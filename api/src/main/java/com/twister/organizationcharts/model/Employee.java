package com.twister.organizationcharts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "employee")
@ApiModel(description = "Employee Model for Handle Employee Details")
public class Employee implements Comparable<Employee> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    private Integer id;

    @NotNull(message = "Employee Name should not be null")
    @Pattern(regexp = "[A-Za-z\\s]{2,}", message = "Name should only contain Alphabets and have atleast 2 Characters")
    @JsonProperty(required = true)
    @ApiModelProperty(notes = "Should be valid Name and have atleast 2 characters")
    private String name;

    @JsonProperty(required = true)
    @ManyToOne
    @JoinColumn(name = "manager_id")
    @ApiModelProperty(notes = "Should be not null and valid Employee ID")
    @JsonIgnoreProperties(value = {"manager"})
    private Employee manager;

    @Transient
    private String jobTitle;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;

    public Employee() {
    }

    public Employee(String name, Employee manager, Designation designation) {
        this.name = name;
        this.manager = manager;
        this.designation = designation;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Employee getManager() {
        return manager;
    }

    public Designation getDesignation() {
        return designation;
    }

    public String getJobTitle() {
        if (designation != null)
            jobTitle = designation.getName();
        return jobTitle;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", managerId=" + manager +
                ", designation=" + designation +
                '}';
    }

    @Override
    public int compareTo(Employee o) {
        int diffLevel = this.getDesignation().getLevel() - o.getDesignation().getLevel();
        if (diffLevel == 0) {
            return this.getName().compareTo(o.getName());
        } else
            return diffLevel;
    }
}
