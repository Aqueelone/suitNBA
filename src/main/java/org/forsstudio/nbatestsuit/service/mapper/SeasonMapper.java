package org.forsstudio.nbatestsuit.service.mapper;

import org.forsstudio.nbatestsuit.domain.Season;
import org.forsstudio.nbatestsuit.service.dto.SeasonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Season} and its DTO {@link SeasonDTO}.
 */
@Mapper(componentModel = "spring")
public interface SeasonMapper extends EntityMapper<SeasonDTO, Season> {}
