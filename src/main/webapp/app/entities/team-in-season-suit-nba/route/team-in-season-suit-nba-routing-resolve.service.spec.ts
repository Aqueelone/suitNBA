import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { ITeamInSeasonSuitNba } from '../team-in-season-suit-nba.model';
import { TeamInSeasonSuitNbaService } from '../service/team-in-season-suit-nba.service';

import teamInSeasonResolve from './team-in-season-suit-nba-routing-resolve.service';

describe('TeamInSeasonSuitNba routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: TeamInSeasonSuitNbaService;
  let resultTeamInSeasonSuitNba: ITeamInSeasonSuitNba | null | undefined;

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
    service = TestBed.inject(TeamInSeasonSuitNbaService);
    resultTeamInSeasonSuitNba = undefined;
  });

  describe('resolve', () => {
    it('should return ITeamInSeasonSuitNba returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        teamInSeasonResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultTeamInSeasonSuitNba = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTeamInSeasonSuitNba).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        teamInSeasonResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultTeamInSeasonSuitNba = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTeamInSeasonSuitNba).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<ITeamInSeasonSuitNba>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        teamInSeasonResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultTeamInSeasonSuitNba = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTeamInSeasonSuitNba).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
