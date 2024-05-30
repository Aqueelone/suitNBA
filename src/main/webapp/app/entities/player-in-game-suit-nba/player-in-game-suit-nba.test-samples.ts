import { IPlayerInGameSuitNba, NewPlayerInGameSuitNba } from './player-in-game-suit-nba.model';

export const sampleWithRequiredData: IPlayerInGameSuitNba = {
  id: 2417,
};

export const sampleWithPartialData: IPlayerInGameSuitNba = {
  id: 13921,
  points: 8198,
  rebounds: 23879,
  blocks: 29842,
};

export const sampleWithFullData: IPlayerInGameSuitNba = {
  id: 5421,
  points: 20136,
  rebounds: 12953,
  assists: 29797,
  steals: 7839,
  blocks: 20688,
  fouls: 19726,
  turnovers: 19811,
  played: 28698.88,
};

export const sampleWithNewData: NewPlayerInGameSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
