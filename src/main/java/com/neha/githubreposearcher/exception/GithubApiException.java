package com.neha.githubreposearcher.exception;

public class GithubApiException extends RuntimeException {
    private final int status;

    public GithubApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() { return status; }
}