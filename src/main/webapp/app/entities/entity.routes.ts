import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'game-suit-nba',
    data: { pageTitle: 'Games' },
    loadChildren: () => import('./game-suit-nba/game-suit-nba.routes'),
  },
  {
    path: 'player-suit-nba',
    data: { pageTitle: 'Players' },
    loadChildren: () => import('./player-suit-nba/player-suit-nba.routes'),
  },
  {
    path: 'player-in-game-suit-nba',
    data: { pageTitle: 'PlayerInGames' },
    loadChildren: () => import('./player-in-game-suit-nba/player-in-game-suit-nba.routes'),
  },
  {
    path: 'season-suit-nba',
    data: { pageTitle: 'Seasons' },
    loadChildren: () => import('./season-suit-nba/season-suit-nba.routes'),
  },
  {
    path: 'team-suit-nba',
    data: { pageTitle: 'Teams' },
    loadChildren: () => import('./team-suit-nba/team-suit-nba.routes'),
  },
  {
    path: 'team-in-game-suit-nba',
    data: { pageTitle: 'TeamInGames' },
    loadChildren: () => import('./team-in-game-suit-nba/team-in-game-suit-nba.routes'),
  },
  {
    path: 'team-in-season-suit-nba',
    data: { pageTitle: 'TeamInSeasons' },
    loadChildren: () => import('./team-in-season-suit-nba/team-in-season-suit-nba.routes'),
  },
  {
    path: 'player-in-team',
    data: { pageTitle: 'PlayerInTeams' },
    loadChildren: () => import('./player-in-team/player-in-team.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
