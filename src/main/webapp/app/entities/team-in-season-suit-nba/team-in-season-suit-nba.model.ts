import { ITeamSuitNba } from 'app/entities/team-suit-nba/team-suit-nba.model';
import { ISeasonSuitNba } from 'app/entities/season-suit-nba/season-suit-nba.model';

export interface ITeamInSeasonSuitNba {
  id: number;
  team?: Pick<ITeamSuitNba, 'id'> | null;
  season?: Pick<ISeasonSuitNba, 'id'> | null;
}

export type NewTeamInSeasonSuitNba = Omit<ITeamInSeasonSuitNba, 'id'> & { id: null };