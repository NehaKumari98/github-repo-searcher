package com.neha.githubreposearcher.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.neha.githubreposearcher.entity.GithubRepository;


public interface GithubRepositoryRepository
        extends JpaRepository<GithubRepository, Long>,
                JpaSpecificationExecutor<GithubRepository> {
}