import { IGameSuitNba, NewGameSuitNba } from './game-suit-nba.model';

export const sampleWithRequiredData: IGameSuitNba = {
  id: 13194,
};

export const sampleWithPartialData: IGameSuitNba = {
  id: 13692,
};

export const sampleWithFullData: IGameSuitNba = {
  id: 6645,
};

export const sampleWithNewData: NewGameSuitNba = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
