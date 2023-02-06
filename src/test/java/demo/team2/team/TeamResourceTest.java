package demo.team2.team;

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


public class TeamResourceTest extends BaseIT {

    @Test
    @Sql("/data/teamData.sql")
    void getAllTeams_success() throws Exception {
        mockMvc.perform(get("/api/teams")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].id").value(((long)1000)));
    }

    @Test
    @Sql("/data/teamData.sql")
    void getTeam_success() throws Exception {
        mockMvc.perform(get("/api/teams/1000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamId").value("Sed ut perspiciatis."));
    }

    @Test
    void getTeam_notFound() throws Exception {
        mockMvc.perform(get("/api/teams/1666")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception").value("NotFoundException"));
    }

    @Test
    void createTeam_success() throws Exception {
        mockMvc.perform(post("/api/teams")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/teamDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertEquals(1, teamRepository.count());
    }

    @Test
    void createTeam_missingField() throws Exception {
        mockMvc.perform(post("/api/teams")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/teamDTORequest_missingField.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("teamId"));
    }

    @Test
    @Sql("/data/teamData.sql")
    void updateTeam_success() throws Exception {
        mockMvc.perform(put("/api/teams/1000")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(readResource("/requests/teamDTORequest.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertEquals("Duis autem vel.", teamRepository.findById(((long)1000)).get().getTeamId());
        assertEquals(2, teamRepository.count());
    }

    @Test
    @Sql("/data/teamData.sql")
    void deleteTeam_success() throws Exception {
        mockMvc.perform(delete("/api/teams/1000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertEquals(1, teamRepository.count());
    }

}
