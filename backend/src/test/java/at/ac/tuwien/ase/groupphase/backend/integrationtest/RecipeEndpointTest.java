package at.ac.tuwien.ase.groupphase.backend.integrationtest;

import at.ac.tuwien.ase.groupphase.backend.controller.RecipeEndpoint;
import at.ac.tuwien.ase.groupphase.backend.dto.RecipeDto;
import at.ac.tuwien.ase.groupphase.backend.mapper.RecipeMapper;
import at.ac.tuwien.ase.groupphase.backend.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RecipeEndpointTest {
    // ToDo: Extract to external constants file
    String RECIPE_BASE_URI = "/api/v1/recipe";
    Long RECIPE_ID = 101233L;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    RecipeEndpoint recipeEndpoint;
    @Autowired
    RecipeMapper recipeMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SecurityProperties securityProperties;

    @Test
    public void givenData_getRecipeById() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(RECIPE_BASE_URI + "/" + RECIPE_ID))
                // .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken())
                .andDo(print()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        RecipeDto dto = objectMapper.readValue(response.getContentAsString(), RecipeDto.class);

        assertNotNull(dto);
        assertAll(() -> assertEquals(RECIPE_ID, dto.getId()), () -> assertFalse(dto.getIngredients().isEmpty()));
    }

    @Test
    public void givenData_getRecipeWithInvalidId() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(get(RECIPE_BASE_URI + "/" + -1)).andDo(print()).andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        RecipeDto dto = objectMapper.readValue(response.getContentAsString(), RecipeDto.class);

        assertNull(dto);
    }
}
