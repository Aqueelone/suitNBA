import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { PlayerInGameSuitNbaComponent } from './list/player-in-game-suit-nba.component';
import { PlayerInGameSuitNbaDetailComponent } from './detail/player-in-game-suit-nba-detail.component';
import { PlayerInGameSuitNbaUpdateComponent } from './update/player-in-game-suit-nba-update.component';
import PlayerInGameSuitNbaResolve from './route/player-in-game-suit-nba-routing-resolve.service';

const playerInGameRoute: Routes = [
  {
    path: '',
    component: PlayerInGameSuitNbaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlayerInGameSuitNbaDetailComponent,
    resolve: {
      playerInGame: PlayerInGameSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlayerInGameSuitNbaUpdateComponent,
    resolve: {
      playerInGame: PlayerInGameSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlayerInGameSuitNbaUpdateComponent,
    resolve: {
      playerInGame: PlayerInGameSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default playerInGameRoute;
