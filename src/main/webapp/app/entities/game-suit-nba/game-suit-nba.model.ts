import { ISeasonSuitNba } from 'app/entities/season-suit-nba/season-suit-nba.model';

export interface IGameSuitNba {
  id: number;
  season?: Pick<ISeasonSuitNba, 'id'> | null;
}

export type NewGameSuitNba = Omit<IGameSuitNba, 'id'> & { id: null };
