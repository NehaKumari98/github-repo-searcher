package com.neha.githubreposearcher.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import com.neha.githubreposearcher.exception.GithubApiException;
import com.neha.githubreposearcher.exception.RateLimitExceededException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GithubApiClientImpl implements GithubApiClient {

    private final RestClient githubRestClient;

    @Override
    public GithubSearchResult searchRepositories(String query, String language, String sort) {
        String q = (language == null || language.isBlank())
                ? query : query + " language:" + language;
        String sortBy = (sort == null || sort.isBlank()) ? "stars" : sort;

        try {
            return githubRestClient.get()
                    .uri(u -> u.path("/search/repositories")
                            .queryParam("q", "{q}")
                            .queryParam("sort", "{sort}")
                            .queryParam("order", "desc")
                            .queryParam("per_page", 30)
                            .build(q, sortBy))
                    .retrieve()
                    .onStatus(s -> s.value() == 403 || s.value() == 429,
                            (req, res) -> { throw new RateLimitExceededException(); })
                    .onStatus(s -> s.value() == 422,
                            (req, res) -> { throw new GithubApiException(422, "Invalid search query"); })
                    .onStatus(HttpStatusCode::isError,
                            (req, res) -> { throw new GithubApiException(502, "GitHub API error: " + res.getStatusCode()); })
                    .body(GithubSearchResult.class);
        } catch (ResourceAccessException e) {
            throw new GithubApiException(503, "Could not reach GitHub API");
        }
    }
}