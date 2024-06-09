package org.forsstudio.nbatestsuit.service.impl;

import org.forsstudio.nbatestsuit.repository.PlayerInTeamRepository;
import org.forsstudio.nbatestsuit.repository.search.PlayerInTeamSearchRepository;
import org.forsstudio.nbatestsuit.service.PlayerInTeamService;
import org.forsstudio.nbatestsuit.service.dto.PlayerInTeamDTO;
import org.forsstudio.nbatestsuit.service.mapper.PlayerInTeamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link org.forsstudio.nbatestsuit.domain.PlayerInTeam}.
 */
@Service
@Transactional
public class PlayerInTeamServiceImpl implements PlayerInTeamService {

    private final Logger log = LoggerFactory.getLogger(PlayerInTeamServiceImpl.class);

    private final PlayerInTeamRepository playerInTeamRepository;

    private final PlayerInTeamMapper playerInTeamMapper;

    private final PlayerInTeamSearchRepository playerInTeamSearchRepository;

    public PlayerInTeamServiceImpl(
        PlayerInTeamRepository playerInTeamRepository,
        PlayerInTeamMapper playerInTeamMapper,
        PlayerInTeamSearchRepository playerInTeamSearchRepository
    ) {
        this.playerInTeamRepository = playerInTeamRepository;
        this.playerInTeamMapper = playerInTeamMapper;
        this.playerInTeamSearchRepository = playerInTeamSearchRepository;
    }

    @Override
    public Mono<PlayerInTeamDTO> save(PlayerInTeamDTO playerInTeamDTO) {
        log.debug("Request to save PlayerInTeam : {}", playerInTeamDTO);
        return playerInTeamRepository
            .save(playerInTeamMapper.toEntity(playerInTeamDTO))
            .flatMap(playerInTeamSearchRepository::save)
            .map(playerInTeamMapper::toDto);
    }

    @Override
    public Mono<PlayerInTeamDTO> update(PlayerInTeamDTO playerInTeamDTO) {
        log.debug("Request to update PlayerInTeam : {}", playerInTeamDTO);
        return playerInTeamRepository
            .save(playerInTeamMapper.toEntity(playerInTeamDTO))
            .flatMap(playerInTeamSearchRepository::save)
            .map(playerInTeamMapper::toDto);
    }

    @Override
    public Mono<PlayerInTeamDTO> partialUpdate(PlayerInTeamDTO playerInTeamDTO) {
        log.debug("Request to partially update PlayerInTeam : {}", playerInTeamDTO);

        return playerInTeamRepository
            .findById(playerInTeamDTO.getId())
            .map(existingPlayerInTeam -> {
                playerInTeamMapper.partialUpdate(existingPlayerInTeam, playerInTeamDTO);

                return existingPlayerInTeam;
            })
            .flatMap(playerInTeamRepository::save)
            .flatMap(savedPlayerInTeam -> {
                playerInTeamSearchRepository.save(savedPlayerInTeam);
                return Mono.just(savedPlayerInTeam);
            })
            .map(playerInTeamMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlayerInTeamDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PlayerInTeams");
        return playerInTeamRepository.findAllBy(pageable).map(playerInTeamMapper::toDto);
    }

    public Mono<Long> countAll() {
        return playerInTeamRepository.count();
    }

    public Mono<Long> searchCount() {
        return playerInTeamSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PlayerInTeamDTO> findOne(Long id) {
        log.debug("Request to get PlayerInTeam : {}", id);
        return playerInTeamRepository.findById(id).map(playerInTeamMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete PlayerInTeam : {}", id);
        return playerInTeamRepository.deleteById(id).then(playerInTeamSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlayerInTeamDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PlayerInTeams for query {}", query);
        return playerInTeamSearchRepository.search(query, pageable).map(playerInTeamMapper::toDto);
    }
}
