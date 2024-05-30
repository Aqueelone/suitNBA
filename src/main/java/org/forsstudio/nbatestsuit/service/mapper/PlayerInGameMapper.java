package org.forsstudio.nbatestsuit.service.mapper;

import org.forsstudio.nbatestsuit.domain.Game;
import org.forsstudio.nbatestsuit.domain.Player;
import org.forsstudio.nbatestsuit.domain.PlayerInGame;
import org.forsstudio.nbatestsuit.domain.TeamInGame;
import org.forsstudio.nbatestsuit.service.dto.GameDTO;
import org.forsstudio.nbatestsuit.service.dto.PlayerDTO;
import org.forsstudio.nbatestsuit.service.dto.PlayerInGameDTO;
import org.forsstudio.nbatestsuit.service.dto.TeamInGameDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PlayerInGame} and its DTO {@link PlayerInGameDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlayerInGameMapper extends EntityMapper<PlayerInGameDTO, PlayerInGame> {
    @Mapping(target = "team", source = "team", qualifiedByName = "teamInGameId")
    @Mapping(target = "player", source = "player", qualifiedByName = "playerId")
    @Mapping(target = "game", source = "game", qualifiedByName = "gameId")
    PlayerInGameDTO toDto(PlayerInGame s);

    @Named("teamInGameId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeamInGameDTO toDtoTeamInGameId(TeamInGame teamInGame);

    @Named("playerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PlayerDTO toDtoPlayerId(Player player);

    @Named("gameId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    GameDTO toDtoGameId(Game game);
}
