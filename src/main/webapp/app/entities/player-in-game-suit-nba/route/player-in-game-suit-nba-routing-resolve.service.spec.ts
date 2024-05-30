import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { IPlayerInGameSuitNba } from '../player-in-game-suit-nba.model';
import { PlayerInGameSuitNbaService } from '../service/player-in-game-suit-nba.service';

import playerInGameResolve from './player-in-game-suit-nba-routing-resolve.service';

describe('PlayerInGameSuitNba routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: PlayerInGameSuitNbaService;
  let resultPlayerInGameSuitNba: IPlayerInGameSuitNba | null | undefined;

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
    service = TestBed.inject(PlayerInGameSuitNbaService);
    resultPlayerInGameSuitNba = undefined;
  });

  describe('resolve', () => {
    it('should return IPlayerInGameSuitNba returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        playerInGameResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPlayerInGameSuitNba = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPlayerInGameSuitNba).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        playerInGameResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPlayerInGameSuitNba = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultPlayerInGameSuitNba).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<IPlayerInGameSuitNba>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        playerInGameResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultPlayerInGameSuitNba = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPlayerInGameSuitNba).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
