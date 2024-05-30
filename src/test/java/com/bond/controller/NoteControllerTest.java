package com.bond.controller;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bond.dto.NoteRequestDto;
import com.bond.dto.NoteResponseDto;
import com.bond.holder.LinksHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoteControllerTest extends LinksHolder {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
    }

    @Sql(
            scripts = {
                    DELETE_ALL_NOTES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_NOTES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("""
            Verify that create endpoint works as expected with valid request
            """)
    public void create_ValidRequest_Success() throws Exception {
        NoteRequestDto requestDto = new NoteRequestDto("Test title", "Test content");

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/notes")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        NoteResponseDto expected = new NoteResponseDto(
                1L, "Test title", "Test content", now(), now()
        );

        NoteResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), NoteResponseDto.class
        );

        /*
         since we always set lastUpdatedAt and createdAt as now()
         we will always have false when comparing some entities without additional configuration
         (like setting the same time)
         so we ignore these field
         */
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("createdAt", "lastUpdatedAt")
                .isEqualTo(expected);
    }

    @Sql(
            scripts = {
                    DELETE_ALL_NOTES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Test
    @DisplayName("""
            Verify that create() endpoint works as expected when passing non-valid params
            """)
    public void create_NonValidRequest_Fail() throws Exception {
        // at first, we will pass null to endpoint and see if it is @HttpStatus.BAD_REQUEST
        NoteRequestDto requestDto = null;
        mockMvc.perform(post("/notes")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        // now requestDto is not valid and it should not be saved to database
        requestDto = new NoteRequestDto("", "");
        mockMvc.perform(post("/notes")
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        // checking whether database empty or not
        // should be empty, because we are not supposed to persist any data to it
        MvcResult result = mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andReturn();

        NoteResponseDto[] responseDtos = objectMapper.readValue(
                result.getResponse().getContentAsString(), NoteResponseDto[].class
        );

        // asserting that result array is empty
        assertThat(responseDtos).hasSize(0);
    }
}
