import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IPlayerInGameSuitNba, NewPlayerInGameSuitNba } from '../player-in-game-suit-nba.model';

export type PartialUpdatePlayerInGameSuitNba = Partial<IPlayerInGameSuitNba> & Pick<IPlayerInGameSuitNba, 'id'>;

export type EntityResponseType = HttpResponse<IPlayerInGameSuitNba>;
export type EntityArrayResponseType = HttpResponse<IPlayerInGameSuitNba[]>;

@Injectable({ providedIn: 'root' })
export class PlayerInGameSuitNbaService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/player-in-games');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/player-in-games/_search');

  create(playerInGame: NewPlayerInGameSuitNba): Observable<EntityResponseType> {
    return this.http.post<IPlayerInGameSuitNba>(this.resourceUrl, playerInGame, { observe: 'response' });
  }

  update(playerInGame: IPlayerInGameSuitNba): Observable<EntityResponseType> {
    return this.http.put<IPlayerInGameSuitNba>(`${this.resourceUrl}/${this.getPlayerInGameSuitNbaIdentifier(playerInGame)}`, playerInGame, {
      observe: 'response',
    });
  }

  partialUpdate(playerInGame: PartialUpdatePlayerInGameSuitNba): Observable<EntityResponseType> {
    return this.http.patch<IPlayerInGameSuitNba>(
      `${this.resourceUrl}/${this.getPlayerInGameSuitNbaIdentifier(playerInGame)}`,
      playerInGame,
      { observe: 'response' },
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPlayerInGameSuitNba>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPlayerInGameSuitNba[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPlayerInGameSuitNba[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IPlayerInGameSuitNba[]>()], asapScheduler)));
  }

  getPlayerInGameSuitNbaIdentifier(playerInGame: Pick<IPlayerInGameSuitNba, 'id'>): number {
    return playerInGame.id;
  }

  comparePlayerInGameSuitNba(o1: Pick<IPlayerInGameSuitNba, 'id'> | null, o2: Pick<IPlayerInGameSuitNba, 'id'> | null): boolean {
    return o1 && o2 ? this.getPlayerInGameSuitNbaIdentifier(o1) === this.getPlayerInGameSuitNbaIdentifier(o2) : o1 === o2;
  }

  addPlayerInGameSuitNbaToCollectionIfMissing<Type extends Pick<IPlayerInGameSuitNba, 'id'>>(
    playerInGameCollection: Type[],
    ...playerInGamesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const playerInGames: Type[] = playerInGamesToCheck.filter(isPresent);
    if (playerInGames.length > 0) {
      const playerInGameCollectionIdentifiers = playerInGameCollection.map(playerInGameItem =>
        this.getPlayerInGameSuitNbaIdentifier(playerInGameItem),
      );
      const playerInGamesToAdd = playerInGames.filter(playerInGameItem => {
        const playerInGameIdentifier = this.getPlayerInGameSuitNbaIdentifier(playerInGameItem);
        if (playerInGameCollectionIdentifiers.includes(playerInGameIdentifier)) {
          return false;
        }
        playerInGameCollectionIdentifiers.push(playerInGameIdentifier);
        return true;
      });
      return [...playerInGamesToAdd, ...playerInGameCollection];
    }
    return playerInGameCollection;
  }
}
