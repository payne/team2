package demo.team2.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import demo.team2.config.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;


public class PersonResourceTest extends BaseIT {

    @Test
    @Sql("/data/personData.sql")
    void getAllPersons_success() throws Exception {
        mockMvc.perform(get("/api/persons")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(((long)1100)));
    }

    @Test
    @Sql("/data/personData.sql")
    void getPerson_success() throws Exception {
        mockMvc.perform(get("/api/persons/1100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personId").value("Duis autem vel."));
    }

    @Test
    void getPerson_notFound() throws Exception {
        mockMvc.perform(get("/api/persons/1766")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception").value("NotFoundException"));
    }

    @Test
    void createPerson_success() throws Exception {
        mockMvc.perform(post("/api/persons")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/personDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertEquals(1, personRepository.count());
    }

    @Test
    void createPerson_missingField() throws Exception {
        mockMvc.perform(post("/api/persons")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/personDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("personId"));
    }

    @Test
    @Sql("/data/personData.sql")
    void updatePerson_success() throws Exception {
        mockMvc.perform(put("/api/persons/1100")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/personDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals("Nam liber tempor.", personRepository.findById(((long)1100)).get().getPersonId());
        assertEquals(2, personRepository.count());
    }

    @Test
    @Sql("/data/personData.sql")
    void deletePerson_success() throws Exception {
        mockMvc.perform(delete("/api/persons/1100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertEquals(1, personRepository.count());
    }

}
