import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlayerInGameSuitNba } from '../player-in-game-suit-nba.model';
import { PlayerInGameSuitNbaService } from '../service/player-in-game-suit-nba.service';

const playerInGameResolve = (route: ActivatedRouteSnapshot): Observable<null | IPlayerInGameSuitNba> => {
  const id = route.params['id'];
  if (id) {
    return inject(PlayerInGameSuitNbaService)
      .find(id)
      .pipe(
        mergeMap((playerInGame: HttpResponse<IPlayerInGameSuitNba>) => {
          if (playerInGame.body) {
            return of(playerInGame.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default playerInGameResolve;
