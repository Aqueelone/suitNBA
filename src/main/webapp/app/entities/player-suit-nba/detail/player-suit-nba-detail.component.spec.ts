import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { PlayerSuitNbaDetailComponent } from './player-suit-nba-detail.component';

describe('PlayerSuitNba Management Detail Component', () => {
  let comp: PlayerSuitNbaDetailComponent;
  let fixture: ComponentFixture<PlayerSuitNbaDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlayerSuitNbaDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: PlayerSuitNbaDetailComponent,
              resolve: { player: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PlayerSuitNbaDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayerSuitNbaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load player on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PlayerSuitNbaDetailComponent);

      // THEN
      expect(instance.player()).toEqual(expect.objectContaining({ id: 123 }));
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
