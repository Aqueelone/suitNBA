import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { ITeamSuitNba } from 'app/entities/team-suit-nba/team-suit-nba.model';
import { TeamSuitNbaService } from 'app/entities/team-suit-nba/service/team-suit-nba.service';
import { ISeasonSuitNba } from 'app/entities/season-suit-nba/season-suit-nba.model';
import { SeasonSuitNbaService } from 'app/entities/season-suit-nba/service/season-suit-nba.service';
import { ITeamInSeasonSuitNba } from '../team-in-season-suit-nba.model';
import { TeamInSeasonSuitNbaService } from '../service/team-in-season-suit-nba.service';
import { TeamInSeasonSuitNbaFormService } from './team-in-season-suit-nba-form.service';

import { TeamInSeasonSuitNbaUpdateComponent } from './team-in-season-suit-nba-update.component';

describe('TeamInSeasonSuitNba Management Update Component', () => {
  let comp: TeamInSeasonSuitNbaUpdateComponent;
  let fixture: ComponentFixture<TeamInSeasonSuitNbaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teamInSeasonFormService: TeamInSeasonSuitNbaFormService;
  let teamInSeasonService: TeamInSeasonSuitNbaService;
  let teamService: TeamSuitNbaService;
  let seasonService: SeasonSuitNbaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TeamInSeasonSuitNbaUpdateComponent],
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
      .overrideTemplate(TeamInSeasonSuitNbaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamInSeasonSuitNbaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teamInSeasonFormService = TestBed.inject(TeamInSeasonSuitNbaFormService);
    teamInSeasonService = TestBed.inject(TeamInSeasonSuitNbaService);
    teamService = TestBed.inject(TeamSuitNbaService);
    seasonService = TestBed.inject(SeasonSuitNbaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TeamSuitNba query and add missing value', () => {
      const teamInSeason: ITeamInSeasonSuitNba = { id: 456 };
      const team: ITeamSuitNba = { id: 25853 };
      teamInSeason.team = team;

      const teamCollection: ITeamSuitNba[] = [{ id: 22964 }];
      jest.spyOn(teamService, 'query').mockReturnValue(of(new HttpResponse({ body: teamCollection })));
      const additionalTeamSuitNbas = [team];
      const expectedCollection: ITeamSuitNba[] = [...additionalTeamSuitNbas, ...teamCollection];
      jest.spyOn(teamService, 'addTeamSuitNbaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamInSeason });
      comp.ngOnInit();

      expect(teamService.query).toHaveBeenCalled();
      expect(teamService.addTeamSuitNbaToCollectionIfMissing).toHaveBeenCalledWith(
        teamCollection,
        ...additionalTeamSuitNbas.map(expect.objectContaining),
      );
      expect(comp.teamsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call SeasonSuitNba query and add missing value', () => {
      const teamInSeason: ITeamInSeasonSuitNba = { id: 456 };
      const season: ISeasonSuitNba = { id: 6798 };
      teamInSeason.season = season;

      const seasonCollection: ISeasonSuitNba[] = [{ id: 29700 }];
      jest.spyOn(seasonService, 'query').mockReturnValue(of(new HttpResponse({ body: seasonCollection })));
      const additionalSeasonSuitNbas = [season];
      const expectedCollection: ISeasonSuitNba[] = [...additionalSeasonSuitNbas, ...seasonCollection];
      jest.spyOn(seasonService, 'addSeasonSuitNbaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ teamInSeason });
      comp.ngOnInit();

      expect(seasonService.query).toHaveBeenCalled();
      expect(seasonService.addSeasonSuitNbaToCollectionIfMissing).toHaveBeenCalledWith(
        seasonCollection,
        ...additionalSeasonSuitNbas.map(expect.objectContaining),
      );
      expect(comp.seasonsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const teamInSeason: ITeamInSeasonSuitNba = { id: 456 };
      const team: ITeamSuitNba = { id: 13750 };
      teamInSeason.team = team;
      const season: ISeasonSuitNba = { id: 22583 };
      teamInSeason.season = season;

      activatedRoute.data = of({ teamInSeason });
      comp.ngOnInit();

      expect(comp.teamsSharedCollection).toContain(team);
      expect(comp.seasonsSharedCollection).toContain(season);
      expect(comp.teamInSeason).toEqual(teamInSeason);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamInSeasonSuitNba>>();
      const teamInSeason = { id: 123 };
      jest.spyOn(teamInSeasonFormService, 'getTeamInSeasonSuitNba').mockReturnValue(teamInSeason);
      jest.spyOn(teamInSeasonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamInSeason });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamInSeason }));
      saveSubject.complete();

      // THEN
      expect(teamInSeasonFormService.getTeamInSeasonSuitNba).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(teamInSeasonService.update).toHaveBeenCalledWith(expect.objectContaining(teamInSeason));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamInSeasonSuitNba>>();
      const teamInSeason = { id: 123 };
      jest.spyOn(teamInSeasonFormService, 'getTeamInSeasonSuitNba').mockReturnValue({ id: null });
      jest.spyOn(teamInSeasonService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamInSeason: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: teamInSeason }));
      saveSubject.complete();

      // THEN
      expect(teamInSeasonFormService.getTeamInSeasonSuitNba).toHaveBeenCalled();
      expect(teamInSeasonService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamInSeasonSuitNba>>();
      const teamInSeason = { id: 123 };
      jest.spyOn(teamInSeasonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ teamInSeason });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teamInSeasonService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTeamSuitNba', () => {
      it('Should forward to teamService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(teamService, 'compareTeamSuitNba');
        comp.compareTeamSuitNba(entity, entity2);
        expect(teamService.compareTeamSuitNba).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
