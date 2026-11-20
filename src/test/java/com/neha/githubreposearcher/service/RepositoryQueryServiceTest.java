package com.neha.githubreposearcher.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.neha.githubreposearcher.dto.RepositoryDto;
import com.neha.githubreposearcher.entity.GithubRepository;
import com.neha.githubreposearcher.repository.GithubRepositoryRepository;

@ExtendWith(MockitoExtension.class)
class RepositoryQueryServiceTest {

    @Mock GithubRepositoryRepository repository;
    @InjectMocks RepositoryQueryService service;

    @Test
    void find_invalidSort_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> service.find("Java", 100L, "abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sort must be one of");
    }

    @Test
    void find_sortByUpdated_usesLastUpdatedFieldDescending() {
        when(repository.findAll(ArgumentMatchers.<Specification<GithubRepository>>any(), any(Sort.class)))
                .thenReturn(List.of());

        service.find(null, null, "updated");

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(repository).findAll(
                ArgumentMatchers.<Specification<GithubRepository>>any(), sortCaptor.capture());
        assertThat(sortCaptor.getValue().toString()).contains("lastUpdated: DESC");
    }

    @Test
    void find_returnsMappedDtos() {
        GithubRepository entity = GithubRepository.builder()
                .id(1L).name("demo").ownerName("neha").language("Java")
                .stars(500L).forks(5L).build();
        when(repository.findAll(ArgumentMatchers.<Specification<GithubRepository>>any(), any(Sort.class)))
                .thenReturn(List.of(entity));

        List<RepositoryDto> result = service.find("Java", 100L, "stars");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("demo");
        assertThat(result.get(0).owner()).isEqualTo("neha");
    }
}