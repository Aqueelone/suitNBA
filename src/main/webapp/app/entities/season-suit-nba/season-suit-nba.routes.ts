import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import { SeasonSuitNbaComponent } from './list/season-suit-nba.component';
import { SeasonSuitNbaDetailComponent } from './detail/season-suit-nba-detail.component';
import { SeasonSuitNbaUpdateComponent } from './update/season-suit-nba-update.component';
import SeasonSuitNbaResolve from './route/season-suit-nba-routing-resolve.service';

const seasonRoute: Routes = [
  {
    path: '',
    component: SeasonSuitNbaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SeasonSuitNbaDetailComponent,
    resolve: {
      season: SeasonSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SeasonSuitNbaUpdateComponent,
    resolve: {
      season: SeasonSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SeasonSuitNbaUpdateComponent,
    resolve: {
      season: SeasonSuitNbaResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default seasonRoute;
