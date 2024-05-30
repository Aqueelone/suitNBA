import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITeamInSeasonSuitNba } from '../team-in-season-suit-nba.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../team-in-season-suit-nba.test-samples';

import { TeamInSeasonSuitNbaService } from './team-in-season-suit-nba.service';

const requireRestSample: ITeamInSeasonSuitNba = {
  ...sampleWithRequiredData,
};

describe('TeamInSeasonSuitNba Service', () => {
  let service: TeamInSeasonSuitNbaService;
  let httpMock: HttpTestingController;
  let expectedResult: ITeamInSeasonSuitNba | ITeamInSeasonSuitNba[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TeamInSeasonSuitNbaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a TeamInSeasonSuitNba', () => {
      const teamInSeason = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(teamInSeason).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TeamInSeasonSuitNba', () => {
      const teamInSeason = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(teamInSeason).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TeamInSeasonSuitNba', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TeamInSeasonSuitNba', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TeamInSeasonSuitNba', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a TeamInSeasonSuitNba', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addTeamInSeasonSuitNbaToCollectionIfMissing', () => {
      it('should add a TeamInSeasonSuitNba to an empty array', () => {
        const teamInSeason: ITeamInSeasonSuitNba = sampleWithRequiredData;
        expectedResult = service.addTeamInSeasonSuitNbaToCollectionIfMissing([], teamInSeason);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamInSeason);
      });

      it('should not add a TeamInSeasonSuitNba to an array that contains it', () => {
        const teamInSeason: ITeamInSeasonSuitNba = sampleWithRequiredData;
        const teamInSeasonCollection: ITeamInSeasonSuitNba[] = [
          {
            ...teamInSeason,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTeamInSeasonSuitNbaToCollectionIfMissing(teamInSeasonCollection, teamInSeason);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TeamInSeasonSuitNba to an array that doesn't contain it", () => {
        const teamInSeason: ITeamInSeasonSuitNba = sampleWithRequiredData;
        const teamInSeasonCollection: ITeamInSeasonSuitNba[] = [sampleWithPartialData];
        expectedResult = service.addTeamInSeasonSuitNbaToCollectionIfMissing(teamInSeasonCollection, teamInSeason);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamInSeason);
      });

      it('should add only unique TeamInSeasonSuitNba to an array', () => {
        const teamInSeasonArray: ITeamInSeasonSuitNba[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const teamInSeasonCollection: ITeamInSeasonSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addTeamInSeasonSuitNbaToCollectionIfMissing(teamInSeasonCollection, ...teamInSeasonArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const teamInSeason: ITeamInSeasonSuitNba = sampleWithRequiredData;
        const teamInSeason2: ITeamInSeasonSuitNba = sampleWithPartialData;
        expectedResult = service.addTeamInSeasonSuitNbaToCollectionIfMissing([], teamInSeason, teamInSeason2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamInSeason);
        expect(expectedResult).toContain(teamInSeason2);
      });

      it('should accept null and undefined values', () => {
        const teamInSeason: ITeamInSeasonSuitNba = sampleWithRequiredData;
        expectedResult = service.addTeamInSeasonSuitNbaToCollectionIfMissing([], null, teamInSeason, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamInSeason);
      });

      it('should return initial array if no TeamInSeasonSuitNba is added', () => {
        const teamInSeasonCollection: ITeamInSeasonSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addTeamInSeasonSuitNbaToCollectionIfMissing(teamInSeasonCollection, undefined, null);
        expect(expectedResult).toEqual(teamInSeasonCollection);
      });
    });

    describe('compareTeamInSeasonSuitNba', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTeamInSeasonSuitNba(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTeamInSeasonSuitNba(entity1, entity2);
        const compareResult2 = service.compareTeamInSeasonSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTeamInSeasonSuitNba(entity1, entity2);
        const compareResult2 = service.compareTeamInSeasonSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTeamInSeasonSuitNba(entity1, entity2);
        const compareResult2 = service.compareTeamInSeasonSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
