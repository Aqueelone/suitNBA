import { Component, computed, NgZone, inject, OnInit, signal, WritableSignal } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Data, ParamMap, Router, RouterModule } from '@angular/router';
import { combineLatest, filter, Observable, Subscription, tap } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { sortStateSignal, SortDirective, SortByDirective, type SortState, SortService } from 'app/shared/sort';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { FormsModule } from '@angular/forms';

import { ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { SORT, ITEM_DELETED_EVENT, DEFAULT_SORT_DATA } from 'app/config/navigation.constants';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { EntityArrayResponseType, PlayerInTeamService } from '../service/player-in-team.service';
import { PlayerInTeamDeleteDialogComponent } from '../delete/player-in-team-delete-dialog.component';
import { IPlayerInTeam } from '../player-in-team.model';
import { TeamInSeasonSuitNbaService } from '../../team-in-season-suit-nba/service/team-in-season-suit-nba.service';
import { ITeamInSeasonSuitNba } from '../../team-in-season-suit-nba/team-in-season-suit-nba.model';
import { map } from 'rxjs/operators';

@Component({
  standalone: true,
  selector: 'jhi-player-in-team',
  templateUrl: './player-in-team.component.html',
  imports: [
    RouterModule,
    FormsModule,
    SharedModule,
    SortDirective,
    SortByDirective,
    DurationPipe,
    FormatMediumDatetimePipe,
    FormatMediumDatePipe,
    InfiniteScrollModule,
  ],
})
export class PlayerInTeamComponent implements OnInit {
  subscription: Subscription | null = null;
  playerInTeams?: IPlayerInTeam[];
  isLoading = false;

  teamInSeason: ITeamInSeasonSuitNba | undefined | null;
  teamInSeasonsSharedCollection: ITeamInSeasonSuitNba[] = [];

  sortState = sortStateSignal({});
  currentSearch = '';

  itemsPerPage = ITEMS_PER_PAGE;
  links: WritableSignal<{ [key: string]: undefined | { [key: string]: string | undefined } }> = signal({});
  hasMorePage = computed(() => !!this.links().next);
  isFirstFetch = computed(() => Object.keys(this.links()).length === 0);

  public router = inject(Router);
  protected playerInTeamService = inject(PlayerInTeamService);
  protected teamInSeasonService = inject(TeamInSeasonSuitNbaService);
  protected activatedRoute = inject(ActivatedRoute);
  protected sortService = inject(SortService);
  protected parseLinks = inject(ParseLinks);
  protected modalService = inject(NgbModal);
  protected ngZone = inject(NgZone);

  trackId = (_index: number, item: IPlayerInTeam): number => this.playerInTeamService.getPlayerInTeamIdentifier(item);

  ngOnInit(): void {
    this.teamInSeasonService
      .query()
      .pipe(map((res: HttpResponse<ITeamInSeasonSuitNba[]>) => res.body ?? []))
      .pipe(
        map((teamInSeasons: ITeamInSeasonSuitNba[]) =>
          this.teamInSeasonService.addTeamInSeasonSuitNbaToCollectionIfMissing<ITeamInSeasonSuitNba>(teamInSeasons, this.teamInSeason),
        ),
      )
      .subscribe((teamInSeasons: ITeamInSeasonSuitNba[]) => (this.teamInSeasonsSharedCollection = teamInSeasons));

    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.reset()),
        tap(() => this.load()),
      )
      .subscribe();
  }

  reset(): void {
    this.playerInTeams = [];
  }

  loadNextPage(): void {
    this.load();
  }

  search(query: string): void {
    this.currentSearch = query;
    this.navigateToWithComponentValues(this.sortState());
  }

  loadDefaultSortState(): void {
    this.sortState.set(this.sortService.parseSortParam(this.activatedRoute.snapshot.data[DEFAULT_SORT_DATA]));
  }

  delete(playerInTeam: IPlayerInTeam): void {
    const modalRef = this.modalService.open(PlayerInTeamDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.playerInTeam = playerInTeam;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend().subscribe({
      next: (res: EntityArrayResponseType) => {
        this.onResponseSuccess(res);
      },
    });
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event, this.currentSearch);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    if (params.has('search') && params.get('search') !== '') {
      this.currentSearch = params.get('search') as string;
    }
  }

  protected onResponseSuccess(response: EntityArrayResponseType): void {
    this.fillComponentAttributesFromResponseHeader(response.headers);
    const dataFromBody = this.fillComponentAttributesFromResponseBody(response.body);
    this.playerInTeams = dataFromBody;
  }

  protected fillComponentAttributesFromResponseBody(data: IPlayerInTeam[] | null): IPlayerInTeam[] {
    // If there is previous link, data is a infinite scroll pagination content.
    if (this.links().prev) {
      const playerInTeamsNew = this.playerInTeams ?? [];
      if (data) {
        for (const d of data) {
          if (playerInTeamsNew.map(op => op.id).indexOf(d.id) === -1) {
            playerInTeamsNew.push(d);
          }
        }
      }
      return playerInTeamsNew;
    }
    return data ?? [];
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    const linkHeader = headers.get('link');
    if (linkHeader) {
      this.links.set(this.parseLinks.parseAll(linkHeader));
    } else {
      this.links.set({});
    }
  }

  protected queryBackend(): Observable<EntityArrayResponseType> {
    const { currentSearch } = this;

    this.isLoading = true;
    const queryObject: any = {
      size: this.itemsPerPage,
      query: currentSearch,
    };
    if (this.hasMorePage()) {
      Object.assign(queryObject, this.links().next);
    } else if (this.isFirstFetch()) {
      Object.assign(queryObject, { sort: this.sortService.buildSortParam(this.sortState()) });
    }

    if (this.currentSearch && this.currentSearch !== '') {
      return this.playerInTeamService.search(queryObject).pipe(tap(() => (this.isLoading = false)));
    } else {
      return this.playerInTeamService.query(queryObject).pipe(tap(() => (this.isLoading = false)));
    }
  }

  protected handleNavigation(sortState: SortState, currentSearch?: string): void {
    this.links.set({});

    const queryParamsObj = {
      search: currentSearch,
      sort: this.sortService.buildSortParam(sortState),
    };

    this.ngZone.run(() => {
      this.router.navigate(['./'], {
        relativeTo: this.activatedRoute,
        queryParams: queryParamsObj,
      });
    });
  }
}
