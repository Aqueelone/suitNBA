import { ITeamInSeasonSuitNba, NewTeamInSeasonSuitNba } from './team-in-season-suit-nba.model';

export const sampleWithRequiredData: ITeamInSeasonSuitNba = {
  id: 11006,
};

export const sampleWithPartialData: ITeamInSeasonSuitNba = {
  id: 4917,
};

export const sampleWithFullData: ITeamInSeasonSuitNba = {
  id: 16070,
};

export const sampleWithNewData: NewTeamInSeasonSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
