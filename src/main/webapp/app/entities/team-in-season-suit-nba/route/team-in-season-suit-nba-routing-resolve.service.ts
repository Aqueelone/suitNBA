import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeamInSeasonSuitNba } from '../team-in-season-suit-nba.model';
import { TeamInSeasonSuitNbaService } from '../service/team-in-season-suit-nba.service';

const teamInSeasonResolve = (route: ActivatedRouteSnapshot): Observable<null | ITeamInSeasonSuitNba> => {
  const id = route.params['id'];
  if (id) {
    return inject(TeamInSeasonSuitNbaService)
      .find(id)
      .pipe(
        mergeMap((teamInSeason: HttpResponse<ITeamInSeasonSuitNba>) => {
          if (teamInSeason.body) {
            return of(teamInSeason.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default teamInSeasonResolve;
