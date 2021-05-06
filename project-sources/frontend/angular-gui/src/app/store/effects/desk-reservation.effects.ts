import { DeskReservationService } from './../../deskreservation/deskreservation.service';
import * as fromActions from '../actions/desk-reservation.actions';

import { mergeMap, map, tap, catchError } from 'rxjs/operators';
import {EMPTY} from 'rxjs';

import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';

@Injectable()
export class DeskReservationEffects {

  constructor(
    private actions$: Actions,
    private deskReservationService: DeskReservationService
  ) {}

  loadDeskReservations$ = createEffect(
    () => this.actions$.pipe(
        ofType(fromActions.loadDeskReservations),
        mergeMap( (action) => {
            console.log('received the loadDeskReservations action by effect.');
            return this.deskReservationService.getReservationList()
            .pipe(
                map(data => fromActions.loadedDeskReservations({deskReservations: data}) ),
                catchError( (err) => {
                  console.log('error while getting the desk reservation list: ' + err);
                  return EMPTY;
                })
            );
        })
    )
  );

  addDeskReservationRequest$ = createEffect(
    () => this.actions$.pipe(
        ofType(fromActions.addDeskReservationRequest),
        mergeMap( (action) => {
            console.log('received the addDeskReservationRequest action by effect.');
            return this.deskReservationService.saveReservation(action.deskReservation)
            .pipe(
                map( (data) => {
                  return fromActions.addDeskReservation({deskReservation: data});
                }),
                catchError( (err) => {
                  console.log('error while saving the desk reservation: ' + err);
                  return EMPTY;
                })
            );
        })
    )
  );

  deleteDeskReservationRequest$ = createEffect(
    () => this.actions$.pipe(
        ofType(fromActions.deleteDeskReservationRequest),
        mergeMap( (action) => {
            console.log('received the deleteDeskReservationRequest action by effect.');
            return this.deskReservationService.deleteReservation(action.deskReservation.id)
            .pipe(
                map( () => {
                  return fromActions.deleteDeskReservation({id: action.deskReservation.id});
                }),
                catchError( (err) => {
                  console.log('error while deleting the desk reservation: ' + err);
                  return EMPTY;
                })
            );
        })
    )
  );

}
