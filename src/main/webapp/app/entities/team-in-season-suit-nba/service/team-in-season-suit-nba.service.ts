import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITeamInSeasonSuitNba, NewTeamInSeasonSuitNba } from '../team-in-season-suit-nba.model';

export type PartialUpdateTeamInSeasonSuitNba = Partial<ITeamInSeasonSuitNba> & Pick<ITeamInSeasonSuitNba, 'id'>;

export type EntityResponseType = HttpResponse<ITeamInSeasonSuitNba>;
export type EntityArrayResponseType = HttpResponse<ITeamInSeasonSuitNba[]>;

@Injectable({ providedIn: 'root' })
export class TeamInSeasonSuitNbaService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/team-in-seasons');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/team-in-seasons/_search');

  create(teamInSeason: NewTeamInSeasonSuitNba): Observable<EntityResponseType> {
    return this.http.post<ITeamInSeasonSuitNba>(this.resourceUrl, teamInSeason, { observe: 'response' });
  }

  update(teamInSeason: ITeamInSeasonSuitNba): Observable<EntityResponseType> {
    return this.http.put<ITeamInSeasonSuitNba>(`${this.resourceUrl}/${this.getTeamInSeasonSuitNbaIdentifier(teamInSeason)}`, teamInSeason, {
      observe: 'response',
    });
  }

  partialUpdate(teamInSeason: PartialUpdateTeamInSeasonSuitNba): Observable<EntityResponseType> {
    return this.http.patch<ITeamInSeasonSuitNba>(
      `${this.resourceUrl}/${this.getTeamInSeasonSuitNbaIdentifier(teamInSeason)}`,
      teamInSeason,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeamInSeasonSuitNba>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeamInSeasonSuitNba[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITeamInSeasonSuitNba[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ITeamInSeasonSuitNba[]>()], asapScheduler)));
  }

  getTeamInSeasonSuitNbaIdentifier(teamInSeason: Pick<ITeamInSeasonSuitNba, 'id'>): number {
    return teamInSeason.id;
  }

  compareTeamInSeasonSuitNba(o1: Pick<ITeamInSeasonSuitNba, 'id'> | null, o2: Pick<ITeamInSeasonSuitNba, 'id'> | null): boolean {
    return o1 && o2 ? this.getTeamInSeasonSuitNbaIdentifier(o1) === this.getTeamInSeasonSuitNbaIdentifier(o2) : o1 === o2;
  }

  addTeamInSeasonSuitNbaToCollectionIfMissing<Type extends Pick<ITeamInSeasonSuitNba, 'id'>>(
    teamInSeasonCollection: Type[],
    ...teamInSeasonsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const teamInSeasons: Type[] = teamInSeasonsToCheck.filter(isPresent);
    if (teamInSeasons.length > 0) {
      const teamInSeasonCollectionIdentifiers = teamInSeasonCollection.map(teamInSeasonItem =>
        this.getTeamInSeasonSuitNbaIdentifier(teamInSeasonItem),
      );
      const teamInSeasonsToAdd = teamInSeasons.filter(teamInSeasonItem => {
        const teamInSeasonIdentifier = this.getTeamInSeasonSuitNbaIdentifier(teamInSeasonItem);
        if (teamInSeasonCollectionIdentifiers.includes(teamInSeasonIdentifier)) {
          return false;
        }
        teamInSeasonCollectionIdentifiers.push(teamInSeasonIdentifier);
        return true;
      });
      return [...teamInSeasonsToAdd, ...teamInSeasonCollection];
    }
    return teamInSeasonCollection;
  }
}
