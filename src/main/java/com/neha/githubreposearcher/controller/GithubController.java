package com.neha.githubreposearcher.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.neha.githubreposearcher.dto.RepositoryDto;
import com.neha.githubreposearcher.dto.RepositoryListResponse;
import com.neha.githubreposearcher.dto.SearchRequest;
import com.neha.githubreposearcher.dto.SearchResponse;
import com.neha.githubreposearcher.service.GithubSearchService;
import com.neha.githubreposearcher.service.RepositoryQueryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GithubController {

    private final GithubSearchService searchService;
    private final RepositoryQueryService queryService;

    @PostMapping("/search")
    public ResponseEntity<SearchResponse> search(@Valid @RequestBody SearchRequest request) {
        List<RepositoryDto> repos = searchService.searchAndSave(request);
        String message = repos.isEmpty()
                ? "No repositories found"
                : "Repositories fetched and saved successfully";
        return ResponseEntity.ok(new SearchResponse(message, repos));
    }

    @GetMapping("/repositories")
    public ResponseEntity<RepositoryListResponse> getRepositories(
            @RequestParam(required = false) String language,
            @RequestParam(required = false) Long minStars,
            @RequestParam(defaultValue = "stars") String sort) {
        return ResponseEntity.ok(
                new RepositoryListResponse(queryService.find(language, minStars, sort)));
    }
}