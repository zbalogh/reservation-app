import { DeskReservation } from '../../deskreservation/reservation.model';
import { Action, createReducer, on } from '@ngrx/store';
import { EntityState, EntityAdapter, createEntityAdapter } from '@ngrx/entity';
import * as DeskReservationActions from '../actions/desk-reservation.actions';

export const deskReservationsFeatureKey = 'deskReservations';

export interface DeskReservationState extends EntityState<DeskReservation> {
  // additional entities state properties
  allDeskReservationLoaded: boolean;
}

export function selectDeskReservationId(entity: DeskReservation): number {
  // In this case this would be optional since primary key is id
  return entity.id;
}

export function sortByDeskNumber(o1: DeskReservation, o2: DeskReservation): number {
  // sort the collection of entites by the desk number
  return o1.deskNumber - o2.deskNumber;
}

export const adapter: EntityAdapter<DeskReservation> = createEntityAdapter<DeskReservation>(
  {
    selectId: selectDeskReservationId,
    sortComparer: sortByDeskNumber
  }
);

export const initialState: DeskReservationState = adapter.getInitialState({
  // additional entity state properties
  allDeskReservationLoaded: false
});

const deskReservationReducer = createReducer(
  initialState,
  on(DeskReservationActions.addDeskReservation,
    (state, action) => adapter.addOne(action.deskReservation, state)
  ),
  on(DeskReservationActions.upsertDeskReservation,
    (state, action) => adapter.upsertOne(action.deskReservation, state)
  ),
  on(DeskReservationActions.addDeskReservations,
    (state, action) => adapter.addMany(action.deskReservations, state)
  ),
  on(DeskReservationActions.upsertDeskReservations,
    (state, action) => adapter.upsertMany(action.deskReservations, state)
  ),
  on(DeskReservationActions.updateDeskReservation,
    (state, action) => adapter.updateOne(action.deskReservation, state)
  ),
  on(DeskReservationActions.updateDeskReservations,
    (state, action) => adapter.updateMany(action.deskReservations, state)
  ),
  on(DeskReservationActions.deleteDeskReservation,
    (state, action) => adapter.removeOne(action.id, state)
  ),
  on(DeskReservationActions.deleteDeskReservations,
    (state, action) => adapter.removeMany(action.ids, state)
  ),
  on(DeskReservationActions.loadedDeskReservations,
    (state, action) => {
      return adapter.addAll(action.deskReservations, { ...state, allDeskReservationLoaded: true } );
    }
  ),
  on(DeskReservationActions.clearDeskReservations,
    state => adapter.removeAll(state)
  ),
);

export function reducer(state: DeskReservationState | undefined, action: Action) {
  return deskReservationReducer(state, action);
}

export const {
  selectIds,
  selectEntities,
  selectAll,
  selectTotal,
} = adapter.getSelectors();
