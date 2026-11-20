
package com.neha.githubreposearcher.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "github_repositories", indexes = {
        @Index(name = "idx_repo_language", columnList = "language"),
        @Index(name = "idx_repo_stars", columnList = "stars")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GithubRepository {

    @Id
    private Long id; // GitHub's own repo id, not auto-generated

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String ownerName;
    private String language;
    private Long stars;
    private Long forks;
    private Long openIssues;
    private Long watchers;
    private String htmlUrl;
    private Instant lastUpdated;
    private Instant fetchedAt;
}
