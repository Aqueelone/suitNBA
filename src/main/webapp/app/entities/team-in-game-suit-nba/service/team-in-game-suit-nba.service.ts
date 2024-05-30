import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ITeamInGameSuitNba, NewTeamInGameSuitNba } from '../team-in-game-suit-nba.model';

export type PartialUpdateTeamInGameSuitNba = Partial<ITeamInGameSuitNba> & Pick<ITeamInGameSuitNba, 'id'>;

export type EntityResponseType = HttpResponse<ITeamInGameSuitNba>;
export type EntityArrayResponseType = HttpResponse<ITeamInGameSuitNba[]>;

@Injectable({ providedIn: 'root' })
export class TeamInGameSuitNbaService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/team-in-games');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/team-in-games/_search');

  create(teamInGame: NewTeamInGameSuitNba): Observable<EntityResponseType> {
    return this.http.post<ITeamInGameSuitNba>(this.resourceUrl, teamInGame, { observe: 'response' });
  }

  update(teamInGame: ITeamInGameSuitNba): Observable<EntityResponseType> {
    return this.http.put<ITeamInGameSuitNba>(`${this.resourceUrl}/${this.getTeamInGameSuitNbaIdentifier(teamInGame)}`, teamInGame, {
      observe: 'response',
    });
  }

  partialUpdate(teamInGame: PartialUpdateTeamInGameSuitNba): Observable<EntityResponseType> {
    return this.http.patch<ITeamInGameSuitNba>(`${this.resourceUrl}/${this.getTeamInGameSuitNbaIdentifier(teamInGame)}`, teamInGame, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeamInGameSuitNba>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeamInGameSuitNba[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITeamInGameSuitNba[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ITeamInGameSuitNba[]>()], asapScheduler)));
  }

  getTeamInGameSuitNbaIdentifier(teamInGame: Pick<ITeamInGameSuitNba, 'id'>): number {
    return teamInGame.id;
  }

  compareTeamInGameSuitNba(o1: Pick<ITeamInGameSuitNba, 'id'> | null, o2: Pick<ITeamInGameSuitNba, 'id'> | null): boolean {
    return o1 && o2 ? this.getTeamInGameSuitNbaIdentifier(o1) === this.getTeamInGameSuitNbaIdentifier(o2) : o1 === o2;
  }

  addTeamInGameSuitNbaToCollectionIfMissing<Type extends Pick<ITeamInGameSuitNba, 'id'>>(
    teamInGameCollection: Type[],
    ...teamInGamesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const teamInGames: Type[] = teamInGamesToCheck.filter(isPresent);
    if (teamInGames.length > 0) {
      const teamInGameCollectionIdentifiers = teamInGameCollection.map(teamInGameItem =>
        this.getTeamInGameSuitNbaIdentifier(teamInGameItem),
      );
      const teamInGamesToAdd = teamInGames.filter(teamInGameItem => {
        const teamInGameIdentifier = this.getTeamInGameSuitNbaIdentifier(teamInGameItem);
        if (teamInGameCollectionIdentifiers.includes(teamInGameIdentifier)) {
          return false;
        }
        teamInGameCollectionIdentifiers.push(teamInGameIdentifier);
        return true;
      });
      return [...teamInGamesToAdd, ...teamInGameCollection];
    }
    return teamInGameCollection;
  }
}
