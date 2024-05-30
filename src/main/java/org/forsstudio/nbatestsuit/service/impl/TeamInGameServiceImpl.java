package org.forsstudio.nbatestsuit.service.impl;

import org.forsstudio.nbatestsuit.repository.TeamInGameRepository;
import org.forsstudio.nbatestsuit.repository.search.TeamInGameSearchRepository;
import org.forsstudio.nbatestsuit.service.TeamInGameService;
import org.forsstudio.nbatestsuit.service.dto.TeamInGameDTO;
import org.forsstudio.nbatestsuit.service.mapper.TeamInGameMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link org.forsstudio.nbatestsuit.domain.TeamInGame}.
 */
@Service
@Transactional
public class TeamInGameServiceImpl implements TeamInGameService {

    private final Logger log = LoggerFactory.getLogger(TeamInGameServiceImpl.class);

    private final TeamInGameRepository teamInGameRepository;

    private final TeamInGameMapper teamInGameMapper;

    private final TeamInGameSearchRepository teamInGameSearchRepository;

    public TeamInGameServiceImpl(
        TeamInGameRepository teamInGameRepository,
        TeamInGameMapper teamInGameMapper,
        TeamInGameSearchRepository teamInGameSearchRepository
    ) {
        this.teamInGameRepository = teamInGameRepository;
        this.teamInGameMapper = teamInGameMapper;
        this.teamInGameSearchRepository = teamInGameSearchRepository;
    }

    @Override
    public Mono<TeamInGameDTO> save(TeamInGameDTO teamInGameDTO) {
        log.debug("Request to save TeamInGame : {}", teamInGameDTO);
        return teamInGameRepository
            .save(teamInGameMapper.toEntity(teamInGameDTO))
            .flatMap(teamInGameSearchRepository::save)
            .map(teamInGameMapper::toDto);
    }

    @Override
    public Mono<TeamInGameDTO> update(TeamInGameDTO teamInGameDTO) {
        log.debug("Request to update TeamInGame : {}", teamInGameDTO);
        return teamInGameRepository
            .save(teamInGameMapper.toEntity(teamInGameDTO))
            .flatMap(teamInGameSearchRepository::save)
            .map(teamInGameMapper::toDto);
    }

    @Override
    public Mono<TeamInGameDTO> partialUpdate(TeamInGameDTO teamInGameDTO) {
        log.debug("Request to partially update TeamInGame : {}", teamInGameDTO);

        return teamInGameRepository
            .findById(teamInGameDTO.getId())
            .map(existingTeamInGame -> {
                teamInGameMapper.partialUpdate(existingTeamInGame, teamInGameDTO);

                return existingTeamInGame;
            })
            .flatMap(teamInGameRepository::save)
            .flatMap(savedTeamInGame -> {
                teamInGameSearchRepository.save(savedTeamInGame);
                return Mono.just(savedTeamInGame);
            })
            .map(teamInGameMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TeamInGameDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TeamInGames");
        return teamInGameRepository.findAllBy(pageable).map(teamInGameMapper::toDto);
    }

    public Mono<Long> countAll() {
        return teamInGameRepository.count();
    }

    public Mono<Long> searchCount() {
        return teamInGameSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TeamInGameDTO> findOne(Long id) {
        log.debug("Request to get TeamInGame : {}", id);
        return teamInGameRepository.findById(id).map(teamInGameMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TeamInGame : {}", id);
        return teamInGameRepository.deleteById(id).then(teamInGameSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TeamInGameDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TeamInGames for query {}", query);
        return teamInGameSearchRepository.search(query, pageable).map(teamInGameMapper::toDto);
    }
}
