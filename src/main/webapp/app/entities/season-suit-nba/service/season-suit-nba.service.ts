import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, asapScheduler, scheduled } from 'rxjs';

import { catchError } from 'rxjs/operators';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { ISeasonSuitNba, NewSeasonSuitNba } from '../season-suit-nba.model';

export type PartialUpdateSeasonSuitNba = Partial<ISeasonSuitNba> & Pick<ISeasonSuitNba, 'id'>;

export type EntityResponseType = HttpResponse<ISeasonSuitNba>;
export type EntityArrayResponseType = HttpResponse<ISeasonSuitNba[]>;

@Injectable({ providedIn: 'root' })
export class SeasonSuitNbaService {
  protected http = inject(HttpClient);
  protected applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/seasons');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/seasons/_search');

  create(season: NewSeasonSuitNba): Observable<EntityResponseType> {
    return this.http.post<ISeasonSuitNba>(this.resourceUrl, season, { observe: 'response' });
  }

  update(season: ISeasonSuitNba): Observable<EntityResponseType> {
    return this.http.put<ISeasonSuitNba>(`${this.resourceUrl}/${this.getSeasonSuitNbaIdentifier(season)}`, season, { observe: 'response' });
  }

  partialUpdate(season: PartialUpdateSeasonSuitNba): Observable<EntityResponseType> {
    return this.http.patch<ISeasonSuitNba>(`${this.resourceUrl}/${this.getSeasonSuitNbaIdentifier(season)}`, season, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISeasonSuitNba>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISeasonSuitNba[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISeasonSuitNba[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(catchError(() => scheduled([new HttpResponse<ISeasonSuitNba[]>()], asapScheduler)));
  }

  getSeasonSuitNbaIdentifier(season: Pick<ISeasonSuitNba, 'id'>): number {
    return season.id;
  }

  compareSeasonSuitNba(o1: Pick<ISeasonSuitNba, 'id'> | null, o2: Pick<ISeasonSuitNba, 'id'> | null): boolean {
    return o1 && o2 ? this.getSeasonSuitNbaIdentifier(o1) === this.getSeasonSuitNbaIdentifier(o2) : o1 === o2;
  }

  addSeasonSuitNbaToCollectionIfMissing<Type extends Pick<ISeasonSuitNba, 'id'>>(
    seasonCollection: Type[],
    ...seasonsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const seasons: Type[] = seasonsToCheck.filter(isPresent);
    if (seasons.length > 0) {
      const seasonCollectionIdentifiers = seasonCollection.map(seasonItem => this.getSeasonSuitNbaIdentifier(seasonItem));
      const seasonsToAdd = seasons.filter(seasonItem => {
        const seasonIdentifier = this.getSeasonSuitNbaIdentifier(seasonItem);
        if (seasonCollectionIdentifiers.includes(seasonIdentifier)) {
          return false;
        }
        seasonCollectionIdentifiers.push(seasonIdentifier);
        return true;
      });
      return [...seasonsToAdd, ...seasonCollection];
    }
    return seasonCollection;
  }
}
