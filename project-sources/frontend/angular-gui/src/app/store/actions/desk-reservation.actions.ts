import { DeskReservation } from '../../deskreservation/reservation.model';
import { createAction, props } from '@ngrx/store';
import { Update } from '@ngrx/entity';

export const loadDeskReservations = createAction(
  '[DeskReservation/API] Load DeskReservations'
);

export const loadedDeskReservations = createAction(
  '[DeskReservation/API] Loaded DeskReservations',
  props<{ deskReservations: DeskReservation[] }>()
);

export const addDeskReservation = createAction(
  '[DeskReservation/API] Add DeskReservation',
  props<{ deskReservation: DeskReservation }>()
);

export const addDeskReservationRequest = createAction(
  '[DeskReservation/API] Add DeskReservation Request',
  props<{ deskReservation: DeskReservation }>()
);

export const upsertDeskReservation = createAction(
  '[DeskReservation/API] Upsert DeskReservation',
  props<{ deskReservation: DeskReservation }>()
);

export const addDeskReservations = createAction(
  '[DeskReservation/API] Add DeskReservations',
  props<{ deskReservations: DeskReservation[] }>()
);

export const upsertDeskReservations = createAction(
  '[DeskReservation/API] Upsert DeskReservations',
  props<{ deskReservations: DeskReservation[] }>()
);

export const updateDeskReservation = createAction(
  '[DeskReservation/API] Update DeskReservation',
  props<{ deskReservation: Update<DeskReservation> }>()
);

export const updateDeskReservations = createAction(
  '[DeskReservation/API] Update DeskReservations',
  props<{ deskReservations: Update<DeskReservation>[] }>()
);

export const deleteDeskReservation = createAction(
  '[DeskReservation/API] Delete DeskReservation',
  props<{ id: number }>()
);

export const deleteDeskReservationRequest = createAction(
  '[DeskReservation/API] Delete DeskReservation Request',
  props<{ deskReservation: DeskReservation }>()
);

export const deleteDeskReservations = createAction(
  '[DeskReservation/API] Delete DeskReservations',
  props<{ ids: number[] }>()
);

export const clearDeskReservations = createAction(
  '[DeskReservation/API] Clear DeskReservations'
);
