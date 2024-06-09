import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { IPlayerInTeam } from '../player-in-team.model';
import { PlayerInTeamService } from '../service/player-in-team.service';

import playerInTeamResolve from './player-in-team-routing-resolve.service';

describe('PlayerInTeam routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: PlayerInTeamService;
  let resultPlayerInTeam: IPlayerInTeam | null | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    service = TestBed.inject(PlayerInTeamService);
    resultPlayerInTeam = undefined;
  });

  describe('resolve', () => {
    it('should return IPlayerInTeam returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        playerInTeamResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPlayerInTeam = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPlayerInTeam).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        playerInTeamResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPlayerInTeam = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultPlayerInTeam).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IPlayerInTeam>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        playerInTeamResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPlayerInTeam = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPlayerInTeam).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
