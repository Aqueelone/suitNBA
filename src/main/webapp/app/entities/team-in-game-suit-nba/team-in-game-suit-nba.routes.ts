import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { TeamInGameSuitNbaComponent } from './list/team-in-game-suit-nba.component';
import { TeamInGameSuitNbaDetailComponent } from './detail/team-in-game-suit-nba-detail.component';
import { TeamInGameSuitNbaUpdateComponent } from './update/team-in-game-suit-nba-update.component';
import TeamInGameSuitNbaResolve from './route/team-in-game-suit-nba-routing-resolve.service';

const teamInGameRoute: Routes = [
  {
    path: '',
    component: TeamInGameSuitNbaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TeamInGameSuitNbaDetailComponent,
    resolve: {
      teamInGame: TeamInGameSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TeamInGameSuitNbaUpdateComponent,
    resolve: {
      teamInGame: TeamInGameSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TeamInGameSuitNbaUpdateComponent,
    resolve: {
      teamInGame: TeamInGameSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default teamInGameRoute;
