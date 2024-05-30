import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { GameSuitNbaComponent } from './list/game-suit-nba.component';
import { GameSuitNbaDetailComponent } from './detail/game-suit-nba-detail.component';
import { GameSuitNbaUpdateComponent } from './update/game-suit-nba-update.component';
import GameSuitNbaResolve from './route/game-suit-nba-routing-resolve.service';

const gameRoute: Routes = [
  {
    path: '',
    component: GameSuitNbaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: GameSuitNbaDetailComponent,
    resolve: {
      game: GameSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: GameSuitNbaUpdateComponent,
    resolve: {
      game: GameSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: GameSuitNbaUpdateComponent,
    resolve: {
      game: GameSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default gameRoute;
