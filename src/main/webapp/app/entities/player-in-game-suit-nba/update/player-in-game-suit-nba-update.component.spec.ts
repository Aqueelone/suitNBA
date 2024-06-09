import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ITeamInGameSuitNba } from 'app/entities/team-in-game-suit-nba/team-in-game-suit-nba.model';
import { TeamInGameSuitNbaService } from 'app/entities/team-in-game-suit-nba/service/team-in-game-suit-nba.service';
import { IPlayerSuitNba } from 'app/entities/player-suit-nba/player-suit-nba.model';
import { PlayerSuitNbaService } from 'app/entities/player-suit-nba/service/player-suit-nba.service';
import { IGameSuitNba } from 'app/entities/game-suit-nba/game-suit-nba.model';
import { GameSuitNbaService } from 'app/entities/game-suit-nba/service/game-suit-nba.service';
import { IPlayerInGameSuitNba } from '../player-in-game-suit-nba.model';
import { PlayerInGameSuitNbaService } from '../service/player-in-game-suit-nba.service';
import { PlayerInGameSuitNbaFormService } from './player-in-game-suit-nba-form.service';

import { PlayerInGameSuitNbaUpdateComponent } from './player-in-game-suit-nba-update.component';

describe('PlayerInGameSuitNba Management Update Component', () => {
  let comp: PlayerInGameSuitNbaUpdateComponent;
  let fixture: ComponentFixture<PlayerInGameSuitNbaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let playerInGameFormService: PlayerInGameSuitNbaFormService;
  let playerInGameService: PlayerInGameSuitNbaService;
  let teamInGameService: TeamInGameSuitNbaService;
  let playerService: PlayerSuitNbaService;
  let gameService: GameSuitNbaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, PlayerInGameSuitNbaUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PlayerInGameSuitNbaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlayerInGameSuitNbaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    playerInGameFormService = TestBed.inject(PlayerInGameSuitNbaFormService);
    playerInGameService = TestBed.inject(PlayerInGameSuitNbaService);
    teamInGameService = TestBed.inject(TeamInGameSuitNbaService);
    playerService = TestBed.inject(PlayerSuitNbaService);
    gameService = TestBed.inject(GameSuitNbaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TeamInGameSuitNba query and add missing value', () => {
      const playerInGame: IPlayerInGameSuitNba = { id: 456 };
      const team: ITeamInGameSuitNba = { id: 18252 };
      playerInGame.team = team;

      const teamInGameCollection: ITeamInGameSuitNba[] = [{ id: 9161 }];
      jest.spyOn(teamInGameService, 'query').mockReturnValue(of(new HttpResponse({ body: teamInGameCollection })));
      const additionalTeamInGameSuitNbas = [team];
      const expectedCollection: ITeamInGameSuitNba[] = [...additionalTeamInGameSuitNbas, ...teamInGameCollection];
      jest.spyOn(teamInGameService, 'addTeamInGameSuitNbaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ playerInGame });
      comp.ngOnInit();

      expect(teamInGameService.query).toHaveBeenCalled();
      expect(teamInGameService.addTeamInGameSuitNbaToCollectionIfMissing).toHaveBeenCalledWith(
        teamInGameCollection,
        ...additionalTeamInGameSuitNbas.map(expect.objectContaining),
      );
      expect(comp.teamInGamesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call PlayerSuitNba query and add missing value', () => {
      const playerInGame: IPlayerInGameSuitNba = { id: 456 };
      const player: IPlayerSuitNba = { id: 4777 };
      playerInGame.player = player;

      const playerCollection: IPlayerSuitNba[] = [{ id: 7249 }];
      jest.spyOn(playerService, 'query').mockReturnValue(of(new HttpResponse({ body: playerCollection })));
      const additionalPlayerSuitNbas = [player];
      const expectedCollection: IPlayerSuitNba[] = [...additionalPlayerSuitNbas, ...playerCollection];
      jest.spyOn(playerService, 'addPlayerSuitNbaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ playerInGame });
      comp.ngOnInit();

      expect(playerService.query).toHaveBeenCalled();
      expect(playerService.addPlayerSuitNbaToCollectionIfMissing).toHaveBeenCalledWith(
        playerCollection,
        ...additionalPlayerSuitNbas.map(expect.objectContaining),
      );
      expect(comp.playersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call GameSuitNba query and add missing value', () => {
      const playerInGame: IPlayerInGameSuitNba = { id: 456 };
      const game: IGameSuitNba = { id: 10044 };
      playerInGame.game = game;

      const gameCollection: IGameSuitNba[] = [{ id: 26666 }];
      jest.spyOn(gameService, 'query').mockReturnValue(of(new HttpResponse({ body: gameCollection })));
      const additionalGameSuitNbas = [game];
      const expectedCollection: IGameSuitNba[] = [...additionalGameSuitNbas, ...gameCollection];
      jest.spyOn(gameService, 'addGameSuitNbaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ playerInGame });
      comp.ngOnInit();

      expect(gameService.query).toHaveBeenCalled();
      expect(gameService.addGameSuitNbaToCollectionIfMissing).toHaveBeenCalledWith(
        gameCollection,
        ...additionalGameSuitNbas.map(expect.objectContaining),
      );
      expect(comp.gamesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const playerInGame: IPlayerInGameSuitNba = { id: 456 };
      const team: ITeamInGameSuitNba = { id: 297 };
      playerInGame.team = team;
      const player: IPlayerSuitNba = { id: 9645 };
      playerInGame.player = player;
      const game: IGameSuitNba = { id: 9278 };
      playerInGame.game = game;

      activatedRoute.data = of({ playerInGame });
      comp.ngOnInit();

      expect(comp.teamInGamesSharedCollection).toContain(team);
      expect(comp.playersSharedCollection).toContain(player);
      expect(comp.gamesSharedCollection).toContain(game);
      expect(comp.playerInGame).toEqual(playerInGame);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerInGameSuitNba>>();
      const playerInGame = { id: 123 };
      jest.spyOn(playerInGameFormService, 'getPlayerInGameSuitNba').mockReturnValue(playerInGame);
      jest.spyOn(playerInGameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerInGame });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playerInGame }));
      saveSubject.complete();

      // THEN
      expect(playerInGameFormService.getPlayerInGameSuitNba).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(playerInGameService.update).toHaveBeenCalledWith(expect.objectContaining(playerInGame));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerInGameSuitNba>>();
      const playerInGame = { id: 123 };
      jest.spyOn(playerInGameFormService, 'getPlayerInGameSuitNba').mockReturnValue({ id: null });
      jest.spyOn(playerInGameService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerInGame: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playerInGame }));
      saveSubject.complete();

      // THEN
      expect(playerInGameFormService.getPlayerInGameSuitNba).toHaveBeenCalled();
      expect(playerInGameService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerInGameSuitNba>>();
      const playerInGame = { id: 123 };
      jest.spyOn(playerInGameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerInGame });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(playerInGameService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTeamInGameSuitNba', () => {
      it('Should forward to teamInGameService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(teamInGameService, 'compareTeamInGameSuitNba');
        comp.compareTeamInGameSuitNba(entity, entity2);
        expect(teamInGameService.compareTeamInGameSuitNba).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePlayerSuitNba', () => {
      it('Should forward to playerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(playerService, 'comparePlayerSuitNba');
        comp.comparePlayerSuitNba(entity, entity2);
        expect(playerService.comparePlayerSuitNba).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareGameSuitNba', () => {
      it('Should forward to gameService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(gameService, 'compareGameSuitNba');
        comp.compareGameSuitNba(entity, entity2);
        expect(gameService.compareGameSuitNba).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
