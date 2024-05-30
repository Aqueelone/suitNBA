import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { TeamSuitNbaComponent } from './list/team-suit-nba.component';
import { TeamSuitNbaDetailComponent } from './detail/team-suit-nba-detail.component';
import { TeamSuitNbaUpdateComponent } from './update/team-suit-nba-update.component';
import TeamSuitNbaResolve from './route/team-suit-nba-routing-resolve.service';

const teamRoute: Routes = [
  {
    path: '',
    component: TeamSuitNbaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TeamSuitNbaDetailComponent,
    resolve: {
      team: TeamSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TeamSuitNbaUpdateComponent,
    resolve: {
      team: TeamSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TeamSuitNbaUpdateComponent,
    resolve: {
      team: TeamSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default teamRoute;
