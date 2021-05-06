import { DeskReservation } from './../../deskreservation/reservation.model';
import { Injectable } from '@angular/core';
import { EntityCollectionServiceBase, EntityCollectionServiceElementsFactory } from '@ngrx/data';

@Injectable({
    providedIn: 'root'
})
export class DeskReservationEntityService extends EntityCollectionServiceBase<DeskReservation> {

    constructor(serviceElementsFactory: EntityCollectionServiceElementsFactory) {
        super('DeskReservation', serviceElementsFactory);
    }

}
