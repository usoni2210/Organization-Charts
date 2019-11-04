package com.twister.organizationcharts.Model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;


@Validated
public class ReplaceWrapper extends Employee {
    @NotNull(message = "replace value cannot be null")
    @JsonProperty(required = true)
    private Boolean replace;

    public ReplaceWrapper(String name, Integer managerId, String jobTitle, Boolean replace) {
        super(name, managerId, jobTitle);
        this.replace = replace;
    }

    public Boolean getReplace() {
        return replace;
    }

    public void setReplace(Boolean replace) {
        this.replace = replace;
    }

    @Override
    public String toString() {
        return "ReplaceWrapper{" +
                "replace=" + replace +
                '}';
    }
}
