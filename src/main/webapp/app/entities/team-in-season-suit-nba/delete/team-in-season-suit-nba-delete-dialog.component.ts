import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITeamInSeasonSuitNba } from '../team-in-season-suit-nba.model';
import { TeamInSeasonSuitNbaService } from '../service/team-in-season-suit-nba.service';

@Component({
  standalone: true,
  templateUrl: './team-in-season-suit-nba-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TeamInSeasonSuitNbaDeleteDialogComponent {
  teamInSeason?: ITeamInSeasonSuitNba;

  protected teamInSeasonService = inject(TeamInSeasonSuitNbaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.teamInSeasonService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
