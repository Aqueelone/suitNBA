import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITeamSuitNba, NewTeamSuitNba } from '../team-suit-nba.model';

export type PartialUpdateTeamSuitNba = Partial<ITeamSuitNba> & Pick<ITeamSuitNba, 'id'>;

export type EntityResponseType = HttpResponse<ITeamSuitNba>;
export type EntityArrayResponseType = HttpResponse<ITeamSuitNba[]>;

@Injectable({ providedIn: 'root' })
export class TeamSuitNbaService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/teams');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/teams/_search');

  create(team: NewTeamSuitNba): Observable<EntityResponseType> {
    return this.http.post<ITeamSuitNba>(this.resourceUrl, team, { observe: 'response' });
  }

  update(team: ITeamSuitNba): Observable<EntityResponseType> {
    return this.http.put<ITeamSuitNba>(`${this.resourceUrl}/${this.getTeamSuitNbaIdentifier(team)}`, team, { observe: 'response' });
  }

  partialUpdate(team: PartialUpdateTeamSuitNba): Observable<EntityResponseType> {
    return this.http.patch<ITeamSuitNba>(`${this.resourceUrl}/${this.getTeamSuitNbaIdentifier(team)}`, team, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeamSuitNba>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeamSuitNba[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITeamSuitNba[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ITeamSuitNba[]>()], asapScheduler)));
  }

  getTeamSuitNbaIdentifier(team: Pick<ITeamSuitNba, 'id'>): number {
    return team.id;
  }

  compareTeamSuitNba(o1: Pick<ITeamSuitNba, 'id'> | null, o2: Pick<ITeamSuitNba, 'id'> | null): boolean {
    return o1 && o2 ? this.getTeamSuitNbaIdentifier(o1) === this.getTeamSuitNbaIdentifier(o2) : o1 === o2;
  }

  addTeamSuitNbaToCollectionIfMissing<Type extends Pick<ITeamSuitNba, 'id'>>(
    teamCollection: Type[],
    ...teamsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const teams: Type[] = teamsToCheck.filter(isPresent);
    if (teams.length > 0) {
      const teamCollectionIdentifiers = teamCollection.map(teamItem => this.getTeamSuitNbaIdentifier(teamItem));
      const teamsToAdd = teams.filter(teamItem => {
        const teamIdentifier = this.getTeamSuitNbaIdentifier(teamItem);
        if (teamCollectionIdentifiers.includes(teamIdentifier)) {
          return false;
        }
        teamCollectionIdentifiers.push(teamIdentifier);
        return true;
      });
      return [...teamsToAdd, ...teamCollection];
    }
    return teamCollection;
  }
}
