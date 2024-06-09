import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PlayerInTeamComponent } from './list/player-in-team.component';
import { PlayerInTeamDetailComponent } from './detail/player-in-team-detail.component';
import { PlayerInTeamUpdateComponent } from './update/player-in-team-update.component';
import PlayerInTeamResolve from './route/player-in-team-routing-resolve.service';

const playerInTeamRoute: Routes = [
  {
    path: '',
    component: PlayerInTeamComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlayerInTeamDetailComponent,
    resolve: {
      playerInTeam: PlayerInTeamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlayerInTeamUpdateComponent,
    resolve: {
      playerInTeam: PlayerInTeamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlayerInTeamUpdateComponent,
    resolve: {
      playerInTeam: PlayerInTeamResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default playerInTeamRoute;
