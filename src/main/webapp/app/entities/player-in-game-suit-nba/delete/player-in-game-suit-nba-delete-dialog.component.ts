import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPlayerInGameSuitNba } from '../player-in-game-suit-nba.model';
import { PlayerInGameSuitNbaService } from '../service/player-in-game-suit-nba.service';

@Component({
  standalone: true,
  templateUrl: './player-in-game-suit-nba-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PlayerInGameSuitNbaDeleteDialogComponent {
  playerInGame?: IPlayerInGameSuitNba;

  protected playerInGameService = inject(PlayerInGameSuitNbaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.playerInGameService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
