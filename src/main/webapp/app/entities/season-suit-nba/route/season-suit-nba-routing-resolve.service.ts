import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISeasonSuitNba } from '../season-suit-nba.model';
import { SeasonSuitNbaService } from '../service/season-suit-nba.service';

const seasonResolve = (route: ActivatedRouteSnapshot): Observable<null | ISeasonSuitNba> => {
  const id = route.params['id'];
  if (id) {
    return inject(SeasonSuitNbaService)
      .find(id)
      .pipe(
        mergeMap((season: HttpResponse<ISeasonSuitNba>) => {
          if (season.body) {
            return of(season.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default seasonResolve;
