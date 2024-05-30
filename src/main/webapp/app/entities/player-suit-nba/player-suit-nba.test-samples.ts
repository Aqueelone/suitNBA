import { IPlayerSuitNba, NewPlayerSuitNba } from './player-suit-nba.model';

export const sampleWithRequiredData: IPlayerSuitNba = {
  id: 4867,
};

export const sampleWithPartialData: IPlayerSuitNba = {
  id: 3370,
  name: 'an',
};

export const sampleWithFullData: IPlayerSuitNba = {
  id: 10703,
  name: 'hmph meanwhile wherever',
};

export const sampleWithNewData: NewPlayerSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
