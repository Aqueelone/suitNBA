import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PlayerInGameSuitNbaDetailComponent } from './player-in-game-suit-nba-detail.component';

describe('PlayerInGameSuitNba Management Detail Component', () => {
  let comp: PlayerInGameSuitNbaDetailComponent;
  let fixture: ComponentFixture<PlayerInGameSuitNbaDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlayerInGameSuitNbaDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: PlayerInGameSuitNbaDetailComponent,
              resolve: { playerInGame: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PlayerInGameSuitNbaDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayerInGameSuitNbaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load playerInGame on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PlayerInGameSuitNbaDetailComponent);

      // THEN
      expect(instance.playerInGame()).toEqual(expect.objectContaining({ id: 123 }));
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
