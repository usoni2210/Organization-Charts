package com.twister.organizationcharts.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.twister.organizationcharts.View.EmployeeJsonView;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@ApiModel(description = "Employee Model for Handle Employee Details")
public class Employee implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    @JsonView(EmployeeJsonView.EmployeeExternal.class)
    private Integer id;

    @NotNull(message = "Employee Name should not be null")
    @Pattern(regexp = "[A-Za-z\\s]{2,}", message = "Name should only contain Alphabets and have atleast 2 Characters")
    @JsonView(EmployeeJsonView.Employee.class)
    @JsonProperty(required = true)
    @ApiModelProperty(notes = "Should be valid Name and have atleast 2 characters")
    private String name;

    @JsonProperty(required = true)
    @ApiModelProperty(notes = "Should be not null and valid id of Employee")
    private Integer managerId;

    @NotNull(message = "Job Title should not be null")
    @JsonView(EmployeeJsonView.Employee.class)
    @JsonProperty(required = true)
    @ApiModelProperty(notes = "Should be not null and a valid Designation Name")
    private String jobTitle;

    public Employee() {
    }

    public Employee(String name, Integer managerId, String jobTitle) {
        this.name = name;
        this.managerId = managerId;
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getName() {
        return name;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", managerId=" + managerId +
                ", jobTitle='" + jobTitle + '\'' +
                '}';
    }
}
