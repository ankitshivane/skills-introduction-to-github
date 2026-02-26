package com.example.crudapp;

import com.example.crudapp.model.Item;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createItem_shouldReturnCreated() throws Exception {
        Item item = new Item(null, "Laptop", "A portable computer");

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("A portable computer"));
    }

    @Test
    void getAllItems_shouldReturnList() throws Exception {
        Item item = new Item(null, "Phone", "A smartphone");
        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Phone"));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        Item item = new Item(null, "Tablet", "A tablet device");
        String response = mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Item created = objectMapper.readValue(response, Item.class);

        mockMvc.perform(get("/api/items/" + created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tablet"));
    }

    @Test
    void getItemById_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(get("/api/items/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItem_shouldReturnUpdatedItem() throws Exception {
        Item item = new Item(null, "Monitor", "A display monitor");
        String response = mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Item created = objectMapper.readValue(response, Item.class);
        created.setName("4K Monitor");

        mockMvc.perform(put("/api/items/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(created)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("4K Monitor"));
    }

    @Test
    void updateItem_notFound_shouldReturn404() throws Exception {
        Item item = new Item(null, "Ghost", "Does not exist");
        mockMvc.perform(put("/api/items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteItem_shouldReturn204() throws Exception {
        Item item = new Item(null, "Keyboard", "A mechanical keyboard");
        String response = mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Item created = objectMapper.readValue(response, Item.class);

        mockMvc.perform(delete("/api/items/" + created.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/items/" + created.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteItem_notFound_shouldReturn404() throws Exception {
        mockMvc.perform(delete("/api/items/999"))
                .andExpect(status().isNotFound());
    }
}
