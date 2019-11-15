package com.twister.organizationcharts.Model;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Validated
public class DesignationAdd {
    @Pattern(regexp = "[A-Za-z\\s]{2,}", message = "Name should only contain Alphabets and have atleast 2 Characters")
    private String name;
    @Min(value = 1, message = "Level minimum value should be 1")
    private int level;

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
}
