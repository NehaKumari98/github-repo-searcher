package com.neha.githubreposearcher.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.neha.githubreposearcher.client.GithubApiClient;
import com.neha.githubreposearcher.client.GithubSearchResult;
import com.neha.githubreposearcher.dto.RepositoryDto;
import com.neha.githubreposearcher.dto.SearchRequest;
import com.neha.githubreposearcher.entity.GithubRepository;
import com.neha.githubreposearcher.repository.GithubRepositoryRepository;

@ExtendWith(MockitoExtension.class)
class GithubSearchServiceTest {

    @Mock GithubApiClient githubApiClient;
    @Mock GithubRepositoryRepository repository;
    @InjectMocks GithubSearchService service;
    @Captor ArgumentCaptor<List<GithubRepository>> captor;

    private GithubSearchResult.Item item(long id, String name, long stars) {
        return new GithubSearchResult.Item(id, name, "desc",
                new GithubSearchResult.Owner("neha"), "Java",
                stars, 10L, 2L, stars,
                "https://github.com/neha/" + name, "2026-11-20T10:00:00Z");
    }

    @Test
    void searchAndSave_savesAndReturnsMappedRepositories() {
        when(githubApiClient.searchRepositories("spring boot", "Java", "stars"))
                .thenReturn(new GithubSearchResult(1, List.of(item(123456L, "demo", 450))));
        when(repository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<RepositoryDto> result =
                service.searchAndSave(new SearchRequest("spring boot", "Java", "stars"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(123456L);
        assertThat(result.get(0).owner()).isEqualTo("neha");
        assertThat(result.get(0).stars()).isEqualTo(450L);
    }

    @Test
    void searchAndSave_usesGithubIdAsPrimaryKey_soExistingRowsAreUpdated() {
        when(githubApiClient.searchRepositories(any(), any(), any()))
                .thenReturn(new GithubSearchResult(2,
                        List.of(item(1L, "a", 5), item(2L, "b", 9))));
        when(repository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        service.searchAndSave(new SearchRequest("x", null, "stars"));

        verify(repository).saveAll(captor.capture());
        assertThat(captor.getValue())
                .extracting(GithubRepository::getId)
                .containsExactly(1L, 2L);
    }

    @Test
    void searchAndSave_emptyResult_returnsEmptyListAndSavesNothing() {
        when(githubApiClient.searchRepositories(any(), any(), any()))
                .thenReturn(new GithubSearchResult(0, List.of()));

        List<RepositoryDto> result =
                service.searchAndSave(new SearchRequest("zzzz", null, "stars"));

        assertThat(result).isEmpty();
        verifyNoInteractions(repository);
    }
}