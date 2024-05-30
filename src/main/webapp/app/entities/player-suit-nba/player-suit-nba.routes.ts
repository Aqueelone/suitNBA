import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PlayerSuitNbaComponent } from './list/player-suit-nba.component';
import { PlayerSuitNbaDetailComponent } from './detail/player-suit-nba-detail.component';
import { PlayerSuitNbaUpdateComponent } from './update/player-suit-nba-update.component';
import PlayerSuitNbaResolve from './route/player-suit-nba-routing-resolve.service';

const playerRoute: Routes = [
  {
    path: '',
    component: PlayerSuitNbaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlayerSuitNbaDetailComponent,
    resolve: {
      player: PlayerSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlayerSuitNbaUpdateComponent,
    resolve: {
      player: PlayerSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlayerSuitNbaUpdateComponent,
    resolve: {
      player: PlayerSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default playerRoute;
