export interface ITeamSuitNba {
  id: number;
  teamName?: string | null;
}

export type NewTeamSuitNba = Omit<ITeamSuitNba, 'id'> & { id: null };
