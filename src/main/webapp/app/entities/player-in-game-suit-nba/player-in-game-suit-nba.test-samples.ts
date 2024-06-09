import { IPlayerInGameSuitNba, NewPlayerInGameSuitNba } from './player-in-game-suit-nba.model';

export const sampleWithRequiredData: IPlayerInGameSuitNba = {
  id: 13480,
};

export const sampleWithPartialData: IPlayerInGameSuitNba = {
  id: 19276,
  points: 4183,
  rebounds: 8397,
  assists: 24021,
  steals: 146,
  blocks: 16591,
  fouls: 4937,
  turnovers: 10726,
};

export const sampleWithFullData: IPlayerInGameSuitNba = {
  id: 17610,
  points: 30052,
  rebounds: 12229,
  assists: 11824,
  steals: 7671,
  blocks: 23579,
  fouls: 24694,
  turnovers: 10872,
  played: 8015.08,
};

export const sampleWithNewData: NewPlayerInGameSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
