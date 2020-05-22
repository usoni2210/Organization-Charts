package com.twister.organizationcharts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twister.organizationcharts.model.input.EmployeeAdd;
import com.twister.organizationcharts.model.input.EmployeeReplace;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertEquals;

@SpringBootTest()
@AutoConfigureMockMvc
public class EmployeeControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

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
    void addEmployeeWithShortName() throws Exception {
        EmployeeAdd employee = new EmployeeAdd("U", 1, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithNameNull() throws Exception {
        EmployeeAdd employee = new EmployeeAdd("", 1, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithManagerNull() throws Exception {
        EmployeeAdd employee = new EmployeeAdd("Umesh", null, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithJobTitleNull() throws Exception {
        EmployeeAdd employee = new EmployeeAdd("Umesh", 1, null);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithWrongDesignation() throws Exception {
        EmployeeAdd employee = new EmployeeAdd("Umesh", 1, "HOD");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithWrongManagerId() throws Exception {
        EmployeeAdd employee = new EmployeeAdd("Umesh", 100, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithDirectorUnderSupervisor() throws Exception {
        EmployeeAdd employee = new EmployeeAdd("Umesh", 3, "Director");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithManagerWithoutSupervisor() throws Exception {
        EmployeeAdd employee = new EmployeeAdd("Umesh", 0, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithRankMistake() throws Exception {
        EmployeeAdd employee = new EmployeeAdd("Umesh", 5, "Manager");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void addEmployeeWithNewDirector() throws Exception {
        EmployeeAdd employee = new EmployeeAdd("Umesh", 0, "Director");

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/rest/employees")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();


        System.out.println(mvcResult.getResponse().getContentAsString());

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
    }

    @Test
    void addEmployeeWithJsonData() throws Exception {
        EmployeeAdd employee = new EmployeeAdd("Umesh", 1, "Intern");

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
        EmployeeReplace employee = new EmployeeReplace(null, 1, "Lead", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithJobTitleNull() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", 1, null, true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithManagerIdNull() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", null, "Manager", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    void putEmployeeWithReplaceNull() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", 1, "Lead", null);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithWrongEmployeeId() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", 1, "Lead", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/100")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
    }

    @Test
    void putEmployeeWithWShortName() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("U", 1, "Lead", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithWrongJobTitle() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", 1, "Head", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithWrongManagerId() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", 100, "Lead", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithDirectorUnderSupervisor() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", 1, "Director", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithoutSupervisor() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", 0, "Lead", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);

    }

    @Test
    void putEmployeeWithWrongRank() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", 3, "Manager", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithLowerRankSupervisor() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", 1, "Developer", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/3")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(400, status);
    }

    @Test
    void putEmployeeWithReplaceTrue() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", 1, "Manager", true);

        String inputJson = mapToJson(employee);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.put("/rest/employees/4")
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    void putEmployeeWithReplaceFalse() throws Exception {
        EmployeeReplace employee = new EmployeeReplace("Umesh", 1, "Manager", false);

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
}