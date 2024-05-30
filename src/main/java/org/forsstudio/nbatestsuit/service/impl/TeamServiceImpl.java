package org.forsstudio.nbatestsuit.service.impl;

import org.forsstudio.nbatestsuit.repository.TeamRepository;
import org.forsstudio.nbatestsuit.repository.search.TeamSearchRepository;
import org.forsstudio.nbatestsuit.service.TeamService;
import org.forsstudio.nbatestsuit.service.dto.TeamDTO;
import org.forsstudio.nbatestsuit.service.mapper.TeamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link org.forsstudio.nbatestsuit.domain.Team}.
 */
@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    private final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final TeamRepository teamRepository;

    private final TeamMapper teamMapper;

    private final TeamSearchRepository teamSearchRepository;

    public TeamServiceImpl(TeamRepository teamRepository, TeamMapper teamMapper, TeamSearchRepository teamSearchRepository) {
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
        this.teamSearchRepository = teamSearchRepository;
    }

    @Override
    public Mono<TeamDTO> save(TeamDTO teamDTO) {
        log.debug("Request to save Team : {}", teamDTO);
        return teamRepository.save(teamMapper.toEntity(teamDTO)).flatMap(teamSearchRepository::save).map(teamMapper::toDto);
    }

    @Override
    public Mono<TeamDTO> update(TeamDTO teamDTO) {
        log.debug("Request to update Team : {}", teamDTO);
        return teamRepository.save(teamMapper.toEntity(teamDTO)).flatMap(teamSearchRepository::save).map(teamMapper::toDto);
    }

    @Override
    public Mono<TeamDTO> partialUpdate(TeamDTO teamDTO) {
        log.debug("Request to partially update Team : {}", teamDTO);

        return teamRepository
            .findById(teamDTO.getId())
            .map(existingTeam -> {
                teamMapper.partialUpdate(existingTeam, teamDTO);

                return existingTeam;
            })
            .flatMap(teamRepository::save)
            .flatMap(savedTeam -> {
                teamSearchRepository.save(savedTeam);
                return Mono.just(savedTeam);
            })
            .map(teamMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TeamDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Teams");
        return teamRepository.findAllBy(pageable).map(teamMapper::toDto);
    }

    public Mono<Long> countAll() {
        return teamRepository.count();
    }

    public Mono<Long> searchCount() {
        return teamSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TeamDTO> findOne(Long id) {
        log.debug("Request to get Team : {}", id);
        return teamRepository.findById(id).map(teamMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Team : {}", id);
        return teamRepository.deleteById(id).then(teamSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TeamDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Teams for query {}", query);
        return teamSearchRepository.search(query, pageable).map(teamMapper::toDto);
    }
}
