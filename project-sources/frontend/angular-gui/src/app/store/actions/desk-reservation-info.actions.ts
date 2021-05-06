import { createAction, props } from '@ngrx/store';
import { DeskReservationInfo } from 'src/app/deskreservation/reservation-info.model';

export enum DeskReservationInfoActionTypes {
  loadDeskReservationInfoAction = '[DeskReservationInfo] LoadDeskReservationInfo',
  loadDeskReservationInfoSuccessAction = '[DeskReservationInfo] LoadDeskReservationInfoSuccess',
  loadDeskReservationInfoFailureAction = '[DeskReservationInfo] LoadDeskReservationInfoFailure',
  updateDeskReservationInfoAction = '[DeskReservationInfo] UpdateDeskReservationInfo'
}

export const LoadDeskReservationInfoAction = createAction(
  DeskReservationInfoActionTypes.loadDeskReservationInfoAction
);

export const LoadDeskReservationInfoSuccessAction = createAction(
  DeskReservationInfoActionTypes.loadDeskReservationInfoSuccessAction,
  props<{ payload: DeskReservationInfo }>()
);

export const LoadDeskReservationInfoFailureAction = createAction(
  DeskReservationInfoActionTypes.loadDeskReservationInfoFailureAction,
  props<{ error: any }>()
);

export const UpdateDeskReservationInfoAction = createAction(
  DeskReservationInfoActionTypes.updateDeskReservationInfoAction,
  props<{ deskNumber: number, id: number }>()
);
