import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { TeamInSeasonSuitNbaComponent } from './list/team-in-season-suit-nba.component';
import { TeamInSeasonSuitNbaDetailComponent } from './detail/team-in-season-suit-nba-detail.component';
import { TeamInSeasonSuitNbaUpdateComponent } from './update/team-in-season-suit-nba-update.component';
import TeamInSeasonSuitNbaResolve from './route/team-in-season-suit-nba-routing-resolve.service';

const teamInSeasonRoute: Routes = [
  {
    path: '',
    component: TeamInSeasonSuitNbaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TeamInSeasonSuitNbaDetailComponent,
    resolve: {
      teamInSeason: TeamInSeasonSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TeamInSeasonSuitNbaUpdateComponent,
    resolve: {
      teamInSeason: TeamInSeasonSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TeamInSeasonSuitNbaUpdateComponent,
    resolve: {
      teamInSeason: TeamInSeasonSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default teamInSeasonRoute;
