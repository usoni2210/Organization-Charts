package com.twister.organizationcharts.Controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twister.organizationcharts.Model.Employee;
import com.twister.organizationcharts.Model.ReplaceWrapper;
import com.twister.organizationcharts.Repository.DesignationRepo;
import com.twister.organizationcharts.Repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertEquals;

@SpringBootTest()
@AutoConfigureMockMvc
public class EmployeeControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private DesignationRepo designationRepo;


    //Get All Employee Details
    @Test
    void getAllEmployees() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/rest/employees")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    //Get Particular Employee Info
    @Test
    void getEmployeeInfo() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/employees/2");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    void getWrongEmployeeId() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/employees/200");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void getNegativeEmployeeId() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/employees/-1");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }


    // Add Employee Info
    @Test
    void addEmployeeWithoutJsonData() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/rest/employees").accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addEmployeeWithEmployeeId() throws Exception {
        Employee employee = new Employee("Umesh", 0, "Director");
        employee.setId(22);

        String inputJson = mapToJson(employee);
        mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addEmployeeWithShortName() throws Exception {
        Employee employee = new Employee("U", 1, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithNameNull() throws Exception {
        Employee employee = new Employee("", 1, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithManagerNull() throws Exception {
        Employee employee = new Employee("Umesh", null, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithJobTitleNull() throws Exception {
        Employee employee = new Employee("Umesh", 1, null);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithWrongDesignation() throws Exception {
        Employee employee = new Employee("Umesh", 1, "HOD");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithWrongManagerId() throws Exception {
        Employee employee = new Employee("Umesh", 100, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithDirectorUnderSupervisor() throws Exception {
        Employee employee = new Employee("Umesh", 3, "Director");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithManagerWithoutSupervisor() throws Exception {
        Employee employee = new Employee("Umesh", 0, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithRankMistake() throws Exception {
        Employee employee = new Employee("Umesh", 5, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithNewDirector() throws Exception {
        Employee employee = new Employee("Umesh", -1, "Director");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
    }

    @Test
    void addEmployeeWithJsonData() throws Exception {
        Employee employee = new Employee("Umesh", 1, "Intern");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
    }


    // Update or Replace Employee
    @Test
    void putEmployeeWithoutJsonData() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/rest/employees/4")
                .accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void putEmployeeWithNameNull() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper(null, 1, "Lead", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithJobTitleNull() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 1, null, true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithManagerIdNull() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", null, "Manager", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    void putEmployeeWithReplaceNull() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 1, "Lead", null);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithEmployeeIdInJson() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 1, "Lead", true);
        employee.setId(4);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithWrongEmployeeId() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 1, "Lead", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/100")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
    }

    @Test
    void putEmployeeWithWShortName() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("U", 1, "Lead", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithWrongJobTitle() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 1, "Head", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithWrongManagerId() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 100, "Lead", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithDirectorUnderSupervisor() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 1, "Director", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithoutSupervisor() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 0, "Lead", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);

    }

    @Test
    void putEmployeeWithWrongRank() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 3, "Manager", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithLowerRankSupervisor() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 1, "Developer", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/3")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithReplaceTrue() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 1, "Manager", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    void putEmployeeWithReplaceFalse() throws Exception {
        ReplaceWrapper employee = new ReplaceWrapper("Umesh", 1, "Manager", false);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }


    // Delete Employee
    @Test
    void deleteEmployee() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/employees/6");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDirector() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/employees/1").accept(MediaType.APPLICATION_JSON);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteWrongEmployeeId() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/employees/200");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNegativeEmployeeId() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/rest/employees/-1");
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    // Functions
    private String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    private <T> T mapFromJson(String json, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }
}