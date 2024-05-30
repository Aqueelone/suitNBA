export interface ISeasonSuitNba {
  id: number;
  seasonName?: string | null;
}

export type NewSeasonSuitNba = Omit<ISeasonSuitNba, 'id'> & { id: null };
