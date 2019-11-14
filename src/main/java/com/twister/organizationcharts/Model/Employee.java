package com.twister.organizationcharts.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.twister.organizationcharts.View.EmployeeJsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "employee")
@ApiModel(description = "Employee Model for Handle Employee Details")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    @JsonView(EmployeeJsonView.EmployeeExternal.class)
    private Integer id;

    @NotNull(message = "Employee Name should not be null")
    @Pattern(regexp = "[A-Za-z\\s]{2,}", message = "Name should only contain Alphabets and have atleast 2 Characters")
    @JsonView(EmployeeJsonView.EmployeeView.class)
    @JsonProperty(required = true)
    @ApiModelProperty(notes = "Should be valid Name and have atleast 2 characters")
    private String name;

    @JsonProperty(required = true)
    @JsonView(EmployeeJsonView.EmployeeInternal.class)
    @ApiModelProperty(notes = "Should be not null and valid id of Employee")
    private Integer managerId;

    @JsonProperty(required = true)
    @Transient
    @JsonView(EmployeeJsonView.EmployeeView.class)
    @ApiModelProperty(notes = "Should be not null and a valid Designation Name")
    private String jobTitle;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "DESIGNATION_ID", nullable = false)
    @JsonView(EmployeeJsonView.EmployeeView.class)
    private Designation designation;

    public Employee() {
    }

    public Employee(String name, Integer managerId, Designation designation) {
        this.name = name;
        this.managerId = managerId;
        this.designation = designation;
        if (designation != null)
            this.jobTitle = designation.getName();
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

    public Integer getManagerId() {
        return managerId;
    }

    public String getJobTitle() {
        if (designation != null)
            jobTitle = designation.getName();
        return jobTitle;
    }

    public Designation getDesignation() {
        return designation;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", managerId=" + managerId +
                ", designation=" + designation +
                '}';
    }
}
