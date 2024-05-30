package org.forsstudio.nbatestsuit.service.impl;

import org.forsstudio.nbatestsuit.repository.SeasonRepository;
import org.forsstudio.nbatestsuit.repository.search.SeasonSearchRepository;
import org.forsstudio.nbatestsuit.service.SeasonService;
import org.forsstudio.nbatestsuit.service.dto.SeasonDTO;
import org.forsstudio.nbatestsuit.service.mapper.SeasonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link org.forsstudio.nbatestsuit.domain.Season}.
 */
@Service
@Transactional
public class SeasonServiceImpl implements SeasonService {

    private final Logger log = LoggerFactory.getLogger(SeasonServiceImpl.class);

    private final SeasonRepository seasonRepository;

    private final SeasonMapper seasonMapper;

    private final SeasonSearchRepository seasonSearchRepository;

    public SeasonServiceImpl(SeasonRepository seasonRepository, SeasonMapper seasonMapper, SeasonSearchRepository seasonSearchRepository) {
        this.seasonRepository = seasonRepository;
        this.seasonMapper = seasonMapper;
        this.seasonSearchRepository = seasonSearchRepository;
    }

    @Override
    public Mono<SeasonDTO> save(SeasonDTO seasonDTO) {
        log.debug("Request to save Season : {}", seasonDTO);
        return seasonRepository.save(seasonMapper.toEntity(seasonDTO)).flatMap(seasonSearchRepository::save).map(seasonMapper::toDto);
    }

    @Override
    public Mono<SeasonDTO> update(SeasonDTO seasonDTO) {
        log.debug("Request to update Season : {}", seasonDTO);
        return seasonRepository.save(seasonMapper.toEntity(seasonDTO)).flatMap(seasonSearchRepository::save).map(seasonMapper::toDto);
    }

    @Override
    public Mono<SeasonDTO> partialUpdate(SeasonDTO seasonDTO) {
        log.debug("Request to partially update Season : {}", seasonDTO);

        return seasonRepository
            .findById(seasonDTO.getId())
            .map(existingSeason -> {
                seasonMapper.partialUpdate(existingSeason, seasonDTO);

                return existingSeason;
            })
            .flatMap(seasonRepository::save)
            .flatMap(savedSeason -> {
                seasonSearchRepository.save(savedSeason);
                return Mono.just(savedSeason);
            })
            .map(seasonMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SeasonDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Seasons");
        return seasonRepository.findAllBy(pageable).map(seasonMapper::toDto);
    }

    public Mono<Long> countAll() {
        return seasonRepository.count();
    }

    public Mono<Long> searchCount() {
        return seasonSearchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<SeasonDTO> findOne(Long id) {
        log.debug("Request to get Season : {}", id);
        return seasonRepository.findById(id).map(seasonMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Season : {}", id);
        return seasonRepository.deleteById(id).then(seasonSearchRepository.deleteById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<SeasonDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Seasons for query {}", query);
        return seasonSearchRepository.search(query, pageable).map(seasonMapper::toDto);
    }
}
