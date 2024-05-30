import { ITeamInGameSuitNba, NewTeamInGameSuitNba } from './team-in-game-suit-nba.model';

export const sampleWithRequiredData: ITeamInGameSuitNba = {
  id: 22790,
};

export const sampleWithPartialData: ITeamInGameSuitNba = {
  id: 29077,
};

export const sampleWithFullData: ITeamInGameSuitNba = {
  id: 19943,
};

export const sampleWithNewData: NewTeamInGameSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
