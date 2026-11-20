package com.neha.githubreposearcher.client;

public interface GithubApiClient {
    GithubSearchResult searchRepositories(String query, String language, String sort);
}