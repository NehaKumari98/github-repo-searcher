package com.neha.githubreposearcher.dto;

import java.util.List;

public record SearchResponse(String message, List<RepositoryDto> repositories) {}