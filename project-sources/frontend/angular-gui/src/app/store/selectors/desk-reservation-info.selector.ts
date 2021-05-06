import { DeskReservationInfoState } from '../reducers/desk-reservation-info.reducer';
import { deskReservationInfoFeatureKey } from '../reducers/desk-reservation-info.reducer';
import { createFeatureSelector, createSelector } from '@ngrx/store';


// ------------------------- states -------------------------

// tslint:disable-next-line:max-line-length
export const selectDeskReservationInfoState = createFeatureSelector<DeskReservationInfoState>(deskReservationInfoFeatureKey);

export const selectDeskReservationInfo = () => createSelector(
    selectDeskReservationInfoState,
    deskReservationState => deskReservationState.reservationInfo
);

export const selectAllDeskReservationList = () => createSelector(
    selectDeskReservationInfo(),
    info => info.reservationList
);

export const selectAllDeskReservationInfoLoaded = () => createSelector(
    selectDeskReservationInfoState,
    state => state.allDeskReservationInfoLoaded
);

export const selectNumberOfAllDesks = () => createSelector(
    selectAllDeskReservationList(),
    reservationList => reservationList.length
);

export const selectNumberOfReservedDesks = () => createSelector(
    selectAllDeskReservationList(),
    (reservationList) => {
        return reservationList.filter( itemValue => itemValue !== 0).length;
    }
);

export const selectNumberOfFreeDesks = () => createSelector(
    selectAllDeskReservationList(),
    (reservationList) => {
        return reservationList.filter( itemValue => itemValue === 0).length;
    }
);


export const selectReservedDeskList = () => createSelector(
    selectAllDeskReservationList(),
    (reservationList) => {
        return reservationList.filter( itemValue => itemValue !== 0);
    }
);

export const selectFreeDeskList = () => createSelector(
    selectAllDeskReservationList(),
    (reservationList) => {
        return reservationList.filter( itemValue => itemValue === 0);
    }
);
