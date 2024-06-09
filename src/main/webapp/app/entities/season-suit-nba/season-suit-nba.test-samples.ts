import { ISeasonSuitNba, NewSeasonSuitNba } from './season-suit-nba.model';

export const sampleWithRequiredData: ISeasonSuitNba = {
  id: 20701,
};

export const sampleWithPartialData: ISeasonSuitNba = {
  id: 26002,
  seasonName: 'clothes hm before',
};

export const sampleWithFullData: ISeasonSuitNba = {
  id: 6489,
  seasonName: 'gosh badly rue',
};

export const sampleWithNewData: NewSeasonSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
