package com.neha.githubreposearcher.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.neha.githubreposearcher.dto.RepositoryDto;
import com.neha.githubreposearcher.entity.GithubRepository;
import com.neha.githubreposearcher.repository.GithubRepositoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RepositoryQueryService {

    private final GithubRepositoryRepository repository;

    @Transactional(readOnly = true)
    public List<RepositoryDto> find(String language, Long minStars, String sort) {
        String field = switch (sort.toLowerCase()) {
            case "stars" -> "stars";
            case "forks" -> "forks";
            case "updated" -> "lastUpdated";
            default -> throw new IllegalArgumentException(
                    "sort must be one of: stars, forks, updated");
        };

        Specification<GithubRepository> spec = (root, query, cb) -> cb.conjunction();

        if (language != null && !language.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(cb.lower(root.get("language")), language.toLowerCase()));
        }
        if (minStars != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("stars"), minStars));
        }

        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, field))
                .stream().map(RepositoryDto::from).toList();
    }
}