import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { getBaseWebURL } from '../store/entity-metadata';

import { DeskReservationInfo } from './reservation-info.model';
import { DeskReservation } from './reservation.model';

@Injectable({
  providedIn: 'root'
})
export class DeskReservationService {

  constructor(private http: HttpClient) { }

  getInfo(): Observable<DeskReservationInfo> {
    return this.http.get<DeskReservationInfo>(getBaseWebURL() + '/api/data/deskreservation/getinfo');
  }

  getReservationList(): Observable<DeskReservation[]> {
    return this.http.get<DeskReservation[]>(getBaseWebURL() + '/api/data/deskreservations');
  }

  saveReservation(data: DeskReservation): Observable<DeskReservation> {
    return this.http.post<DeskReservation>(getBaseWebURL() + '/api/data/deskreservation', data);
  }

  deleteReservation(id: number) {
    return this.http.delete(getBaseWebURL() + '/api/data/deskreservation/' + id);
  }

  getReservationByIdentifier(identifier: string) : Observable<DeskReservation>
  {
    return this.http.get<DeskReservation>(getBaseWebURL() + '/api/data/deskreservation/identifier/' + identifier);
  }

  deleteReservationByIdentifier(identifier: string)
  {
    return this.http.delete(getBaseWebURL() + '/api/data/deskreservation/identifier/' + identifier);
  }

}
