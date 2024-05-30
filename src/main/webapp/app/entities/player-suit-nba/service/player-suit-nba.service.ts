import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPlayerSuitNba, NewPlayerSuitNba } from '../player-suit-nba.model';

export type PartialUpdatePlayerSuitNba = Partial<IPlayerSuitNba> & Pick<IPlayerSuitNba, 'id'>;

export type EntityResponseType = HttpResponse<IPlayerSuitNba>;
export type EntityArrayResponseType = HttpResponse<IPlayerSuitNba[]>;

@Injectable({ providedIn: 'root' })
export class PlayerSuitNbaService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/players');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/players/_search');

  create(player: NewPlayerSuitNba): Observable<EntityResponseType> {
    return this.http.post<IPlayerSuitNba>(this.resourceUrl, player, { observe: 'response' });
  }

  update(player: IPlayerSuitNba): Observable<EntityResponseType> {
    return this.http.put<IPlayerSuitNba>(`${this.resourceUrl}/${this.getPlayerSuitNbaIdentifier(player)}`, player, { observe: 'response' });
  }

  partialUpdate(player: PartialUpdatePlayerSuitNba): Observable<EntityResponseType> {
    return this.http.patch<IPlayerSuitNba>(`${this.resourceUrl}/${this.getPlayerSuitNbaIdentifier(player)}`, player, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPlayerSuitNba>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPlayerSuitNba[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPlayerSuitNba[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IPlayerSuitNba[]>()], asapScheduler)));
  }

  getPlayerSuitNbaIdentifier(player: Pick<IPlayerSuitNba, 'id'>): number {
    return player.id;
  }

  comparePlayerSuitNba(o1: Pick<IPlayerSuitNba, 'id'> | null, o2: Pick<IPlayerSuitNba, 'id'> | null): boolean {
    return o1 && o2 ? this.getPlayerSuitNbaIdentifier(o1) === this.getPlayerSuitNbaIdentifier(o2) : o1 === o2;
  }

  addPlayerSuitNbaToCollectionIfMissing<Type extends Pick<IPlayerSuitNba, 'id'>>(
    playerCollection: Type[],
    ...playersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const players: Type[] = playersToCheck.filter(isPresent);
    if (players.length > 0) {
      const playerCollectionIdentifiers = playerCollection.map(playerItem => this.getPlayerSuitNbaIdentifier(playerItem));
      const playersToAdd = players.filter(playerItem => {
        const playerIdentifier = this.getPlayerSuitNbaIdentifier(playerItem);
        if (playerCollectionIdentifiers.includes(playerIdentifier)) {
          return false;
        }
        playerCollectionIdentifiers.push(playerIdentifier);
        return true;
      });
      return [...playersToAdd, ...playerCollection];
    }
    return playerCollection;
  }
}
