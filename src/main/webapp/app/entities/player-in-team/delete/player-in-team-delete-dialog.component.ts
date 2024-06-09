import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPlayerInTeam } from '../player-in-team.model';
import { PlayerInTeamService } from '../service/player-in-team.service';

@Component({
  standalone: true,
  templateUrl: './player-in-team-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PlayerInTeamDeleteDialogComponent {
  playerInTeam?: IPlayerInTeam;

  protected playerInTeamService = inject(PlayerInTeamService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.playerInTeamService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
