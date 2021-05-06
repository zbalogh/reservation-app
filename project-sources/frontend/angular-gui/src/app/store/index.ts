import {
  ActionReducerMap,
  MetaReducer
} from '@ngrx/store';
import { environment } from '../../environments/environment';


// tslint:disable-next-line:no-empty-interface
export interface AppState {
}

export const reducers: ActionReducerMap<AppState> = {

};


export const metaReducers: MetaReducer<AppState>[] = !environment.production ? [] : [];
