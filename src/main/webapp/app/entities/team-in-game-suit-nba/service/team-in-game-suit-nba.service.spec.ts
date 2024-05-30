import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITeamInGameSuitNba } from '../team-in-game-suit-nba.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../team-in-game-suit-nba.test-samples';

import { TeamInGameSuitNbaService } from './team-in-game-suit-nba.service';

const requireRestSample: ITeamInGameSuitNba = {
  ...sampleWithRequiredData,
};

describe('TeamInGameSuitNba Service', () => {
  let service: TeamInGameSuitNbaService;
  let httpMock: HttpTestingController;
  let expectedResult: ITeamInGameSuitNba | ITeamInGameSuitNba[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TeamInGameSuitNbaService);
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

    it('should create a TeamInGameSuitNba', () => {
      const teamInGame = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(teamInGame).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TeamInGameSuitNba', () => {
      const teamInGame = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(teamInGame).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TeamInGameSuitNba', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TeamInGameSuitNba', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TeamInGameSuitNba', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a TeamInGameSuitNba', () => {
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

    describe('addTeamInGameSuitNbaToCollectionIfMissing', () => {
      it('should add a TeamInGameSuitNba to an empty array', () => {
        const teamInGame: ITeamInGameSuitNba = sampleWithRequiredData;
        expectedResult = service.addTeamInGameSuitNbaToCollectionIfMissing([], teamInGame);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamInGame);
      });

      it('should not add a TeamInGameSuitNba to an array that contains it', () => {
        const teamInGame: ITeamInGameSuitNba = sampleWithRequiredData;
        const teamInGameCollection: ITeamInGameSuitNba[] = [
          {
            ...teamInGame,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTeamInGameSuitNbaToCollectionIfMissing(teamInGameCollection, teamInGame);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TeamInGameSuitNba to an array that doesn't contain it", () => {
        const teamInGame: ITeamInGameSuitNba = sampleWithRequiredData;
        const teamInGameCollection: ITeamInGameSuitNba[] = [sampleWithPartialData];
        expectedResult = service.addTeamInGameSuitNbaToCollectionIfMissing(teamInGameCollection, teamInGame);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamInGame);
      });

      it('should add only unique TeamInGameSuitNba to an array', () => {
        const teamInGameArray: ITeamInGameSuitNba[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const teamInGameCollection: ITeamInGameSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addTeamInGameSuitNbaToCollectionIfMissing(teamInGameCollection, ...teamInGameArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const teamInGame: ITeamInGameSuitNba = sampleWithRequiredData;
        const teamInGame2: ITeamInGameSuitNba = sampleWithPartialData;
        expectedResult = service.addTeamInGameSuitNbaToCollectionIfMissing([], teamInGame, teamInGame2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teamInGame);
        expect(expectedResult).toContain(teamInGame2);
      });

      it('should accept null and undefined values', () => {
        const teamInGame: ITeamInGameSuitNba = sampleWithRequiredData;
        expectedResult = service.addTeamInGameSuitNbaToCollectionIfMissing([], null, teamInGame, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teamInGame);
      });

      it('should return initial array if no TeamInGameSuitNba is added', () => {
        const teamInGameCollection: ITeamInGameSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addTeamInGameSuitNbaToCollectionIfMissing(teamInGameCollection, undefined, null);
        expect(expectedResult).toEqual(teamInGameCollection);
      });
    });

    describe('compareTeamInGameSuitNba', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTeamInGameSuitNba(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTeamInGameSuitNba(entity1, entity2);
        const compareResult2 = service.compareTeamInGameSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTeamInGameSuitNba(entity1, entity2);
        const compareResult2 = service.compareTeamInGameSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTeamInGameSuitNba(entity1, entity2);
        const compareResult2 = service.compareTeamInGameSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
