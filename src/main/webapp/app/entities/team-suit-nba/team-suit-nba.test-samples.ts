import { ITeamSuitNba, NewTeamSuitNba } from './team-suit-nba.model';

export const sampleWithRequiredData: ITeamSuitNba = {
  id: 6044,
};

export const sampleWithPartialData: ITeamSuitNba = {
  id: 2400,
  teamName: 'minus authorized',
};

export const sampleWithFullData: ITeamSuitNba = {
  id: 11899,
  teamName: 'across undress where',
};

export const sampleWithNewData: NewTeamSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
