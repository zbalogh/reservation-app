import { Component, OnDestroy, OnInit, TemplateRef } from '@angular/core';
import { Store } from '@ngrx/store';
import { BsModalRef, BsModalService } from 'ngx-bootstrap/modal';
import { Observable, Subscription } from 'rxjs';
import * as fromInfoActions from '../../store/actions/desk-reservation-info.actions';
import { DeskReservation } from './../../deskreservation/reservation.model';
import { DeskReservationState } from './../../store/reducers/desk-reservation.reducer';
import { DeskReservationEntityService } from './../../store/services/deskreservation-entity.service';

@Component({
  selector: 'app-manage-deskreservation',
  templateUrl: './manage-deskreservation.component.html',
  styleUrls: ['./manage-deskreservation.component.scss']
})
export class ManageDeskreservationComponent implements OnInit, OnDestroy {

  reservationList$: Observable<DeskReservation[]>;
  reservationLoaded$: Observable<boolean>;

  reservationLoadedSubscription: Subscription;

  modalRef: BsModalRef;

  selectedItem: DeskReservation = null;

  constructor(
    private store: Store<DeskReservationState>,
    private modalService: BsModalService,
    private entityService: DeskReservationEntityService
  ) {
      // load data if necessary
      this.loadData();
  }

  /**
   * This method is responsible to load reservation data if necessary (if not loaded yet).
   */
  loadData() {
    // if reservation list is not loaded yet then we load it
    this.reservationLoaded$ = this.entityService.loaded$;

    // subscribe for this reservation
    this.reservationLoadedSubscription = this.reservationLoaded$.subscribe(
      (loaded) => {
          if (!loaded) {
              // get all desk reservations from the ngrx/data based EntityService
              this.entityService.getAll();
          }
      }
    );
  }

  ngOnInit() {
    // now we subscribe for the entities observable in the store
    // in order to get the reservation data list
    this.reservationList$ = this.entityService.entities$;
    console.log('desk reservation list selected from the store.');
  }

  ngOnDestroy() {
    // unsubscribe from the reservation loaded observable
    if (this.reservationLoadedSubscription) {
        this.reservationLoadedSubscription.unsubscribe();
    }
  }

  deleteReservation(reservation: DeskReservation, template: TemplateRef<any>) {
    // set the selectedItem
    this.selectedItem = reservation;
    // open confirm modal
    this.modalRef = this.modalService.show(template, {class: 'modal-sm'});
  }

  confirm(): void {
    this.modalRef.hide();
    console.log('Delete reservation by ID: ' + this.selectedItem.id);
    // const rid = this.selectedItem.id;
    // trigger an effect by the action
    this.entityService.delete(this.selectedItem);
    // updating the info state
    this.store.dispatch(fromInfoActions.UpdateDeskReservationInfoAction({deskNumber: this.selectedItem.deskNumber, id: 0}));
    // reset the selectedItem
    this.selectedItem = null;
  }

  decline(): void {
    // reset the selectedItem
    this.selectedItem = null;
    this.modalRef.hide();
  }

}
