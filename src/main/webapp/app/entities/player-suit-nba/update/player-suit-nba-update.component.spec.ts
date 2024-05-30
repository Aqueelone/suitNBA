import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { PlayerSuitNbaService } from '../service/player-suit-nba.service';
import { IPlayerSuitNba } from '../player-suit-nba.model';
import { PlayerSuitNbaFormService } from './player-suit-nba-form.service';

import { PlayerSuitNbaUpdateComponent } from './player-suit-nba-update.component';

describe('PlayerSuitNba Management Update Component', () => {
  let comp: PlayerSuitNbaUpdateComponent;
  let fixture: ComponentFixture<PlayerSuitNbaUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let playerFormService: PlayerSuitNbaFormService;
  let playerService: PlayerSuitNbaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, PlayerSuitNbaUpdateComponent],
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
      .overrideTemplate(PlayerSuitNbaUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlayerSuitNbaUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    playerFormService = TestBed.inject(PlayerSuitNbaFormService);
    playerService = TestBed.inject(PlayerSuitNbaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const player: IPlayerSuitNba = { id: 456 };

      activatedRoute.data = of({ player });
      comp.ngOnInit();

      expect(comp.player).toEqual(player);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerSuitNba>>();
      const player = { id: 123 };
      jest.spyOn(playerFormService, 'getPlayerSuitNba').mockReturnValue(player);
      jest.spyOn(playerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ player });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: player }));
      saveSubject.complete();

      // THEN
      expect(playerFormService.getPlayerSuitNba).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(playerService.update).toHaveBeenCalledWith(expect.objectContaining(player));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerSuitNba>>();
      const player = { id: 123 };
      jest.spyOn(playerFormService, 'getPlayerSuitNba').mockReturnValue({ id: null });
      jest.spyOn(playerService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ player: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: player }));
      saveSubject.complete();

      // THEN
      expect(playerFormService.getPlayerSuitNba).toHaveBeenCalled();
      expect(playerService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerSuitNba>>();
      const player = { id: 123 };
      jest.spyOn(playerService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ player });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(playerService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
