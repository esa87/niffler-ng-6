package guru.qa.niffler.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Random;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository usersRepository;

    @Test
    void currentUserEndpoint() throws Exception {
        UserEntity userDataEntity = new UserEntity();
        userDataEntity.setUsername("dima");
        userDataEntity.setCurrency(CurrencyValues.RUB);
        usersRepository.save(userDataEntity);

        mockMvc.perform(get("/internal/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "dima")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("dima"));
    }


    @Test
    void editUserEndpoint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        UserEntity userDataEntity = new UserEntity();
        userDataEntity.setUsername("test");
        userDataEntity.setFullname("Ivan");
        userDataEntity.setCurrency(CurrencyValues.RUB);
        usersRepository.save(userDataEntity);

        JsonNode userNode = mapper.readTree(mockMvc.perform(get("/internal/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("username", "test")
                )
                .andReturn().getResponse().getContentAsString());

        ((ObjectNode) userNode).put("fullname", "Stepan");

        mockMvc.perform(post("/internal/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userNode))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullname").value("Stepan"));
    }
}