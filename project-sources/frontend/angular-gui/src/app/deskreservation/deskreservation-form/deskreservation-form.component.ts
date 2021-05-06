import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import * as fromInfoActions from '../../store/actions/desk-reservation-info.actions';
import { DeskReservation } from '../reservation.model';
import { DeskReservationState } from './../../store/reducers/desk-reservation.reducer';
import { DeskReservationEntityService } from './../../store/services/deskreservation-entity.service';

@Component({
  selector: 'app-deskreservation-form',
  templateUrl: './deskreservation-form.component.html',
  styleUrls: ['./deskreservation-form.component.scss']
})
export class DeskreservationFormComponent implements OnInit {

  // it is the desk number retrieved from the query parameters
  desknumber = '';

  // it is 'true' if this page is running in 'editor mode'
  editor = false;

  // it is set to true if the checkbox is checked
  checkboxAccept = false;

  // it is true if the form is submitted
  submitted = false;

  // it represents the model object for the form
  deskReservation: DeskReservation = {} as DeskReservation;

  /**
   * Constructor
   */
  constructor(
    private route: ActivatedRoute,
    private store: Store<DeskReservationState>,
    private entityService: DeskReservationEntityService
  ) {}

  ngOnInit() {
    // get the desk number
    this.desknumber = this.route.snapshot.queryParamMap.get('desknumber');
    // get the editor mode (optional)
    this.editor = this.route.snapshot.queryParamMap.get('editor') === 'true';
  }

  /**
   * This method handles the form submit event
   */
  onSubmit(reservationForm: NgForm) {
      if (!reservationForm.form.valid) {
        return false;
      }
      // set desk number
      this.deskReservation.deskNumber = Number(this.desknumber);

      // set status
      this.deskReservation.status = 1;

      this.entityService.add(this.deskReservation)
      .subscribe(
          (data) => {
            // set the 'deskReservation' with the given data
            this.deskReservation = data;
            // set the 'submitted' flag
            this.submitted = true;
            // updating the info state
            this.store.dispatch(fromInfoActions.UpdateDeskReservationInfoAction(
              {
                deskNumber: this.deskReservation.deskNumber,
                id: this.deskReservation.id
              }
            ));
          },
          (err) => {
            console.log('error while saving the desk reservation: ' + err);
          }
      );
  }

}
