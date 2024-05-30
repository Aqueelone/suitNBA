package org.forsstudio.nbatestsuit.service.impl;

import org.forsstudio.nbatestsuit.repository.GameRepository;
import org.forsstudio.nbatestsuit.repository.search.GameSearchRepository;
import org.forsstudio.nbatestsuit.service.GameService;
import org.forsstudio.nbatestsuit.service.dto.GameDTO;
import org.forsstudio.nbatestsuit.service.mapper.GameMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link org.forsstudio.nbatestsuit.domain.Game}.
 */
@Service
@Transactional
public class GameServiceImpl implements GameService {

    private final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);

    private final GameRepository gameRepository;

    private final GameMapper gameMapper;

    private final GameSearchRepository gameSearchRepository;

    public GameServiceImpl(GameRepository gameRepository, GameMapper gameMapper, GameSearchRepository gameSearchRepository) {
        this.gameRepository = gameRepository;
        this.gameMapper = gameMapper;
        this.gameSearchRepository = gameSearchRepository;
    }

    @Override
    public Mono<GameDTO> save(GameDTO gameDTO) {
        log.debug("Request to save Game : {}", gameDTO);
        return gameRepository.save(gameMapper.toEntity(gameDTO)).flatMap(gameSearchRepository::save).map(gameMapper::toDto);
    }

    @Override
    public Mono<GameDTO> update(GameDTO gameDTO) {
        log.debug("Request to update Game : {}", gameDTO);
        return gameRepository.save(gameMapper.toEntity(gameDTO)).flatMap(gameSearchRepository::save).map(gameMapper::toDto);
    }

    @Override
    public Mono<GameDTO> partialUpdate(GameDTO gameDTO) {
        log.debug("Request to partially update Game : {}", gameDTO);

        return gameRepository
            .findById(gameDTO.getId())
            .map(existingGame -> {
                gameMapper.partialUpdate(existingGame, gameDTO);

                return existingGame;
            })
            .flatMap(gameRepository::save)
            .flatMap(savedGame -> {
                gameSearchRepository.save(savedGame);
                return Mono.just(savedGame);
            })
            .map(gameMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<GameDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Games");
        return gameRepository.findAllBy(pageable).map(gameMapper::toDto);
    }

    public Mono<Long> countAll() {
        return gameRepository.count();
    }

    public Mono<Long> searchCount() {
        return gameSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<GameDTO> findOne(Long id) {
        log.debug("Request to get Game : {}", id);
        return gameRepository.findById(id).map(gameMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Game : {}", id);
        return gameRepository.deleteById(id).then(gameSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<GameDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Games for query {}", query);
        return gameSearchRepository.search(query, pageable).map(gameMapper::toDto);
    }
}
