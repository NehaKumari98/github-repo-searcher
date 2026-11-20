package com.neha.githubreposearcher.exception;

public class RateLimitExceededException extends GithubApiException {
    public RateLimitExceededException() {
        super(429, "GitHub API rate limit exceeded. Please try again later.");
    }
}