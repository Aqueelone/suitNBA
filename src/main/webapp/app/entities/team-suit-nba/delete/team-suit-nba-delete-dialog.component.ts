import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITeamSuitNba } from '../team-suit-nba.model';
import { TeamSuitNbaService } from '../service/team-suit-nba.service';

@Component({
  standalone: true,
  templateUrl: './team-suit-nba-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TeamSuitNbaDeleteDialogComponent {
  team?: ITeamSuitNba;

  protected teamService = inject(TeamSuitNbaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.teamService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
