import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGameSuitNba } from '../game-suit-nba.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../game-suit-nba.test-samples';

import { GameSuitNbaService } from './game-suit-nba.service';

const requireRestSample: IGameSuitNba = {
  ...sampleWithRequiredData,
};

describe('GameSuitNba Service', () => {
  let service: GameSuitNbaService;
  let httpMock: HttpTestingController;
  let expectedResult: IGameSuitNba | IGameSuitNba[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GameSuitNbaService);
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

    it('should create a GameSuitNba', () => {
      const game = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(game).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a GameSuitNba', () => {
      const game = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(game).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a GameSuitNba', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of GameSuitNba', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a GameSuitNba', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a GameSuitNba', () => {
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

    describe('addGameSuitNbaToCollectionIfMissing', () => {
      it('should add a GameSuitNba to an empty array', () => {
        const game: IGameSuitNba = sampleWithRequiredData;
        expectedResult = service.addGameSuitNbaToCollectionIfMissing([], game);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(game);
      });

      it('should not add a GameSuitNba to an array that contains it', () => {
        const game: IGameSuitNba = sampleWithRequiredData;
        const gameCollection: IGameSuitNba[] = [
          {
            ...game,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addGameSuitNbaToCollectionIfMissing(gameCollection, game);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a GameSuitNba to an array that doesn't contain it", () => {
        const game: IGameSuitNba = sampleWithRequiredData;
        const gameCollection: IGameSuitNba[] = [sampleWithPartialData];
        expectedResult = service.addGameSuitNbaToCollectionIfMissing(gameCollection, game);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(game);
      });

      it('should add only unique GameSuitNba to an array', () => {
        const gameArray: IGameSuitNba[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const gameCollection: IGameSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addGameSuitNbaToCollectionIfMissing(gameCollection, ...gameArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const game: IGameSuitNba = sampleWithRequiredData;
        const game2: IGameSuitNba = sampleWithPartialData;
        expectedResult = service.addGameSuitNbaToCollectionIfMissing([], game, game2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(game);
        expect(expectedResult).toContain(game2);
      });

      it('should accept null and undefined values', () => {
        const game: IGameSuitNba = sampleWithRequiredData;
        expectedResult = service.addGameSuitNbaToCollectionIfMissing([], null, game, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(game);
      });

      it('should return initial array if no GameSuitNba is added', () => {
        const gameCollection: IGameSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addGameSuitNbaToCollectionIfMissing(gameCollection, undefined, null);
        expect(expectedResult).toEqual(gameCollection);
      });
    });

    describe('compareGameSuitNba', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareGameSuitNba(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareGameSuitNba(entity1, entity2);
        const compareResult2 = service.compareGameSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareGameSuitNba(entity1, entity2);
        const compareResult2 = service.compareGameSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareGameSuitNba(entity1, entity2);
        const compareResult2 = service.compareGameSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
