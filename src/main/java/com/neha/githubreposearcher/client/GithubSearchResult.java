package com.neha.githubreposearcher.client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubSearchResult(
        @JsonProperty("total_count") long totalCount,
        List<Item> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(
            long id,
            String name,
            String description,
            Owner owner,
            String language,
            @JsonProperty("stargazers_count") long stars,
            @JsonProperty("forks_count") long forks,
            @JsonProperty("open_issues_count") long openIssues,
            @JsonProperty("watchers_count") long watchers,
            @JsonProperty("html_url") String htmlUrl,
            @JsonProperty("updated_at") String updatedAt) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Owner(String login) {
        
    }
}