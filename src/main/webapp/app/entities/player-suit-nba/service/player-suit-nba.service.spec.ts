import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPlayerSuitNba } from '../player-suit-nba.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../player-suit-nba.test-samples';

import { PlayerSuitNbaService } from './player-suit-nba.service';

const requireRestSample: IPlayerSuitNba = {
  ...sampleWithRequiredData,
};

describe('PlayerSuitNba Service', () => {
  let service: PlayerSuitNbaService;
  let httpMock: HttpTestingController;
  let expectedResult: IPlayerSuitNba | IPlayerSuitNba[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PlayerSuitNbaService);
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

    it('should create a PlayerSuitNba', () => {
      const player = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(player).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PlayerSuitNba', () => {
      const player = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(player).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PlayerSuitNba', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PlayerSuitNba', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PlayerSuitNba', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a PlayerSuitNba', () => {
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

    describe('addPlayerSuitNbaToCollectionIfMissing', () => {
      it('should add a PlayerSuitNba to an empty array', () => {
        const player: IPlayerSuitNba = sampleWithRequiredData;
        expectedResult = service.addPlayerSuitNbaToCollectionIfMissing([], player);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(player);
      });

      it('should not add a PlayerSuitNba to an array that contains it', () => {
        const player: IPlayerSuitNba = sampleWithRequiredData;
        const playerCollection: IPlayerSuitNba[] = [
          {
            ...player,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPlayerSuitNbaToCollectionIfMissing(playerCollection, player);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PlayerSuitNba to an array that doesn't contain it", () => {
        const player: IPlayerSuitNba = sampleWithRequiredData;
        const playerCollection: IPlayerSuitNba[] = [sampleWithPartialData];
        expectedResult = service.addPlayerSuitNbaToCollectionIfMissing(playerCollection, player);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(player);
      });

      it('should add only unique PlayerSuitNba to an array', () => {
        const playerArray: IPlayerSuitNba[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const playerCollection: IPlayerSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addPlayerSuitNbaToCollectionIfMissing(playerCollection, ...playerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const player: IPlayerSuitNba = sampleWithRequiredData;
        const player2: IPlayerSuitNba = sampleWithPartialData;
        expectedResult = service.addPlayerSuitNbaToCollectionIfMissing([], player, player2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(player);
        expect(expectedResult).toContain(player2);
      });

      it('should accept null and undefined values', () => {
        const player: IPlayerSuitNba = sampleWithRequiredData;
        expectedResult = service.addPlayerSuitNbaToCollectionIfMissing([], null, player, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(player);
      });

      it('should return initial array if no PlayerSuitNba is added', () => {
        const playerCollection: IPlayerSuitNba[] = [sampleWithRequiredData];
        expectedResult = service.addPlayerSuitNbaToCollectionIfMissing(playerCollection, undefined, null);
        expect(expectedResult).toEqual(playerCollection);
      });
    });

    describe('comparePlayerSuitNba', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePlayerSuitNba(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePlayerSuitNba(entity1, entity2);
        const compareResult2 = service.comparePlayerSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePlayerSuitNba(entity1, entity2);
        const compareResult2 = service.comparePlayerSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePlayerSuitNba(entity1, entity2);
        const compareResult2 = service.comparePlayerSuitNba(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
