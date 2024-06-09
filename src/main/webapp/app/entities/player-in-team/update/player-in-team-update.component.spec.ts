import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject, from } from 'rxjs';

import { IPlayerSuitNba } from 'app/entities/player-suit-nba/player-suit-nba.model';
import { PlayerSuitNbaService } from 'app/entities/player-suit-nba/service/player-suit-nba.service';
import { ITeamInSeasonSuitNba } from 'app/entities/team-in-season-suit-nba/team-in-season-suit-nba.model';
import { TeamInSeasonSuitNbaService } from 'app/entities/team-in-season-suit-nba/service/team-in-season-suit-nba.service';
import { IPlayerInTeam } from '../player-in-team.model';
import { PlayerInTeamService } from '../service/player-in-team.service';
import { PlayerInTeamFormService } from './player-in-team-form.service';

import { PlayerInTeamUpdateComponent } from './player-in-team-update.component';

describe('PlayerInTeam Management Update Component', () => {
  let comp: PlayerInTeamUpdateComponent;
  let fixture: ComponentFixture<PlayerInTeamUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let playerInTeamFormService: PlayerInTeamFormService;
  let playerInTeamService: PlayerInTeamService;
  let playerService: PlayerSuitNbaService;
  let teamInSeasonService: TeamInSeasonSuitNbaService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, PlayerInTeamUpdateComponent],
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
      .overrideTemplate(PlayerInTeamUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlayerInTeamUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    playerInTeamFormService = TestBed.inject(PlayerInTeamFormService);
    playerInTeamService = TestBed.inject(PlayerInTeamService);
    playerService = TestBed.inject(PlayerSuitNbaService);
    teamInSeasonService = TestBed.inject(TeamInSeasonSuitNbaService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call PlayerSuitNba query and add missing value', () => {
      const playerInTeam: IPlayerInTeam = { id: 456 };
      const player: IPlayerSuitNba = { id: 10297 };
      playerInTeam.player = player;

      const playerCollection: IPlayerSuitNba[] = [{ id: 13555 }];
      jest.spyOn(playerService, 'query').mockReturnValue(of(new HttpResponse({ body: playerCollection })));
      const additionalPlayerSuitNbas = [player];
      const expectedCollection: IPlayerSuitNba[] = [...additionalPlayerSuitNbas, ...playerCollection];
      jest.spyOn(playerService, 'addPlayerSuitNbaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ playerInTeam });
      comp.ngOnInit();

      expect(playerService.query).toHaveBeenCalled();
      expect(playerService.addPlayerSuitNbaToCollectionIfMissing).toHaveBeenCalledWith(
        playerCollection,
        ...additionalPlayerSuitNbas.map(expect.objectContaining),
      );
      expect(comp.playersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call TeamInSeasonSuitNba query and add missing value', () => {
      const playerInTeam: IPlayerInTeam = { id: 456 };
      const teamInSeason: ITeamInSeasonSuitNba = { id: 25052 };
      playerInTeam.teamInSeason = teamInSeason;

      const teamInSeasonCollection: ITeamInSeasonSuitNba[] = [{ id: 5801 }];
      jest.spyOn(teamInSeasonService, 'query').mockReturnValue(of(new HttpResponse({ body: teamInSeasonCollection })));
      const additionalTeamInSeasonSuitNbas = [teamInSeason];
      const expectedCollection: ITeamInSeasonSuitNba[] = [...additionalTeamInSeasonSuitNbas, ...teamInSeasonCollection];
      jest.spyOn(teamInSeasonService, 'addTeamInSeasonSuitNbaToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ playerInTeam });
      comp.ngOnInit();

      expect(teamInSeasonService.query).toHaveBeenCalled();
      expect(teamInSeasonService.addTeamInSeasonSuitNbaToCollectionIfMissing).toHaveBeenCalledWith(
        teamInSeasonCollection,
        ...additionalTeamInSeasonSuitNbas.map(expect.objectContaining),
      );
      expect(comp.teamInSeasonsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const playerInTeam: IPlayerInTeam = { id: 456 };
      const player: IPlayerSuitNba = { id: 24099 };
      playerInTeam.player = player;
      const teamInSeason: ITeamInSeasonSuitNba = { id: 16932 };
      playerInTeam.teamInSeason = teamInSeason;

      activatedRoute.data = of({ playerInTeam });
      comp.ngOnInit();

      expect(comp.playersSharedCollection).toContain(player);
      expect(comp.teamInSeasonsSharedCollection).toContain(teamInSeason);
      expect(comp.playerInTeam).toEqual(playerInTeam);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerInTeam>>();
      const playerInTeam = { id: 123 };
      jest.spyOn(playerInTeamFormService, 'getPlayerInTeam').mockReturnValue(playerInTeam);
      jest.spyOn(playerInTeamService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerInTeam });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playerInTeam }));
      saveSubject.complete();

      // THEN
      expect(playerInTeamFormService.getPlayerInTeam).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(playerInTeamService.update).toHaveBeenCalledWith(expect.objectContaining(playerInTeam));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerInTeam>>();
      const playerInTeam = { id: 123 };
      jest.spyOn(playerInTeamFormService, 'getPlayerInTeam').mockReturnValue({ id: null });
      jest.spyOn(playerInTeamService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerInTeam: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: playerInTeam }));
      saveSubject.complete();

      // THEN
      expect(playerInTeamFormService.getPlayerInTeam).toHaveBeenCalled();
      expect(playerInTeamService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPlayerInTeam>>();
      const playerInTeam = { id: 123 };
      jest.spyOn(playerInTeamService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ playerInTeam });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(playerInTeamService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePlayerSuitNba', () => {
      it('Should forward to playerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(playerService, 'comparePlayerSuitNba');
        comp.comparePlayerSuitNba(entity, entity2);
        expect(playerService.comparePlayerSuitNba).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareTeamInSeasonSuitNba', () => {
      it('Should forward to teamInSeasonService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(teamInSeasonService, 'compareTeamInSeasonSuitNba');
        comp.compareTeamInSeasonSuitNba(entity, entity2);
        expect(teamInSeasonService.compareTeamInSeasonSuitNba).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
