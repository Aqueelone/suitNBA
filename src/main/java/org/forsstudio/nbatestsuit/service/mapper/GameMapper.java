package org.forsstudio.nbatestsuit.service.mapper;

import org.forsstudio.nbatestsuit.domain.Game;
import org.forsstudio.nbatestsuit.domain.Season;
import org.forsstudio.nbatestsuit.service.dto.GameDTO;
import org.forsstudio.nbatestsuit.service.dto.SeasonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Game} and its DTO {@link GameDTO}.
 */
@Mapper(componentModel = "spring")
public interface GameMapper extends EntityMapper<GameDTO, Game> {
    @Mapping(target = "season", source = "season", qualifiedByName = "seasonId")
    GameDTO toDto(Game s);

    @Named("seasonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SeasonDTO toDtoSeasonId(Season season);
}
