import { IPlayerSuitNba } from 'app/entities/player-suit-nba/player-suit-nba.model';
import { ITeamInSeasonSuitNba } from 'app/entities/team-in-season-suit-nba/team-in-season-suit-nba.model';
import { ITeamSuitNba } from '../team-suit-nba/team-suit-nba.model';
import { ISeasonSuitNba } from '../season-suit-nba/season-suit-nba.model';

export interface IPlayerInTeam {
  id: number;
  player?: IPlayerSuitNba | null;
  teamInSeason?: ITeamInSeasonSuitNba | null;
  team?: ITeamSuitNba | null;
  season?: ISeasonSuitNba | null;
}

export type NewPlayerInTeam = Omit<IPlayerInTeam, 'id'> & { id: null };
