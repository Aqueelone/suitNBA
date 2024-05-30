package org.forsstudio.nbatestsuit.service.mapper;

import org.forsstudio.nbatestsuit.domain.Game;
import org.forsstudio.nbatestsuit.domain.TeamInGame;
import org.forsstudio.nbatestsuit.domain.TeamInSeason;
import org.forsstudio.nbatestsuit.service.dto.GameDTO;
import org.forsstudio.nbatestsuit.service.dto.TeamInGameDTO;
import org.forsstudio.nbatestsuit.service.dto.TeamInSeasonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TeamInGame} and its DTO {@link TeamInGameDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamInGameMapper extends EntityMapper<TeamInGameDTO, TeamInGame> {
    @Mapping(target = "team", source = "team", qualifiedByName = "teamInSeasonId")
    @Mapping(target = "game", source = "game", qualifiedByName = "gameId")
    TeamInGameDTO toDto(TeamInGame s);

    @Named("teamInSeasonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeamInSeasonDTO toDtoTeamInSeasonId(TeamInSeason teamInSeason);

    @Named("gameId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GameDTO toDtoGameId(Game game);
}
