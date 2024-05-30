import { ITeamInGameSuitNba } from 'app/entities/team-in-game-suit-nba/team-in-game-suit-nba.model';
import { IPlayerSuitNba } from 'app/entities/player-suit-nba/player-suit-nba.model';
import { IGameSuitNba } from 'app/entities/game-suit-nba/game-suit-nba.model';

export interface IPlayerInGameSuitNba {
  id: number;
  points?: number | null;
  rebounds?: number | null;
  assists?: number | null;
  steals?: number | null;
  blocks?: number | null;
  fouls?: number | null;
  turnovers?: number | null;
  played?: number | null;
  team?: Pick<ITeamInGameSuitNba, 'id'> | null;
  player?: Pick<IPlayerSuitNba, 'id'> | null;
  game?: Pick<IGameSuitNba, 'id'> | null;
}

export type NewPlayerInGameSuitNba = Omit<IPlayerInGameSuitNba, 'id'> & { id: null };
