import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITeamInGameSuitNba } from '../team-in-game-suit-nba.model';
import { TeamInGameSuitNbaService } from '../service/team-in-game-suit-nba.service';

@Component({
  standalone: true,
  templateUrl: './team-in-game-suit-nba-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TeamInGameSuitNbaDeleteDialogComponent {
  teamInGame?: ITeamInGameSuitNba;

  protected teamInGameService = inject(TeamInGameSuitNbaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.teamInGameService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
