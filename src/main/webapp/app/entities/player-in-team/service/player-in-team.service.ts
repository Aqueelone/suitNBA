import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPlayerInTeam, NewPlayerInTeam } from '../player-in-team.model';

export type PartialUpdatePlayerInTeam = Partial<IPlayerInTeam> & Pick<IPlayerInTeam, 'id'>;

export type EntityResponseType = HttpResponse<IPlayerInTeam>;
export type EntityArrayResponseType = HttpResponse<IPlayerInTeam[]>;

@Injectable({ providedIn: 'root' })
export class PlayerInTeamService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/player-in-teams');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/player-in-teams/_search');

  create(playerInTeam: NewPlayerInTeam): Observable<EntityResponseType> {
    return this.http.post<IPlayerInTeam>(this.resourceUrl, playerInTeam, { observe: 'response' });
  }

  update(playerInTeam: IPlayerInTeam): Observable<EntityResponseType> {
    return this.http.put<IPlayerInTeam>(`${this.resourceUrl}/${this.getPlayerInTeamIdentifier(playerInTeam)}`, playerInTeam, {
      observe: 'response',
    });
  }

  partialUpdate(playerInTeam: PartialUpdatePlayerInTeam): Observable<EntityResponseType> {
    return this.http.patch<IPlayerInTeam>(`${this.resourceUrl}/${this.getPlayerInTeamIdentifier(playerInTeam)}`, playerInTeam, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPlayerInTeam>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPlayerInTeam[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPlayerInTeam[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IPlayerInTeam[]>()], asapScheduler)));
  }

  getPlayerInTeamIdentifier(playerInTeam: Pick<IPlayerInTeam, 'id'>): number {
    return playerInTeam.id;
  }

  comparePlayerInTeam(o1: Pick<IPlayerInTeam, 'id'> | null, o2: Pick<IPlayerInTeam, 'id'> | null): boolean {
    return o1 && o2 ? this.getPlayerInTeamIdentifier(o1) === this.getPlayerInTeamIdentifier(o2) : o1 === o2;
  }

  addPlayerInTeamToCollectionIfMissing<Type extends Pick<IPlayerInTeam, 'id'>>(
    playerInTeamCollection: Type[],
    ...playerInTeamsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const playerInTeams: Type[] = playerInTeamsToCheck.filter(isPresent);
    if (playerInTeams.length > 0) {
      const playerInTeamCollectionIdentifiers = playerInTeamCollection.map(playerInTeamItem =>
        this.getPlayerInTeamIdentifier(playerInTeamItem),
      );
      const playerInTeamsToAdd = playerInTeams.filter(playerInTeamItem => {
        const playerInTeamIdentifier = this.getPlayerInTeamIdentifier(playerInTeamItem);
        if (playerInTeamCollectionIdentifiers.includes(playerInTeamIdentifier)) {
          return false;
        }
        playerInTeamCollectionIdentifiers.push(playerInTeamIdentifier);
        return true;
      });
      return [...playerInTeamsToAdd, ...playerInTeamCollection];
    }
    return playerInTeamCollection;
  }
}
