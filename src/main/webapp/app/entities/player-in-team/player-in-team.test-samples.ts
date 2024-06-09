import { IPlayerInTeam, NewPlayerInTeam } from './player-in-team.model';

export const sampleWithRequiredData: IPlayerInTeam = {
  id: 7322,
};

export const sampleWithPartialData: IPlayerInTeam = {
  id: 9554,
};

export const sampleWithFullData: IPlayerInTeam = {
  id: 21800,
};

export const sampleWithNewData: NewPlayerInTeam = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
