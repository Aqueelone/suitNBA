import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IGameSuitNba, NewGameSuitNba } from '../game-suit-nba.model';

export type PartialUpdateGameSuitNba = Partial<IGameSuitNba> & Pick<IGameSuitNba, 'id'>;

export type EntityResponseType = HttpResponse<IGameSuitNba>;
export type EntityArrayResponseType = HttpResponse<IGameSuitNba[]>;

@Injectable({ providedIn: 'root' })
export class GameSuitNbaService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/games');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/games/_search');

  create(game: NewGameSuitNba): Observable<EntityResponseType> {
    return this.http.post<IGameSuitNba>(this.resourceUrl, game, { observe: 'response' });
  }

  update(game: IGameSuitNba): Observable<EntityResponseType> {
    return this.http.put<IGameSuitNba>(`${this.resourceUrl}/${this.getGameSuitNbaIdentifier(game)}`, game, { observe: 'response' });
  }

  partialUpdate(game: PartialUpdateGameSuitNba): Observable<EntityResponseType> {
    return this.http.patch<IGameSuitNba>(`${this.resourceUrl}/${this.getGameSuitNbaIdentifier(game)}`, game, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGameSuitNba>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGameSuitNba[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IGameSuitNba[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<IGameSuitNba[]>()], asapScheduler)));
  }

  getGameSuitNbaIdentifier(game: Pick<IGameSuitNba, 'id'>): number {
    return game.id;
  }

  compareGameSuitNba(o1: Pick<IGameSuitNba, 'id'> | null, o2: Pick<IGameSuitNba, 'id'> | null): boolean {
    return o1 && o2 ? this.getGameSuitNbaIdentifier(o1) === this.getGameSuitNbaIdentifier(o2) : o1 === o2;
  }

  addGameSuitNbaToCollectionIfMissing<Type extends Pick<IGameSuitNba, 'id'>>(
    gameCollection: Type[],
    ...gamesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const games: Type[] = gamesToCheck.filter(isPresent);
    if (games.length > 0) {
      const gameCollectionIdentifiers = gameCollection.map(gameItem => this.getGameSuitNbaIdentifier(gameItem));
      const gamesToAdd = games.filter(gameItem => {
        const gameIdentifier = this.getGameSuitNbaIdentifier(gameItem);
        if (gameCollectionIdentifiers.includes(gameIdentifier)) {
          return false;
        }
        gameCollectionIdentifiers.push(gameIdentifier);
        return true;
      });
      return [...gamesToAdd, ...gameCollection];
    }
    return gameCollection;
  }
}
