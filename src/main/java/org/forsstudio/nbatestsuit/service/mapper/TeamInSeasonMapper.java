package org.forsstudio.nbatestsuit.service.mapper;

import org.forsstudio.nbatestsuit.domain.Season;
import org.forsstudio.nbatestsuit.domain.Team;
import org.forsstudio.nbatestsuit.domain.TeamInSeason;
import org.forsstudio.nbatestsuit.service.dto.SeasonDTO;
import org.forsstudio.nbatestsuit.service.dto.TeamDTO;
import org.forsstudio.nbatestsuit.service.dto.TeamInSeasonDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TeamInSeason} and its DTO {@link TeamInSeasonDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamInSeasonMapper extends EntityMapper<TeamInSeasonDTO, TeamInSeason> {
    @Mapping(target = "team", source = "team", qualifiedByName = "teamId")
    @Mapping(target = "season", source = "season", qualifiedByName = "seasonId")
    TeamInSeasonDTO toDto(TeamInSeason s);

    @Named("teamId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeamDTO toDtoTeamId(Team team);

    @Named("seasonId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    SeasonDTO toDtoSeasonId(Season season);
}
