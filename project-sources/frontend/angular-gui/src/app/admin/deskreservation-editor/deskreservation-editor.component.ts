import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { DeskReservation } from './../../deskreservation/reservation.model';
import { DeskReservationEntityService } from './../../store/services/deskreservation-entity.service';

@Component({
  selector: 'app-deskreservation-editor',
  templateUrl: './deskreservation-editor.component.html',
  styleUrls: ['./deskreservation-editor.component.scss']
})
export class DeskreservationEditorComponent implements OnInit, OnDestroy {

  // it is the desk ID in the database
  id = '';

  // it is the desk number retrieved from the query parameters
  desknumber = '';

  // it is 'true' if this page is running in 'editor mode'
  editor = false;

  // it is true if the form is submitted
  submitted = false;

  // it represents the model object for the form
  deskReservation: DeskReservation = {} as DeskReservation;

  // subscription
  reservationSubscription: Subscription;


  constructor(
    private route: ActivatedRoute,
    private entityService: DeskReservationEntityService
  ) {}


  ngOnInit() {
    // get the desk ID
    this.id = this.route.snapshot.queryParamMap.get('id');

    // get the desk number
    this.desknumber = this.route.snapshot.queryParamMap.get('desknumber');

    // get the editor mode (optional)
    this.editor = this.route.snapshot.queryParamMap.get('editor') === 'true';

    // get the selected reservation from the store by the given ID
    this.loadDeskReservation(this.id);
  }

  loadDeskReservation(reservationID: string): void {
      // convert the value into number
      const resID = Number(reservationID);
      // get the selected reservation from the EntityCache by the given ID
      this.reservationSubscription = this.entityService.entities$
      .pipe(
            map(reservations => {
              return reservations.find(reservation => reservation.id === resID);
            })
        )
      .subscribe(
          (data) => {
            if (data) {
                // create a copy from the "data" and assign it to the "deskReservation"
                // this.deskReservation = JSON.parse(JSON.stringify(data));
                this.deskReservation = {...data};
            } else {
                console.log('No reservation data found with the given ID: ' + reservationID);
            }
          }
      );
  }


  ngOnDestroy() {
    // unsubscribe
    this.reservationSubscription.unsubscribe();
  }


  /**
   * This method handles the form submit event
   */
  onSubmit(reservationForm: NgForm) {
    // if the form is invalid then return
    if (!reservationForm.form.valid) {
      return false;
    }

    this.entityService.upsert(this.deskReservation)
    .subscribe(
        (data) => {
            // set the 'deskReservation' with the given data
            this.deskReservation = data;
            // set the 'submitted' flag
            this.submitted = true;
        },
        (err) => {
          console.log('error while saving the desk reservation: ' + err);
        }
    );
  }

}
