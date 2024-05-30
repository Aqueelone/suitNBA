import { ITeamInSeasonSuitNba, NewTeamInSeasonSuitNba } from './team-in-season-suit-nba.model';

export const sampleWithRequiredData: ITeamInSeasonSuitNba = {
  id: 3744,
};

export const sampleWithPartialData: ITeamInSeasonSuitNba = {
  id: 29900,
};

export const sampleWithFullData: ITeamInSeasonSuitNba = {
  id: 26840,
};

export const sampleWithNewData: NewTeamInSeasonSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
