import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ISeasonSuitNba } from 'app/entities/season-suit-nba/season-suit-nba.model';
import { SeasonSuitNbaService } from 'app/entities/season-suit-nba/service/season-suit-nba.service';
import { GameSuitNbaService } from '../service/game-suit-nba.service';
import { IGameSuitNba } from '../game-suit-nba.model';
import { GameSuitNbaFormService } from './game-suit-nba-form.service';

import { GameSuitNbaUpdateComponent } from './game-suit-nba-update.component';

describe('GameSuitNba Management Update Component', () => {
  let comp: GameSuitNbaUpdateComponent;
  let fixture: ComponentFixture<GameSuitNbaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let gameFormService: GameSuitNbaFormService;
  let gameService: GameSuitNbaService;
  let seasonService: SeasonSuitNbaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, GameSuitNbaUpdateComponent],
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
      .overrideTemplate(GameSuitNbaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GameSuitNbaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gameFormService = TestBed.inject(GameSuitNbaFormService);
    gameService = TestBed.inject(GameSuitNbaService);
    seasonService = TestBed.inject(SeasonSuitNbaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call SeasonSuitNba query and add missing value', () => {
      const game: IGameSuitNba = { id: 456 };
      const season: ISeasonSuitNba = { id: 18884 };
      game.season = season;

      const seasonCollection: ISeasonSuitNba[] = [{ id: 22307 }];
      jest.spyOn(seasonService, 'query').mockReturnValue(of(new HttpResponse({ body: seasonCollection })));
      const additionalSeasonSuitNbas = [season];
      const expectedCollection: ISeasonSuitNba[] = [...additionalSeasonSuitNbas, ...seasonCollection];
      jest.spyOn(seasonService, 'addSeasonSuitNbaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ game });
      comp.ngOnInit();

      expect(seasonService.query).toHaveBeenCalled();
      expect(seasonService.addSeasonSuitNbaToCollectionIfMissing).toHaveBeenCalledWith(
        seasonCollection,
        ...additionalSeasonSuitNbas.map(expect.objectContaining),
      );
      expect(comp.seasonsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const game: IGameSuitNba = { id: 456 };
      const season: ISeasonSuitNba = { id: 14913 };
      game.season = season;

      activatedRoute.data = of({ game });
      comp.ngOnInit();

      expect(comp.seasonsSharedCollection).toContain(season);
      expect(comp.game).toEqual(game);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameSuitNba>>();
      const game = { id: 123 };
      jest.spyOn(gameFormService, 'getGameSuitNba').mockReturnValue(game);
      jest.spyOn(gameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ game });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: game }));
      saveSubject.complete();

      // THEN
      expect(gameFormService.getGameSuitNba).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(gameService.update).toHaveBeenCalledWith(expect.objectContaining(game));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameSuitNba>>();
      const game = { id: 123 };
      jest.spyOn(gameFormService, 'getGameSuitNba').mockReturnValue({ id: null });
      jest.spyOn(gameService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ game: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: game }));
      saveSubject.complete();

      // THEN
      expect(gameFormService.getGameSuitNba).toHaveBeenCalled();
      expect(gameService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IGameSuitNba>>();
      const game = { id: 123 };
      jest.spyOn(gameService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ game });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gameService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareSeasonSuitNba', () => {
      it('Should forward to seasonService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(seasonService, 'compareSeasonSuitNba');
        comp.compareSeasonSuitNba(entity, entity2);
        expect(seasonService.compareSeasonSuitNba).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
