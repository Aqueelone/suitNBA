import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlayerSuitNba } from '../player-suit-nba.model';
import { PlayerSuitNbaService } from '../service/player-suit-nba.service';

const playerResolve = (route: ActivatedRouteSnapshot): Observable<null | IPlayerSuitNba> => {
  const id = route.params['id'];
  if (id) {
    return inject(PlayerSuitNbaService)
      .find(id)
      .pipe(
        mergeMap((player: HttpResponse<IPlayerSuitNba>) => {
          if (player.body) {
            return of(player.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default playerResolve;
