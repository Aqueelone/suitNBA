export interface IPlayerSuitNba {
  id: number;
  name?: string | null;
}

export type NewPlayerSuitNba = Omit<IPlayerSuitNba, 'id'> & { id: null };
