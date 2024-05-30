import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISeasonSuitNba } from '../season-suit-nba.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../season-suit-nba.test-samples';

import { SeasonSuitNbaService } from './season-suit-nba.service';

const requireRestSample: ISeasonSuitNba = {
  ...sampleWithRequiredData,
};

describe('SeasonSuitNba Service', () => {
  let service: SeasonSuitNbaService;
  let httpMock: HttpTestingController;
  let expectedResult: ISeasonSuitNba | ISeasonSuitNba[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SeasonSuitNbaService);
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

    it('should create a SeasonSuitNba', () => {
      const season = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(season).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SeasonSuitNba', () => {
      const season = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(season).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SeasonSuitNba', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SeasonSuitNba', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a SeasonSuitNba', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a SeasonSuitNba', () => {
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

    describe('addSeasonSuitNbaToCollectionIfMissing', () => {
      it('should add a SeasonSuitNba to an empty array', () => {
        const season: ISeasonSuitNba = sampleWithRequiredData;
        expectedResult = service.addSeasonSuitNbaToCollectionIfMissing([], season);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(season);
      });

      it('should not add a SeasonSuitNba to an array that contains it', () => {
        const season: ISeasonSuitNba = sampleWithRequiredData;
        const seasonCollection: ISeasonSuitNba[] = [
          {
            ...season,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSeasonSuitNbaToCollectionIfMissing(seasonCollection, season);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SeasonSuitNba to an array that doesn't contain it", () => {
        const season: ISeasonSuitNba = sampleWithRequiredData;
        const seasonCollection: ISeasonSuitNba[] = [sampleWithPartialData];
        expectedResult = service.addSeasonSuitNbaToCollectionIfMissing(seasonCollection, season);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(season);
      });

      it('should add only unique SeasonSuitNba to an array', () => {
        const seasonArray: ISeasonSuitNba[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const seasonCollection: ISeasonSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addSeasonSuitNbaToCollectionIfMissing(seasonCollection, ...seasonArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const season: ISeasonSuitNba = sampleWithRequiredData;
        const season2: ISeasonSuitNba = sampleWithPartialData;
        expectedResult = service.addSeasonSuitNbaToCollectionIfMissing([], season, season2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(season);
        expect(expectedResult).toContain(season2);
      });

      it('should accept null and undefined values', () => {
        const season: ISeasonSuitNba = sampleWithRequiredData;
        expectedResult = service.addSeasonSuitNbaToCollectionIfMissing([], null, season, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(season);
      });

      it('should return initial array if no SeasonSuitNba is added', () => {
        const seasonCollection: ISeasonSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addSeasonSuitNbaToCollectionIfMissing(seasonCollection, undefined, null);
        expect(expectedResult).toEqual(seasonCollection);
      });
    });

    describe('compareSeasonSuitNba', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSeasonSuitNba(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSeasonSuitNba(entity1, entity2);
        const compareResult2 = service.compareSeasonSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSeasonSuitNba(entity1, entity2);
        const compareResult2 = service.compareSeasonSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSeasonSuitNba(entity1, entity2);
        const compareResult2 = service.compareSeasonSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
