import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPlayerSuitNba } from '../player-suit-nba.model';
import { PlayerSuitNbaService } from '../service/player-suit-nba.service';

@Component({
  standalone: true,
  templateUrl: './player-suit-nba-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PlayerSuitNbaDeleteDialogComponent {
  player?: IPlayerSuitNba;

  protected playerService = inject(PlayerSuitNbaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.playerService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
