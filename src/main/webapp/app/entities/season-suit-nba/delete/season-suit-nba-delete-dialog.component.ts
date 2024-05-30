import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISeasonSuitNba } from '../season-suit-nba.model';
import { SeasonSuitNbaService } from '../service/season-suit-nba.service';

@Component({
  standalone: true,
  templateUrl: './season-suit-nba-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SeasonSuitNbaDeleteDialogComponent {
  season?: ISeasonSuitNba;

  protected seasonService = inject(SeasonSuitNbaService);
  protected activeModal = inject(NgbActiveModal);

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.seasonService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
