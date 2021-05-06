import * as fromActions from '../actions/desk-reservation-info.actions';
import { DeskReservationService } from '../../deskreservation/deskreservation.service';

import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { map, mergeMap, switchMap, catchError } from 'rxjs/operators';
import { EMPTY, of } from 'rxjs';

/*
https://stackblitz.com/edit/ngrx-effects-example
 */

@Injectable()
export class DeskReservationInfoEffects {

  constructor(
      private actions$: Actions,
      private deskReservationService: DeskReservationService
  ) {}

  getDeskReservationInfo$ = createEffect(
    () => this.actions$.pipe(
      ofType(fromActions.LoadDeskReservationInfoAction),
      mergeMap(action => {
        console.log('received the LoadDeskReservationInfoAction action by effect.');
        return this.deskReservationService.getInfo()
        .pipe(
          map(reservationInfo => ( fromActions.LoadDeskReservationInfoSuccessAction({payload: reservationInfo}) ) ),
          catchError( err => of(fromActions.LoadDeskReservationInfoFailureAction({error: err})) )
        );
      })
    )
  );

}
