package com.twister.organizationcharts.model.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Validated
public class DesignationAdd {
    @Pattern(regexp = "[A-Za-z\\s]{2,}", message = "Name should only contain Alphabets and have atleast 2 Characters")
    private String name;
    @NotNull(message = "Designation Id cannot be null")
    @Min(value = 0, message = "Designation Id must be greater then equal to 1")
    private Integer superiorId;
    @NotNull(message = "parallel cannot be null")
    @JsonProperty(required = true)
    private Boolean parallel;

    public DesignationAdd() {
    }

    public DesignationAdd(String name, Integer superiorId, Boolean parallel) {
        this.name = name;
        this.superiorId = superiorId;
        this.parallel = parallel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSuperiorId() {
        return superiorId;
    }

    public void setSuperiorId(Integer superiorId) {
        this.superiorId = superiorId;
    }

    public Boolean getParallel() {
        return parallel;
    }

    public void setParallel(Boolean parallel) {
        this.parallel = parallel;
    }
}
