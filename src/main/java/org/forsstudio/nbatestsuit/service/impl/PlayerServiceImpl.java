package org.forsstudio.nbatestsuit.service.impl;

import org.forsstudio.nbatestsuit.repository.PlayerRepository;
import org.forsstudio.nbatestsuit.repository.search.PlayerSearchRepository;
import org.forsstudio.nbatestsuit.service.PlayerService;
import org.forsstudio.nbatestsuit.service.dto.PlayerDTO;
import org.forsstudio.nbatestsuit.service.mapper.PlayerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link org.forsstudio.nbatestsuit.domain.Player}.
 */
@Service
@Transactional
public class PlayerServiceImpl implements PlayerService {

    private final Logger log = LoggerFactory.getLogger(PlayerServiceImpl.class);

    private final PlayerRepository playerRepository;

    private final PlayerMapper playerMapper;

    private final PlayerSearchRepository playerSearchRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository, PlayerMapper playerMapper, PlayerSearchRepository playerSearchRepository) {
        this.playerRepository = playerRepository;
        this.playerMapper = playerMapper;
        this.playerSearchRepository = playerSearchRepository;
    }

    @Override
    public Mono<PlayerDTO> save(PlayerDTO playerDTO) {
        log.debug("Request to save Player : {}", playerDTO);
        return playerRepository.save(playerMapper.toEntity(playerDTO)).flatMap(playerSearchRepository::save).map(playerMapper::toDto);
    }

    @Override
    public Mono<PlayerDTO> update(PlayerDTO playerDTO) {
        log.debug("Request to update Player : {}", playerDTO);
        return playerRepository.save(playerMapper.toEntity(playerDTO)).flatMap(playerSearchRepository::save).map(playerMapper::toDto);
    }

    @Override
    public Mono<PlayerDTO> partialUpdate(PlayerDTO playerDTO) {
        log.debug("Request to partially update Player : {}", playerDTO);

        return playerRepository
            .findById(playerDTO.getId())
            .map(existingPlayer -> {
                playerMapper.partialUpdate(existingPlayer, playerDTO);

                return existingPlayer;
            })
            .flatMap(playerRepository::save)
            .flatMap(savedPlayer -> {
                playerSearchRepository.save(savedPlayer);
                return Mono.just(savedPlayer);
            })
            .map(playerMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlayerDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Players");
        return playerRepository.findAllBy(pageable).map(playerMapper::toDto);
    }

    public Mono<Long> countAll() {
        return playerRepository.count();
    }

    public Mono<Long> searchCount() {
        return playerSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PlayerDTO> findOne(Long id) {
        log.debug("Request to get Player : {}", id);
        return playerRepository.findById(id).map(playerMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Player : {}", id);
        return playerRepository.deleteById(id).then(playerSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<PlayerDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Players for query {}", query);
        return playerSearchRepository.search(query, pageable).map(playerMapper::toDto);
    }
}
