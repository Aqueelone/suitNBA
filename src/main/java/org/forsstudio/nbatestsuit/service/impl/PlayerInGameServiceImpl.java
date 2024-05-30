package org.forsstudio.nbatestsuit.service.impl;

import org.forsstudio.nbatestsuit.repository.PlayerInGameRepository;
import org.forsstudio.nbatestsuit.repository.search.PlayerInGameSearchRepository;
import org.forsstudio.nbatestsuit.service.PlayerInGameService;
import org.forsstudio.nbatestsuit.service.dto.PlayerInGameDTO;
import org.forsstudio.nbatestsuit.service.mapper.PlayerInGameMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link org.forsstudio.nbatestsuit.domain.PlayerInGame}.
 */
@Service
@Transactional
public class PlayerInGameServiceImpl implements PlayerInGameService {

    private final Logger log = LoggerFactory.getLogger(PlayerInGameServiceImpl.class);

    private final PlayerInGameRepository playerInGameRepository;

    private final PlayerInGameMapper playerInGameMapper;

    private final PlayerInGameSearchRepository playerInGameSearchRepository;

    public PlayerInGameServiceImpl(
        PlayerInGameRepository playerInGameRepository,
        PlayerInGameMapper playerInGameMapper,
        PlayerInGameSearchRepository playerInGameSearchRepository
    ) {
        this.playerInGameRepository = playerInGameRepository;
        this.playerInGameMapper = playerInGameMapper;
        this.playerInGameSearchRepository = playerInGameSearchRepository;
    }

    @Override
    public Mono<PlayerInGameDTO> save(PlayerInGameDTO playerInGameDTO) {
        log.debug("Request to save PlayerInGame : {}", playerInGameDTO);
        return playerInGameRepository
            .save(playerInGameMapper.toEntity(playerInGameDTO))
            .flatMap(playerInGameSearchRepository::save)
            .map(playerInGameMapper::toDto);
    }

    @Override
    public Mono<PlayerInGameDTO> update(PlayerInGameDTO playerInGameDTO) {
        log.debug("Request to update PlayerInGame : {}", playerInGameDTO);
        return playerInGameRepository
            .save(playerInGameMapper.toEntity(playerInGameDTO))
            .flatMap(playerInGameSearchRepository::save)
            .map(playerInGameMapper::toDto);
    }

    @Override
    public Mono<PlayerInGameDTO> partialUpdate(PlayerInGameDTO playerInGameDTO) {
        log.debug("Request to partially update PlayerInGame : {}", playerInGameDTO);

        return playerInGameRepository
            .findById(playerInGameDTO.getId())
            .map(existingPlayerInGame -> {
                playerInGameMapper.partialUpdate(existingPlayerInGame, playerInGameDTO);

                return existingPlayerInGame;
            })
            .flatMap(playerInGameRepository::save)
            .flatMap(savedPlayerInGame -> {
                playerInGameSearchRepository.save(savedPlayerInGame);
                return Mono.just(savedPlayerInGame);
            })
            .map(playerInGameMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlayerInGameDTO> findAll(Pageable pageable) {
        log.debug("Request to get all PlayerInGames");
        return playerInGameRepository.findAllBy(pageable).map(playerInGameMapper::toDto);
    }

    public Mono<Long> countAll() {
        return playerInGameRepository.count();
    }

    public Mono<Long> searchCount() {
        return playerInGameSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PlayerInGameDTO> findOne(Long id) {
        log.debug("Request to get PlayerInGame : {}", id);
        return playerInGameRepository.findById(id).map(playerInGameMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete PlayerInGame : {}", id);
        return playerInGameRepository.deleteById(id).then(playerInGameSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlayerInGameDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of PlayerInGames for query {}", query);
        return playerInGameSearchRepository.search(query, pageable).map(playerInGameMapper::toDto);
    }
}
