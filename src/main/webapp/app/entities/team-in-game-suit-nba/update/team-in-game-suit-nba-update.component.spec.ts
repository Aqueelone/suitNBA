import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ITeamInSeasonSuitNba } from 'app/entities/team-in-season-suit-nba/team-in-season-suit-nba.model';
import { TeamInSeasonSuitNbaService } from 'app/entities/team-in-season-suit-nba/service/team-in-season-suit-nba.service';
import { IGameSuitNba } from 'app/entities/game-suit-nba/game-suit-nba.model';
import { GameSuitNbaService } from 'app/entities/game-suit-nba/service/game-suit-nba.service';
import { ITeamInGameSuitNba } from '../team-in-game-suit-nba.model';
import { TeamInGameSuitNbaService } from '../service/team-in-game-suit-nba.service';
import { TeamInGameSuitNbaFormService } from './team-in-game-suit-nba-form.service';

import { TeamInGameSuitNbaUpdateComponent } from './team-in-game-suit-nba-update.component';

describe('TeamInGameSuitNba Management Update Component', () => {
  let comp: TeamInGameSuitNbaUpdateComponent;
  let fixture: ComponentFixture<TeamInGameSuitNbaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teamInGameFormService: TeamInGameSuitNbaFormService;
  let teamInGameService: TeamInGameSuitNbaService;
  let teamInSeasonService: TeamInSeasonSuitNbaService;
  let gameService: GameSuitNbaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TeamInGameSuitNbaUpdateComponent],
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
      .overrideTemplate(TeamInGameSuitNbaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamInGameSuitNbaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teamInGameFormService = TestBed.inject(TeamInGameSuitNbaFormService);
    teamInGameService = TestBed.inject(TeamInGameSuitNbaService);
    teamInSeasonService = TestBed.inject(TeamInSeasonSuitNbaService);
    gameService = TestBed.inject(GameSuitNbaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TeamInSeasonSuitNba query and add missing value', () => {
      const teamInGame: ITeamInGameSuitNba = { id: 456 };
      const team: ITeamInSeasonSuitNba = { id: 25654 };
      teamInGame.team = team;

      const teamInSeasonCollection: ITeamInSeasonSuitNba[] = [{ id: 12004 }];
      jest.spyOn(teamInSeasonService, 'query').mockReturnValue(of(new HttpResponse({ body: teamInSeasonCollection })));
      const additionalTeamInSeasonSuitNbas = [team];
      const expectedCollection: ITeamInSeasonSuitNba[] = [...additionalTeamInSeasonSuitNbas, ...teamInSeasonCollection];
      jest.spyOn(teamInSeasonService, 'addTeamInSeasonSuitNbaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamInGame });
      comp.ngOnInit();

      expect(teamInSeasonService.query).toHaveBeenCalled();
      expect(teamInSeasonService.addTeamInSeasonSuitNbaToCollectionIfMissing).toHaveBeenCalledWith(
        teamInSeasonCollection,
        ...additionalTeamInSeasonSuitNbas.map(expect.objectContaining),
      );
      expect(comp.teamInSeasonsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call GameSuitNba query and add missing value', () => {
      const teamInGame: ITeamInGameSuitNba = { id: 456 };
      const game: IGameSuitNba = { id: 4092 };
      teamInGame.game = game;

      const gameCollection: IGameSuitNba[] = [{ id: 25585 }];
      jest.spyOn(gameService, 'query').mockReturnValue(of(new HttpResponse({ body: gameCollection })));
      const additionalGameSuitNbas = [game];
      const expectedCollection: IGameSuitNba[] = [...additionalGameSuitNbas, ...gameCollection];
      jest.spyOn(gameService, 'addGameSuitNbaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamInGame });
      comp.ngOnInit();

      expect(gameService.query).toHaveBeenCalled();
      expect(gameService.addGameSuitNbaToCollectionIfMissing).toHaveBeenCalledWith(
        gameCollection,
        ...additionalGameSuitNbas.map(expect.objectContaining),
      );
      expect(comp.gamesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teamInGame: ITeamInGameSuitNba = { id: 456 };
      const team: ITeamInSeasonSuitNba = { id: 32482 };
      teamInGame.team = team;
      const game: IGameSuitNba = { id: 22854 };
      teamInGame.game = game;

      activatedRoute.data = of({ teamInGame });
      comp.ngOnInit();

      expect(comp.teamInSeasonsSharedCollection).toContain(team);
      expect(comp.gamesSharedCollection).toContain(game);
      expect(comp.teamInGame).toEqual(teamInGame);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamInGameSuitNba>>();
      const teamInGame = { id: 123 };
      jest.spyOn(teamInGameFormService, 'getTeamInGameSuitNba').mockReturnValue(teamInGame);
      jest.spyOn(teamInGameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamInGame });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamInGame }));
      saveSubject.complete();

      // THEN
      expect(teamInGameFormService.getTeamInGameSuitNba).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(teamInGameService.update).toHaveBeenCalledWith(expect.objectContaining(teamInGame));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamInGameSuitNba>>();
      const teamInGame = { id: 123 };
      jest.spyOn(teamInGameFormService, 'getTeamInGameSuitNba').mockReturnValue({ id: null });
      jest.spyOn(teamInGameService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamInGame: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamInGame }));
      saveSubject.complete();

      // THEN
      expect(teamInGameFormService.getTeamInGameSuitNba).toHaveBeenCalled();
      expect(teamInGameService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamInGameSuitNba>>();
      const teamInGame = { id: 123 };
      jest.spyOn(teamInGameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamInGame });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teamInGameService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTeamInSeasonSuitNba', () => {
      it('Should forward to teamInSeasonService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(teamInSeasonService, 'compareTeamInSeasonSuitNba');
        comp.compareTeamInSeasonSuitNba(entity, entity2);
        expect(teamInSeasonService.compareTeamInSeasonSuitNba).toHaveBeenCalledWith(entity, entity2);
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
