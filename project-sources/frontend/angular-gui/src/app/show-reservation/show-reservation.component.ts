import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Store } from '@ngrx/store';
import { TranslateService } from '@ngx-translate/core';

import * as fromInfoActions from '../store/actions/desk-reservation-info.actions';

import { DeskReservationService } from '../deskreservation/deskreservation.service';
import { DeskReservation } from '../deskreservation/reservation.model';
import { DeskReservationState } from '../store/reducers/desk-reservation.reducer';
import { DeskReservationEntityService } from '../store/services/deskreservation-entity.service';

@Component({
  selector: 'app-show-reservation',
  templateUrl: './show-reservation.component.html',
  styleUrls: ['./show-reservation.component.scss']
})
export class ShowReservationComponent implements OnInit {

  // error flag
  errorMessage = '';

  // flag for cancellation. Set to true if cancellation is success.
  cancelled = false;

  // it represents the model object for the form
  deskReservation: DeskReservation = null; // {} as DeskReservation;

  reservationIdentifier = '';

  constructor(
    private deskReservationService: DeskReservationService,
    private translate: TranslateService,
    private entityService: DeskReservationEntityService,
    private store: Store<DeskReservationState>,
  ) {}

  ngOnInit(): void {
  }

  onSubmitReservationIdentifierForm(reservationIdentifierForm: NgForm) {
    // check if the form is valid
    if (!reservationIdentifierForm.form.valid) {
        return false;
    }

    this.deskReservationService.getReservationByIdentifier(this.reservationIdentifier)
      .subscribe( (data: DeskReservation) => {
        this.errorMessage = '';
        this.deskReservation = {...data};
      },
      (error) => {
        console.log('Error while getting desk reservation by the given identifier: ' + this.reservationIdentifier);
        // get the translated labels/messages
        this.translate.get('showReservationPage.reservationNotFound').subscribe( str => {
          this.errorMessage = str;
        });
      }
      );
  }

  onCancelReservation()
  {
    if (this.deskReservation && this.deskReservation.reservationIdentifier) {
      // read the identifier
      const reservationIdentifier = this.deskReservation.reservationIdentifier;
      // cancel the reservation with the identifier
      this.deskReservationService.deleteReservationByIdentifier(reservationIdentifier)
        .subscribe( (data) => {
          // cancellation is successfully done
          this.cancelled = true;
          this.errorMessage = '';
          // remove this desk reservation from the local NgRx store
          this.removeReservationEntryFromNgRxStore(this.deskReservation);
        },
        (error) => {
          console.log('Error while getting desk reservation by the given identifier: ' + reservationIdentifier);
          this.cancelled = false;
          // get the translated labels/messages
          this.translate.get('showReservationPage.cancelReservationFailed').subscribe( str => {
            this.errorMessage = str;
          });
        }
        );
    }
    else {
      // normally we should have reservation...
      console.log('well, no reservation found. Nothing to do.');
    }
  }

  removeReservationEntryFromNgRxStore(reservation : DeskReservation)
  {
      // remove the given reservation only from the local NgRx Data store (no HTTP request to the remote storage)
      this.entityService.removeOneFromCache(reservation);
      // updating the reservation info state
      this.store.dispatch(fromInfoActions.UpdateDeskReservationInfoAction({deskNumber: reservation.deskNumber, id: 0}));
  }

}
