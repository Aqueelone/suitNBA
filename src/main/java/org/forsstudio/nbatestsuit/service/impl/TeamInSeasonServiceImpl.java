package org.forsstudio.nbatestsuit.service.impl;

import org.forsstudio.nbatestsuit.repository.TeamInSeasonRepository;
import org.forsstudio.nbatestsuit.repository.search.TeamInSeasonSearchRepository;
import org.forsstudio.nbatestsuit.service.TeamInSeasonService;
import org.forsstudio.nbatestsuit.service.dto.TeamInSeasonDTO;
import org.forsstudio.nbatestsuit.service.mapper.TeamInSeasonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link org.forsstudio.nbatestsuit.domain.TeamInSeason}.
 */
@Service
@Transactional
public class TeamInSeasonServiceImpl implements TeamInSeasonService {

    private final Logger log = LoggerFactory.getLogger(TeamInSeasonServiceImpl.class);

    private final TeamInSeasonRepository teamInSeasonRepository;

    private final TeamInSeasonMapper teamInSeasonMapper;

    private final TeamInSeasonSearchRepository teamInSeasonSearchRepository;

    public TeamInSeasonServiceImpl(
        TeamInSeasonRepository teamInSeasonRepository,
        TeamInSeasonMapper teamInSeasonMapper,
        TeamInSeasonSearchRepository teamInSeasonSearchRepository
    ) {
        this.teamInSeasonRepository = teamInSeasonRepository;
        this.teamInSeasonMapper = teamInSeasonMapper;
        this.teamInSeasonSearchRepository = teamInSeasonSearchRepository;
    }

    @Override
    public Mono<TeamInSeasonDTO> save(TeamInSeasonDTO teamInSeasonDTO) {
        log.debug("Request to save TeamInSeason : {}", teamInSeasonDTO);
        return teamInSeasonRepository
            .save(teamInSeasonMapper.toEntity(teamInSeasonDTO))
            .flatMap(teamInSeasonSearchRepository::save)
            .map(teamInSeasonMapper::toDto);
    }

    @Override
    public Mono<TeamInSeasonDTO> update(TeamInSeasonDTO teamInSeasonDTO) {
        log.debug("Request to update TeamInSeason : {}", teamInSeasonDTO);
        return teamInSeasonRepository
            .save(teamInSeasonMapper.toEntity(teamInSeasonDTO))
            .flatMap(teamInSeasonSearchRepository::save)
            .map(teamInSeasonMapper::toDto);
    }

    @Override
    public Mono<TeamInSeasonDTO> partialUpdate(TeamInSeasonDTO teamInSeasonDTO) {
        log.debug("Request to partially update TeamInSeason : {}", teamInSeasonDTO);

        return teamInSeasonRepository
            .findById(teamInSeasonDTO.getId())
            .map(existingTeamInSeason -> {
                teamInSeasonMapper.partialUpdate(existingTeamInSeason, teamInSeasonDTO);

                return existingTeamInSeason;
            })
            .flatMap(teamInSeasonRepository::save)
            .flatMap(savedTeamInSeason -> {
                teamInSeasonSearchRepository.save(savedTeamInSeason);
                return Mono.just(savedTeamInSeason);
            })
            .map(teamInSeasonMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TeamInSeasonDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TeamInSeasons");
        return teamInSeasonRepository.findAllBy(pageable).map(teamInSeasonMapper::toDto);
    }

    public Mono<Long> countAll() {
        return teamInSeasonRepository.count();
    }

    public Mono<Long> searchCount() {
        return teamInSeasonSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TeamInSeasonDTO> findOne(Long id) {
        log.debug("Request to get TeamInSeason : {}", id);
        return teamInSeasonRepository.findById(id).map(teamInSeasonMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TeamInSeason : {}", id);
        return teamInSeasonRepository.deleteById(id).then(teamInSeasonSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TeamInSeasonDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TeamInSeasons for query {}", query);
        return teamInSeasonSearchRepository.search(query, pageable).map(teamInSeasonMapper::toDto);
    }
}
