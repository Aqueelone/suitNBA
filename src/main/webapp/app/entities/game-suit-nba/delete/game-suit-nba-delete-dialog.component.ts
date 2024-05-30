import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IGameSuitNba } from '../game-suit-nba.model';
import { GameSuitNbaService } from '../service/game-suit-nba.service';

@Component({
  standalone: true,
  templateUrl: './game-suit-nba-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class GameSuitNbaDeleteDialogComponent {
  game?: IGameSuitNba;

  protected gameService = inject(GameSuitNbaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.gameService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
