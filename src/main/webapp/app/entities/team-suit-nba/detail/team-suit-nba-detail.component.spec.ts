import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { of } from 'rxjs';

import { TeamSuitNbaDetailComponent } from './team-suit-nba-detail.component';

describe('TeamSuitNba Management Detail Component', () => {
  let comp: TeamSuitNbaDetailComponent;
  let fixture: ComponentFixture<TeamSuitNbaDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TeamSuitNbaDetailComponent],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: TeamSuitNbaDetailComponent,
              resolve: { team: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(TeamSuitNbaDetailComponent, '')
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TeamSuitNbaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load team on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', TeamSuitNbaDetailComponent);

      // THEN
      expect(instance.team()).toEqual(expect.objectContaining({ id: 123 }));
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
