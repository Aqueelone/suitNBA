import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITeamSuitNba } from '../team-suit-nba.model';
import { TeamSuitNbaService } from '../service/team-suit-nba.service';

const teamResolve = (route: ActivatedRouteSnapshot): Observable<null | ITeamSuitNba> => {
  const id = route.params['id'];
  if (id) {
    return inject(TeamSuitNbaService)
      .find(id)
      .pipe(
        mergeMap((team: HttpResponse<ITeamSuitNba>) => {
          if (team.body) {
            return of(team.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        }),
      );
  }
  return of(null);
};

export default teamResolve;
