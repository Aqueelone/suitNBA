import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { GameSuitNbaDetailComponent } from './game-suit-nba-detail.component';

describe('GameSuitNba Management Detail Component', () => {
  let comp: GameSuitNbaDetailComponent;
  let fixture: ComponentFixture<GameSuitNbaDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GameSuitNbaDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: GameSuitNbaDetailComponent,
              resolve: { game: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(GameSuitNbaDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GameSuitNbaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load game on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', GameSuitNbaDetailComponent);

      // THEN
      expect(instance.game()).toEqual(expect.objectContaining({ id: 123 }));
    });
  });

  describe('PreviousState', () => {
    it('Should navigate to previous state', () => {
      jest.spyOn(window.history, 'back');
      comp.previousState();
      expect(window.history.back).toHaveBeenCalled();
    });
  });
});
