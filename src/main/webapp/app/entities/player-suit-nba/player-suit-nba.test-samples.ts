import { IPlayerSuitNba, NewPlayerSuitNba } from './player-suit-nba.model';

export const sampleWithRequiredData: IPlayerSuitNba = {
  id: 15438,
};

export const sampleWithPartialData: IPlayerSuitNba = {
  id: 17146,
};

export const sampleWithFullData: IPlayerSuitNba = {
  id: 11999,
  name: 'whereas faithfully sometimes',
};

export const sampleWithNewData: NewPlayerSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
