import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlayerInTeam } from '../player-in-team.model';
import { PlayerInTeamService } from '../service/player-in-team.service';

const playerInTeamResolve = (route: ActivatedRouteSnapshot): Observable<null | IPlayerInTeam> => {
  const id = route.params['id'];
  if (id) {
    return inject(PlayerInTeamService)
      .find(id)
      .pipe(
        mergeMap((playerInTeam: HttpResponse<IPlayerInTeam>) => {
          if (playerInTeam.body) {
            return of(playerInTeam.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default playerInTeamResolve;
