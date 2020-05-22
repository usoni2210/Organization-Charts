package com.twister.organizationcharts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twister.organizationcharts.model.Designation;
import com.twister.organizationcharts.model.input.DesignationAdd;
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
public class DesignationControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private MockMvc mockMvc;

    @Test(priority = 1)
    void getListOfDesignations() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/rest/designation")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

        Designation[] designations = mapFromJson(result.getResponse().getContentAsString(), Designation[].class);
        assertEquals(designations.length, 7);
    }

    @Test(dependsOnMethods = {"addDesignation"})
    void getDesignationInfo() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/rest/designation/1")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test(dependsOnMethods = {"addDesignation"})
    void getDesignationInfoWithWrongId() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/rest/designation/20")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test(dependsOnMethods = {"addDesignation"})
    void getDesignationInfoWithNegativeId() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/rest/designation/-1")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addDesignation() throws Exception {
        DesignationAdd designation = new DesignationAdd("XYZ", 7, true);
        String inputJson = mapToJson(designation);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/rest/designation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andExpect(status().isCreated());
    }

    @Test
    void addDesignationWithoutJsonData() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                .post("/rest/designation")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addDesignationWithShortName() throws Exception {
        DesignationAdd designation = new DesignationAdd("X", 7, true);
        String inputJson = mapToJson(designation);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/rest/designation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addDesignationWithNameNull() throws Exception {
        DesignationAdd designation = new DesignationAdd("", 7, true);
        String inputJson = mapToJson(designation);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/rest/designation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addDesignationWithSuperiorIdNull() throws Exception {
        DesignationAdd designation = new DesignationAdd("XYZ", null, true);
        String inputJson = mapToJson(designation);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/rest/designation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addDesignationWithParallelNull() throws Exception {
        DesignationAdd designation = new DesignationAdd("XYZ", 7, null);
        String inputJson = mapToJson(designation);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/rest/designation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(inputJson))
                .andExpect(status().isBadRequest());
    }

    @Test(dependsOnMethods = {"addDesignation"})
    void deleteDesignation() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/rest/designation/8")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent());
    }

    @Test(dependsOnMethods = {"addDesignation"})
    void deleteDesignationWithEmployee() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/rest/designation/1")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test(dependsOnMethods = {"addDesignation"})
    void deleteDesignationInfoWithWrongId() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/rest/designation/20")
                .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test(dependsOnMethods = {"addDesignation"})
    void deleteDesignationInfoWithNegativeId() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/rest/designation/1")
                .accept(MediaType.APPLICATION_JSON_VALUE);
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