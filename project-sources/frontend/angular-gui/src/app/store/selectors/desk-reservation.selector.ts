import * as fromDeskReservation from './../reducers/desk-reservation.reducer';
import { createSelector, createFeatureSelector, props } from '@ngrx/store';


// tslint:disable-next-line:max-line-length
export const selectDeskReservationState = createFeatureSelector<fromDeskReservation.DeskReservationState>(fromDeskReservation.deskReservationsFeatureKey);

export const selectAllDeskReservationList = () => createSelector(
    selectDeskReservationState,
    fromDeskReservation.selectAll
);

export const selectAllDeskReservationLoaded = () => createSelector(
    selectDeskReservationState,
    (state: fromDeskReservation.DeskReservationState) => {
        return state.allDeskReservationLoaded;
    }
);

export const selectDeskReservationByID = () => createSelector(
    selectDeskReservationState,
    // tslint:disable-next-line:no-shadowed-variable
    (state: fromDeskReservation.DeskReservationState, props: any) => {
        return state.entities[props.id];
    }
);
