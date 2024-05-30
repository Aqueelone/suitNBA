import { ISeasonSuitNba, NewSeasonSuitNba } from './season-suit-nba.model';

export const sampleWithRequiredData: ISeasonSuitNba = {
  id: 12644,
};

export const sampleWithPartialData: ISeasonSuitNba = {
  id: 8118,
};

export const sampleWithFullData: ISeasonSuitNba = {
  id: 31334,
  seasonName: 'broadly boohoo while',
};

export const sampleWithNewData: NewSeasonSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
