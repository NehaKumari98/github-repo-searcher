package com.neha.githubreposearcher.dto;

import java.time.Instant;

import com.neha.githubreposearcher.entity.GithubRepository;

public record RepositoryDto(
        Long id,
        String name,
        String description,
        String owner,
        String language,
        Long stars,
        Long forks,
        Instant lastUpdated
) {
    public static RepositoryDto from(GithubRepository r) {
        return new RepositoryDto(r.getId(), r.getName(), r.getDescription(),
                r.getOwnerName(), r.getLanguage(), r.getStars(),
                r.getForks(), r.getLastUpdated());
    }
}