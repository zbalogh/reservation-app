import * as fromActions from '../actions/desk-reservation-info.actions';
import { Action, createReducer, on } from '@ngrx/store';
import { DeskReservationInfo } from 'src/app/deskreservation/reservation-info.model';


export const deskReservationInfoFeatureKey = 'deskReservationInfo';

// tslint:disable-next-line:no-empty-interface
export interface DeskReservationInfoState {
  error: any;
  reservationInfo: DeskReservationInfo;
  allDeskReservationInfoLoaded: boolean;
}

export const initialState: DeskReservationInfoState = {
    error: null,
    reservationInfo: {
      reservationList: []
    },
    allDeskReservationInfoLoaded: false
};

const deskReservationInfoReducerFn = createReducer(
  initialState,
  on(fromActions.LoadDeskReservationInfoSuccessAction, (state, action) => {
      console.log('received the LoadDeskReservationInfoSuccessAction action by reducer.');
      return { error: null, reservationInfo: action.payload, allDeskReservationInfoLoaded: true};
    }
  ),
  on(fromActions.LoadDeskReservationInfoFailureAction, (state, action) => {
      console.log('received the LoadDeskReservationInfoFailureAction action by reducer.');
      return { error: action.error};
    }
  ),
  on(fromActions.UpdateDeskReservationInfoAction, (state: DeskReservationInfoState, action) => {
      console.log('received the UpdateDeskReservationInfoAction action by reducer.');
      // update the value for the given element in the array
      const index = action.deskNumber - 1;
      const list: Array<number> = [...state.reservationInfo.reservationList];
      list[index] = action.id;
      // get the loaded flag from the state
      const loaded = state.allDeskReservationInfoLoaded;
      // create new state as return object
      const newState: DeskReservationInfoState = {
          error: null,
          reservationInfo: {
            reservationList: list
          },
          allDeskReservationInfoLoaded: loaded
      };
      return newState;
    }
  )
);

export function reducer(state: DeskReservationInfoState | undefined, action: Action) {
  return deskReservationInfoReducerFn(state, action);
}
