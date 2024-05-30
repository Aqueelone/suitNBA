import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeamInGameSuitNba } from '../team-in-game-suit-nba.model';
import { TeamInGameSuitNbaService } from '../service/team-in-game-suit-nba.service';

const teamInGameResolve = (route: ActivatedRouteSnapshot): Observable<null | ITeamInGameSuitNba> => {
  const id = route.params['id'];
  if (id) {
    return inject(TeamInGameSuitNbaService)
      .find(id)
      .pipe(
        mergeMap((teamInGame: HttpResponse<ITeamInGameSuitNba>) => {
          if (teamInGame.body) {
            return of(teamInGame.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default teamInGameResolve;
