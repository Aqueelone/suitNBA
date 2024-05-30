import { ITeamInSeasonSuitNba } from 'app/entities/team-in-season-suit-nba/team-in-season-suit-nba.model';
import { IGameSuitNba } from 'app/entities/game-suit-nba/game-suit-nba.model';

export interface ITeamInGameSuitNba {
  id: number;
  team?: Pick<ITeamInSeasonSuitNba, 'id'> | null;
  game?: Pick<IGameSuitNba, 'id'> | null;
}

export type NewTeamInGameSuitNba = Omit<ITeamInGameSuitNba, 'id'> & { id: null };
