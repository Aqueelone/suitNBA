package org.forsstudio.nbatestsuit.service.mapper;

import org.forsstudio.nbatestsuit.domain.Player;
import org.forsstudio.nbatestsuit.domain.PlayerInTeam;
import org.forsstudio.nbatestsuit.domain.Season;
import org.forsstudio.nbatestsuit.domain.Team;
import org.forsstudio.nbatestsuit.domain.TeamInSeason;
import org.forsstudio.nbatestsuit.service.dto.PlayerDTO;
import org.forsstudio.nbatestsuit.service.dto.PlayerInTeamDTO;
import org.forsstudio.nbatestsuit.service.dto.SeasonDTO;
import org.forsstudio.nbatestsuit.service.dto.TeamDTO;
import org.forsstudio.nbatestsuit.service.dto.TeamInSeasonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PlayerInTeam} and its DTO {@link PlayerInTeamDTO}.
 */
@Mapper(componentModel = "spring")
public interface PlayerInTeamMapper extends EntityMapper<PlayerInTeamDTO, PlayerInTeam> {
    @Mapping(target = "player", source = "player", qualifiedByName = "playerId")
    @Mapping(target = "teamInSeason", source = "teamInSeason", qualifiedByName = "teamInSeasonId")
    PlayerInTeamDTO toDto(PlayerInTeam s);

    @Named("playerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    PlayerDTO toDtoPlayerId(Player player);

    @Named("teamInSeasonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "team", source = "team")
    @Mapping(target = "team.id", source = "team.id")
    @Mapping(target = "team.teamName", source = "team.teamName")
    @Mapping(target = "season", source = "season")
    @Mapping(target = "season.id", source = "season.id")
    @Mapping(target = "season.seasonName", source = "season.seasonName")
    TeamInSeasonDTO toDtoTeamInSeasonId(TeamInSeason teamInSeason);
}
