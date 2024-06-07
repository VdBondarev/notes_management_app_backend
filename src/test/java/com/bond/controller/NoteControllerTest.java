package com.bond.controller;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bond.dto.NoteRequestDto;
import com.bond.dto.NoteResponseDto;
import com.bond.holder.LinksHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoteControllerTest extends LinksHolder {
    protected static MockMvc mockMvc;
    private static final String CREATED_AT_FIELD = "createdAt";
    private static final String LAST_UPDATED_AT_FIELD = "lastUpdatedAt";
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
            Verify that create endpoint works as expected with a valid request
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
                .ignoringFields(CREATED_AT_FIELD, LAST_UPDATED_AT_FIELD)
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

    @Sql(
            scripts = {
                    DELETE_ALL_NOTES_FILE_PATH, INSERT_FIVE_NOTES_FILE_PATH
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
            Verify that getAll() endpoint works as expected with a valid request
            """)
    public void getAll_ValidRequest_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);

        String pageableContent = objectMapper.writeValueAsString(pageable);

        MvcResult result = mockMvc.perform(get("/notes")
                        .content(pageableContent)
                )
                .andExpect(status().isOk())
                .andReturn();

        NoteResponseDto[] responseDtos = objectMapper.readValue(
                result.getResponse().getContentAsString(), NoteResponseDto[].class
        );

        assertThat(responseDtos).hasSize(5);

        NoteResponseDto expectedFirstResponseDto = new NoteResponseDto(
                1L, "First title", "First content", now(), now()
        );

        assertThat(responseDtos[0]).usingRecursiveComparison()
                .ignoringFields(CREATED_AT_FIELD, LAST_UPDATED_AT_FIELD)
                .isEqualTo(expectedFirstResponseDto);
    }

    @Sql(
            scripts = {
                    DELETE_ALL_NOTES_FILE_PATH, INSERT_ONE_NOTE_FILE_PATH
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
            Verify that getById() endpoint works as expected when passing a valid id
            """)
    public void getById_ValidRequest_Success() throws Exception {
        Long id = 1L;

        MvcResult result = mockMvc.perform(get("/notes/" + id))
                .andExpect(status().isOk())
                .andReturn();

        NoteResponseDto expected = new NoteResponseDto(
                1L, "First title", "First content", now(), now()
        );

        NoteResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), NoteResponseDto.class
        );

        assertThat(actual).usingRecursiveComparison()
                .ignoringFields(CREATED_AT_FIELD, LAST_UPDATED_AT_FIELD)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Verify that getById() endpoint works as expected when passing a non-valid id
            """)
    public void getById_NonValidRequest_Fail() throws Exception {
        // there is no note by this id, expecting EntityNotFoundException
        Long id = 12551L;

        MvcResult result = mockMvc.perform(get("/notes/" + id))
                .andExpect(status().isBadRequest())
                .andReturn();

        String expectedMessage = "Can't find a note with id " + id;
        String actualMessage = result.getResolvedException().getMessage();

        assertThat(result.getResolvedException().getClass())
                .isEqualTo(EntityNotFoundException.class);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Sql(
            scripts = {
                    DELETE_ALL_NOTES_FILE_PATH, INSERT_FIVE_NOTES_FILE_PATH
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
            Verify that delete() endpoint works as expected
            """)
    public void delete_ValidRequest_Success() throws Exception {
        Long id = 1L;

        // deleting first record
        mockMvc.perform(delete("/notes/" + id))
                .andExpect(status().isNoContent());

        Pageable pageable = PageRequest.of(0, 5);

        String content = objectMapper.writeValueAsString(pageable);

        MvcResult result = mockMvc.perform(get("/notes")
                        .content(content)
                )
                .andExpect(status().isOk())
                .andReturn();

        NoteResponseDto[] responseDtos = objectMapper.readValue(
                result.getResponse().getContentAsString(), NoteResponseDto[].class
        );

        // expecting that first record was deleted, and now we have 4 only
        assertThat(responseDtos).hasSize(4);
    }

    @Sql(
            scripts = {
                    DELETE_ALL_NOTES_FILE_PATH, INSERT_FIVE_NOTES_FILE_PATH
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
            Verify that search() endpoint works as expected with a valid request
            """)
    public void search_ValidRequest_Success() throws Exception {
        // it should be case-insensitive and find all the required notes
        NoteRequestDto requestDto = new NoteRequestDto("TitLE", "CONtEnT");
        String title = "TitLE";
        String content = "CONtEnt";

        Pageable pageable = PageRequest.of(0, 5);

        String pageableContent = objectMapper.writeValueAsString(pageable);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("title", List.of(title));
        map.put("content", List.of(content));

        MvcResult result = mockMvc.perform(get("/notes/search")
                        .content(pageableContent)
                        .params(map)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        NoteResponseDto[] responseDtos = objectMapper.readValue(
                result.getResponse().getContentAsString(), NoteResponseDto[].class
        );

        // expecting that all the records are valid
        assertThat(responseDtos).hasSize(5);
    }

    @Test
    @DisplayName("""
            Verify that search() endpoint works as expected with a non-valid request
            """)
    public void search_NonValidRequest_Fail() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);

        String pageableContent = objectMapper.writeValueAsString(pageable);

        // expecting that now a request should not pass
        String title = "";
        String content = "";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("title", List.of(title));
        map.put("content", List.of(content));

        MvcResult result = mockMvc.perform(get("/notes/search")
                        .content(pageableContent)
                        .params(map)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String expectedMessage = "Searching should be done by at least 1 param";
        String actualMessage = result.getResolvedException().getMessage();

        assertThat(actualMessage).isEqualTo(expectedMessage);
        assertThat(result.getResolvedException().getClass())
                .isEqualTo(IllegalArgumentException.class);
    }

    @Sql(
            scripts = {
                    DELETE_ALL_NOTES_FILE_PATH, INSERT_ONE_NOTE_FILE_PATH
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
            Verify that update() endpoint works as expected with a valid request
            """)
    public void update_ValidRequest_Success() throws Exception {
        Long id = 1L;

        NoteRequestDto requestDto = new NoteRequestDto("New title", "New content");

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/notes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isOk())
                .andReturn();

        NoteResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), NoteResponseDto.class
        );
        NoteResponseDto expected = new NoteResponseDto(
                1L, "New title", "New content", now(), now()
        );

        assertThat(actual).usingRecursiveComparison()
                .ignoringFields(CREATED_AT_FIELD, LAST_UPDATED_AT_FIELD)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Verify that update() endpoint works as expected with a non-valid request
            """)
    public void update_NonValidRequest_Fail() throws Exception {
        Long id = 1L;

        NoteRequestDto requestDto = new NoteRequestDto(null, null);

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/notes/" + id)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String expectedMessage = """
                Both title and content cannot be empty
                Update at least one of them
                """;
        String actualMessage = result.getResolvedException().getMessage();

        assertThat(result.getResolvedException().getClass())
                .isEqualTo(IllegalArgumentException.class);
        assertThat(actualMessage).isEqualTo(expectedMessage);

        id = -100L;
        requestDto = new NoteRequestDto("Valid", "Valid");
        content = objectMapper.writeValueAsString(requestDto);
        result = mockMvc.perform(put("/notes/" + id)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        assertThat(result.getResolvedException().getClass())
                .isEqualTo(EntityNotFoundException.class);
    }
}
