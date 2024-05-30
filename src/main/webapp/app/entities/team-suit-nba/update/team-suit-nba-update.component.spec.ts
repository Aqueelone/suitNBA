import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { TeamSuitNbaService } from '../service/team-suit-nba.service';
import { ITeamSuitNba } from '../team-suit-nba.model';
import { TeamSuitNbaFormService } from './team-suit-nba-form.service';

import { TeamSuitNbaUpdateComponent } from './team-suit-nba-update.component';

describe('TeamSuitNba Management Update Component', () => {
  let comp: TeamSuitNbaUpdateComponent;
  let fixture: ComponentFixture<TeamSuitNbaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let teamFormService: TeamSuitNbaFormService;
  let teamService: TeamSuitNbaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, TeamSuitNbaUpdateComponent],
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
      .overrideTemplate(TeamSuitNbaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TeamSuitNbaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    teamFormService = TestBed.inject(TeamSuitNbaFormService);
    teamService = TestBed.inject(TeamSuitNbaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const team: ITeamSuitNba = { id: 456 };

      activatedRoute.data = of({ team });
      comp.ngOnInit();

      expect(comp.team).toEqual(team);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamSuitNba>>();
      const team = { id: 123 };
      jest.spyOn(teamFormService, 'getTeamSuitNba').mockReturnValue(team);
      jest.spyOn(teamService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ team });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: team }));
      saveSubject.complete();

      // THEN
      expect(teamFormService.getTeamSuitNba).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(teamService.update).toHaveBeenCalledWith(expect.objectContaining(team));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamSuitNba>>();
      const team = { id: 123 };
      jest.spyOn(teamFormService, 'getTeamSuitNba').mockReturnValue({ id: null });
      jest.spyOn(teamService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ team: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: team }));
      saveSubject.complete();

      // THEN
      expect(teamFormService.getTeamSuitNba).toHaveBeenCalled();
      expect(teamService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITeamSuitNba>>();
      const team = { id: 123 };
      jest.spyOn(teamService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ team });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(teamService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
