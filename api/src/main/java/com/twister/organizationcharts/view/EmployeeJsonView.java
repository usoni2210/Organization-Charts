package com.twister.organizationcharts.view;

public class EmployeeJsonView {
    // To Display Employee id name and JobTitle
    public static class EmployeeView {
    }

    // To Display Employee id, name and jobTitle
    public static class EmployeeExternal extends EmployeeView {
    }

    // To Display Employee id, name, jobTile, managerId
    public static class EmployeeInternal extends EmployeeView {
    }
}
