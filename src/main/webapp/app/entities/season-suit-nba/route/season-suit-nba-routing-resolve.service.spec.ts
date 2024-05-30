import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';

import { ISeasonSuitNba } from '../season-suit-nba.model';
import { SeasonSuitNbaService } from '../service/season-suit-nba.service';

import seasonResolve from './season-suit-nba-routing-resolve.service';

describe('SeasonSuitNba routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let service: SeasonSuitNbaService;
  let resultSeasonSuitNba: ISeasonSuitNba | null | undefined;

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
    service = TestBed.inject(SeasonSuitNbaService);
    resultSeasonSuitNba = undefined;
  });

  describe('resolve', () => {
    it('should return ISeasonSuitNba returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        seasonResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultSeasonSuitNba = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultSeasonSuitNba).toEqual({ id: 123 });
    });

    it('should return null if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      TestBed.runInInjectionContext(() => {
        seasonResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultSeasonSuitNba = result;
          },
        });
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultSeasonSuitNba).toEqual(null);
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse<ISeasonSuitNba>({ body: null })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      TestBed.runInInjectionContext(() => {
        seasonResolve(mockActivatedRouteSnapshot).subscribe({
          next(result) {
            resultSeasonSuitNba = result;
          },
        });
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultSeasonSuitNba).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
