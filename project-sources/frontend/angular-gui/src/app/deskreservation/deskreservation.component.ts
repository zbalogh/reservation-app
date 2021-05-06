import * as fromSelectors from '../store/selectors/desk-reservation-info.selector';
import * as fromActions from '../store/actions/desk-reservation-info.actions';
import { DeskReservationInfoState } from '../store/reducers/desk-reservation-info.reducer';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { Store, select } from '@ngrx/store';
import { DeskReservationInfo } from './reservation-info.model';

@Component({
  selector: 'app-deskreservation',
  templateUrl: './deskreservation.component.html',
  styleUrls: ['./deskreservation.component.scss']
})
export class DeskreservationComponent implements OnInit, OnDestroy {

 numberOfAllDesks$: Observable<number>;
 numberOfReservedDesks$: Observable<number>;
 numberOfFreeDesks$: Observable<number>;

  // we use observable for the data coming from the service
  reservationInfo$: Observable<DeskReservationInfo>;

  reservationInfoLoaded$: Observable<boolean>;
  reservationInfoLoadedSubscription: Subscription;

  constructor(
      private store: Store<DeskReservationInfoState>
    ) {
      this.loadData();
    }

  loadData() {
    // if reservation list is not loaded yet then we load it
    this.reservationInfoLoaded$ = this.store.pipe( select(fromSelectors.selectAllDeskReservationInfoLoaded()) );

    // subscribe for this reservation
    this.reservationInfoLoadedSubscription = this.reservationInfoLoaded$.subscribe(
      (loaded) => {
          if (!loaded) {
              // send action to load reservation info data from the server.
              // An effect will be triggered by this action.
              // When load is done then effect will dispatch an action which will be caught by the reducer.
              // The reducer put the data into the "deskreservation-info" feature store.
              console.log('dispatching the LoadDeskReservationInfoAction action');
              this.store.dispatch(fromActions.LoadDeskReservationInfoAction());
          }
      }
    );
  }

  ngOnInit() {
    this.reservationInfo$ = this.store.pipe( select(fromSelectors.selectDeskReservationInfo()) );
    console.log('desk reservation info selected from the store.');

    this.numberOfAllDesks$ = this.store.pipe( select(fromSelectors.selectNumberOfAllDesks()) );
    this.numberOfFreeDesks$ = this.store.pipe( select(fromSelectors.selectNumberOfFreeDesks()) );
    this.numberOfReservedDesks$ = this.store.pipe( select(fromSelectors.selectNumberOfReservedDesks()) );
  }

  ngOnDestroy() {
    // unsubscribe from the reservation loaded observable
    this.reservationInfoLoadedSubscription.unsubscribe();
  }

}
