import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPlayerInTeam } from '../player-in-team.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../player-in-team.test-samples';

import { PlayerInTeamService } from './player-in-team.service';

const requireRestSample: IPlayerInTeam = {
  ...sampleWithRequiredData,
};

describe('PlayerInTeam Service', () => {
  let service: PlayerInTeamService;
  let httpMock: HttpTestingController;
  let expectedResult: IPlayerInTeam | IPlayerInTeam[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PlayerInTeamService);
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

    it('should create a PlayerInTeam', () => {
      const playerInTeam = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(playerInTeam).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PlayerInTeam', () => {
      const playerInTeam = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(playerInTeam).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PlayerInTeam', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PlayerInTeam', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PlayerInTeam', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    it('should handle exceptions for searching a PlayerInTeam', () => {
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

    describe('addPlayerInTeamToCollectionIfMissing', () => {
      it('should add a PlayerInTeam to an empty array', () => {
        const playerInTeam: IPlayerInTeam = sampleWithRequiredData;
        expectedResult = service.addPlayerInTeamToCollectionIfMissing([], playerInTeam);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(playerInTeam);
      });

      it('should not add a PlayerInTeam to an array that contains it', () => {
        const playerInTeam: IPlayerInTeam = sampleWithRequiredData;
        const playerInTeamCollection: IPlayerInTeam[] = [
          {
            ...playerInTeam,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPlayerInTeamToCollectionIfMissing(playerInTeamCollection, playerInTeam);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PlayerInTeam to an array that doesn't contain it", () => {
        const playerInTeam: IPlayerInTeam = sampleWithRequiredData;
        const playerInTeamCollection: IPlayerInTeam[] = [sampleWithPartialData];
        expectedResult = service.addPlayerInTeamToCollectionIfMissing(playerInTeamCollection, playerInTeam);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(playerInTeam);
      });

      it('should add only unique PlayerInTeam to an array', () => {
        const playerInTeamArray: IPlayerInTeam[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const playerInTeamCollection: IPlayerInTeam[] = [sampleWithRequiredData];
        expectedResult = service.addPlayerInTeamToCollectionIfMissing(playerInTeamCollection, ...playerInTeamArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const playerInTeam: IPlayerInTeam = sampleWithRequiredData;
        const playerInTeam2: IPlayerInTeam = sampleWithPartialData;
        expectedResult = service.addPlayerInTeamToCollectionIfMissing([], playerInTeam, playerInTeam2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(playerInTeam);
        expect(expectedResult).toContain(playerInTeam2);
      });

      it('should accept null and undefined values', () => {
        const playerInTeam: IPlayerInTeam = sampleWithRequiredData;
        expectedResult = service.addPlayerInTeamToCollectionIfMissing([], null, playerInTeam, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(playerInTeam);
      });

      it('should return initial array if no PlayerInTeam is added', () => {
        const playerInTeamCollection: IPlayerInTeam[] = [sampleWithRequiredData];
        expectedResult = service.addPlayerInTeamToCollectionIfMissing(playerInTeamCollection, undefined, null);
        expect(expectedResult).toEqual(playerInTeamCollection);
      });
    });

    describe('comparePlayerInTeam', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePlayerInTeam(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePlayerInTeam(entity1, entity2);
        const compareResult2 = service.comparePlayerInTeam(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePlayerInTeam(entity1, entity2);
        const compareResult2 = service.comparePlayerInTeam(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePlayerInTeam(entity1, entity2);
        const compareResult2 = service.comparePlayerInTeam(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
