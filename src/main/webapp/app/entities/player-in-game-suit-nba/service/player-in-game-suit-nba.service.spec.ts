import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPlayerInGameSuitNba } from '../player-in-game-suit-nba.model';
import {
  sampleWithRequiredData,
  sampleWithNewData,
  sampleWithPartialData,
  sampleWithFullData,
} from '../player-in-game-suit-nba.test-samples';

import { PlayerInGameSuitNbaService } from './player-in-game-suit-nba.service';

const requireRestSample: IPlayerInGameSuitNba = {
  ...sampleWithRequiredData,
};

describe('PlayerInGameSuitNba Service', () => {
  let service: PlayerInGameSuitNbaService;
  let httpMock: HttpTestingController;
  let expectedResult: IPlayerInGameSuitNba | IPlayerInGameSuitNba[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PlayerInGameSuitNbaService);
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

    it('should create a PlayerInGameSuitNba', () => {
      const playerInGame = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(playerInGame).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PlayerInGameSuitNba', () => {
      const playerInGame = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(playerInGame).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PlayerInGameSuitNba', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PlayerInGameSuitNba', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PlayerInGameSuitNba', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a PlayerInGameSuitNba', () => {
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

    describe('addPlayerInGameSuitNbaToCollectionIfMissing', () => {
      it('should add a PlayerInGameSuitNba to an empty array', () => {
        const playerInGame: IPlayerInGameSuitNba = sampleWithRequiredData;
        expectedResult = service.addPlayerInGameSuitNbaToCollectionIfMissing([], playerInGame);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(playerInGame);
      });

      it('should not add a PlayerInGameSuitNba to an array that contains it', () => {
        const playerInGame: IPlayerInGameSuitNba = sampleWithRequiredData;
        const playerInGameCollection: IPlayerInGameSuitNba[] = [
          {
            ...playerInGame,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPlayerInGameSuitNbaToCollectionIfMissing(playerInGameCollection, playerInGame);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PlayerInGameSuitNba to an array that doesn't contain it", () => {
        const playerInGame: IPlayerInGameSuitNba = sampleWithRequiredData;
        const playerInGameCollection: IPlayerInGameSuitNba[] = [sampleWithPartialData];
        expectedResult = service.addPlayerInGameSuitNbaToCollectionIfMissing(playerInGameCollection, playerInGame);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(playerInGame);
      });

      it('should add only unique PlayerInGameSuitNba to an array', () => {
        const playerInGameArray: IPlayerInGameSuitNba[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const playerInGameCollection: IPlayerInGameSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addPlayerInGameSuitNbaToCollectionIfMissing(playerInGameCollection, ...playerInGameArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const playerInGame: IPlayerInGameSuitNba = sampleWithRequiredData;
        const playerInGame2: IPlayerInGameSuitNba = sampleWithPartialData;
        expectedResult = service.addPlayerInGameSuitNbaToCollectionIfMissing([], playerInGame, playerInGame2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(playerInGame);
        expect(expectedResult).toContain(playerInGame2);
      });

      it('should accept null and undefined values', () => {
        const playerInGame: IPlayerInGameSuitNba = sampleWithRequiredData;
        expectedResult = service.addPlayerInGameSuitNbaToCollectionIfMissing([], null, playerInGame, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(playerInGame);
      });

      it('should return initial array if no PlayerInGameSuitNba is added', () => {
        const playerInGameCollection: IPlayerInGameSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addPlayerInGameSuitNbaToCollectionIfMissing(playerInGameCollection, undefined, null);
        expect(expectedResult).toEqual(playerInGameCollection);
      });
    });

    describe('comparePlayerInGameSuitNba', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePlayerInGameSuitNba(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePlayerInGameSuitNba(entity1, entity2);
        const compareResult2 = service.comparePlayerInGameSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePlayerInGameSuitNba(entity1, entity2);
        const compareResult2 = service.comparePlayerInGameSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePlayerInGameSuitNba(entity1, entity2);
        const compareResult2 = service.comparePlayerInGameSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
