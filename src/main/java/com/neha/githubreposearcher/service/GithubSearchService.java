package com.neha.githubreposearcher.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.githubreposearcher.client.GithubApiClient;
import com.neha.githubreposearcher.client.GithubSearchResult;
import com.neha.githubreposearcher.dto.RepositoryDto;
import com.neha.githubreposearcher.dto.SearchRequest;
import com.neha.githubreposearcher.entity.GithubRepository;
import com.neha.githubreposearcher.repository.GithubRepositoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GithubSearchService {

    private final GithubApiClient githubApiClient;
    private final GithubRepositoryRepository repository;

    @Transactional
    public List<RepositoryDto> searchAndSave(SearchRequest request) {
        GithubSearchResult result = githubApiClient
                .searchRepositories(request.query(), request.language(), request.sort());

        if (result == null || result.items() == null || result.items().isEmpty()) {
            return List.of();
        }

        Instant now = Instant.now();
        List<GithubRepository> entities = result.items().stream()
                .map(item -> toEntity(item, now))
                .toList();

        // id is GitHub's id, so saveAll inserts new rows and updates existing ones (upsert)
        return repository.saveAll(entities).stream().map(RepositoryDto::from).toList();
    }

    private GithubRepository toEntity(GithubSearchResult.Item i, Instant now) {
        return GithubRepository.builder()
                .id(i.id())
                .name(i.name())
                .description(i.description())
                .ownerName(i.owner() != null ? i.owner().login() : null)
                .language(i.language())
                .stars(i.stars())
                .forks(i.forks())
                .openIssues(i.openIssues())
                .watchers(i.watchers())
                .htmlUrl(i.htmlUrl())
                .lastUpdated(i.updatedAt() != null ? Instant.parse(i.updatedAt()) : null)
                .fetchedAt(now)
                .build();
    }
}