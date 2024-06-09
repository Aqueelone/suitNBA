import { IGameSuitNba, NewGameSuitNba } from './game-suit-nba.model';

export const sampleWithRequiredData: IGameSuitNba = {
  id: 18975,
};

export const sampleWithPartialData: IGameSuitNba = {
  id: 51,
};

export const sampleWithFullData: IGameSuitNba = {
  id: 24635,
};

export const sampleWithNewData: NewGameSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
