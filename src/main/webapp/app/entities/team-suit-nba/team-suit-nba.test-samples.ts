import { ITeamSuitNba, NewTeamSuitNba } from './team-suit-nba.model';

export const sampleWithRequiredData: ITeamSuitNba = {
  id: 26451,
};

export const sampleWithPartialData: ITeamSuitNba = {
  id: 1381,
};

export const sampleWithFullData: ITeamSuitNba = {
  id: 18277,
  teamName: 'procure concede',
};

export const sampleWithNewData: NewTeamSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
