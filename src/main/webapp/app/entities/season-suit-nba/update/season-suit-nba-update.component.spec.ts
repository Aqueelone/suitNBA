import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { SeasonSuitNbaService } from '../service/season-suit-nba.service';
import { ISeasonSuitNba } from '../season-suit-nba.model';
import { SeasonSuitNbaFormService } from './season-suit-nba-form.service';

import { SeasonSuitNbaUpdateComponent } from './season-suit-nba-update.component';

describe('SeasonSuitNba Management Update Component', () => {
  let comp: SeasonSuitNbaUpdateComponent;
  let fixture: ComponentFixture<SeasonSuitNbaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let seasonFormService: SeasonSuitNbaFormService;
  let seasonService: SeasonSuitNbaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, SeasonSuitNbaUpdateComponent],
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
      .overrideTemplate(SeasonSuitNbaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SeasonSuitNbaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    seasonFormService = TestBed.inject(SeasonSuitNbaFormService);
    seasonService = TestBed.inject(SeasonSuitNbaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const season: ISeasonSuitNba = { id: 456 };

      activatedRoute.data = of({ season });
      comp.ngOnInit();

      expect(comp.season).toEqual(season);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISeasonSuitNba>>();
      const season = { id: 123 };
      jest.spyOn(seasonFormService, 'getSeasonSuitNba').mockReturnValue(season);
      jest.spyOn(seasonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ season });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: season }));
      saveSubject.complete();

      // THEN
      expect(seasonFormService.getSeasonSuitNba).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(seasonService.update).toHaveBeenCalledWith(expect.objectContaining(season));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISeasonSuitNba>>();
      const season = { id: 123 };
      jest.spyOn(seasonFormService, 'getSeasonSuitNba').mockReturnValue({ id: null });
      jest.spyOn(seasonService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ season: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: season }));
      saveSubject.complete();

      // THEN
      expect(seasonFormService.getSeasonSuitNba).toHaveBeenCalled();
      expect(seasonService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISeasonSuitNba>>();
      const season = { id: 123 };
      jest.spyOn(seasonService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ season });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(seasonService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
