import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGameSuitNba } from '../game-suit-nba.model';
import { GameSuitNbaService } from '../service/game-suit-nba.service';

const gameResolve = (route: ActivatedRouteSnapshot): Observable<null | IGameSuitNba> => {
  const id = route.params['id'];
  if (id) {
    return inject(GameSuitNbaService)
      .find(id)
      .pipe(
        mergeMap((game: HttpResponse<IGameSuitNba>) => {
          if (game.body) {
            return of(game.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default gameResolve;
