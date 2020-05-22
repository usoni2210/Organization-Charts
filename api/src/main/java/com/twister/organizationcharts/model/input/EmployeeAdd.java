package com.twister.organizationcharts.model.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class EmployeeAdd {

    @NotNull(message = "Employee Name should not be null")
    @Pattern(regexp = "[A-Za-z\\s]{2,}", message = "Name should only contain Alphabets and have atleast 2 Characters")
    @JsonProperty(required = true)
    @ApiModelProperty(notes = "Should be valid Name and have atleast 2 characters")
    private String name;

    @ApiModelProperty(notes = "Should be not null and valid id of Employee")
    private Integer managerId;

    @NotNull(message = "Job Title should not be null")
    @ApiModelProperty(notes = "Should be not null and a valid Designation Name")
    private String jobTitle;

    EmployeeAdd() {
    }

    public EmployeeAdd(String name, Integer managerId, String jobTitle) {
        this.name = name;
        this.managerId = managerId;
        this.jobTitle = jobTitle;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    @Override
    public String toString() {
        return "EmployeeAdd{" +
                "name='" + name + '\'' +
                ", managerId=" + managerId +
                ", jobTitle='" + jobTitle + '\'' +
                '}';
    }
}
