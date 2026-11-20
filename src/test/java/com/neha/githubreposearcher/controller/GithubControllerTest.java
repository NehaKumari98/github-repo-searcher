package com.neha.githubreposearcher.controller;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.neha.githubreposearcher.dto.RepositoryDto;
import com.neha.githubreposearcher.exception.GlobalExceptionHandler;
import com.neha.githubreposearcher.exception.RateLimitExceededException;
import com.neha.githubreposearcher.service.GithubSearchService;
import com.neha.githubreposearcher.service.RepositoryQueryService;

@ExtendWith(MockitoExtension.class)
class GithubControllerTest {

    @Mock GithubSearchService searchService;
    @Mock RepositoryQueryService queryService;
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new GithubController(searchService, queryService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private RepositoryDto dto() {
        return new RepositoryDto(123456L, "spring-boot-example", "An example",
                "user123", "Java", 450L, 120L, Instant.parse("2024-01-01T12:00:00Z"));
    }

    @Test
    void search_validRequest_returns200WithMessage() throws Exception {
        when(searchService.searchAndSave(any())).thenReturn(List.of(dto()));

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"spring boot\",\"language\":\"Java\",\"sort\":\"stars\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Repositories fetched and saved successfully"))
                .andExpect(jsonPath("$.repositories[0].id").value(123456));
    }

    @Test
    void search_emptyResult_returnsNoRepositoriesMessage() throws Exception {
        when(searchService.searchAndSave(any())).thenReturn(List.of());

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"zzzz\",\"sort\":\"stars\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("No repositories found"));
    }

    @Test
    void search_blankQuery_returns400() throws Exception {
        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"\",\"sort\":\"stars\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_invalidSort_returns400() throws Exception {
        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"spring\",\"sort\":\"abc\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void search_rateLimited_returns429() throws Exception {
        when(searchService.searchAndSave(any())).thenThrow(new RateLimitExceededException());

        mockMvc.perform(post("/api/github/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"query\":\"spring\",\"sort\":\"stars\"}"))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void getRepositories_returnsStoredRepositories() throws Exception {
        when(queryService.find("Java", 100L, "stars")).thenReturn(List.of(dto()));

        mockMvc.perform(get("/api/github/repositories")
                        .param("language", "Java")
                        .param("minStars", "100")
                        .param("sort", "stars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repositories[0].name").value("spring-boot-example"));
    }
}