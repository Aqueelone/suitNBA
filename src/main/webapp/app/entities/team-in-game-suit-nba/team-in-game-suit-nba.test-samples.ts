import { ITeamInGameSuitNba, NewTeamInGameSuitNba } from './team-in-game-suit-nba.model';

export const sampleWithRequiredData: ITeamInGameSuitNba = {
  id: 3736,
};

export const sampleWithPartialData: ITeamInGameSuitNba = {
  id: 23170,
};

export const sampleWithFullData: ITeamInGameSuitNba = {
  id: 27958,
};

export const sampleWithNewData: NewTeamInGameSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
