import { EntityMetadataMap, DefaultDataServiceConfig } from '@ngrx/data';
import { environment } from '../../environments/environment';
import { compareDeskReservation } from '../deskreservation/reservation.model';

export const defaultEntityDispatcherOptions = {
  optimisticAdd: false,
  optimisticDelete: false,
  optimisticUpdate: false,
  optimisticUpsert: false
};

const entityMetadata: EntityMetadataMap = {
    DeskReservation: {
      sortComparer: compareDeskReservation,
      entityDispatcherOptions: defaultEntityDispatcherOptions
    },
};

export const entityConfig = {
  entityMetadata
};

export const defaultDataServiceConfig: DefaultDataServiceConfig = {
    root: getBaseWebURL() + '/api/data',
    timeout: 3000, // request timeout
};

export function getBaseWebURL(): string {
  let baseURL='';
  const protocol = location.protocol;
  const port = location.port;
  const hostname = location.hostname;

  /*
  if (environment.production) {
      // in production build we use the default port values which is used for the real API server/backend
      if (protocol.startsWith('https')) {
        port = '8443';
      } else {
        port = '8080';
      }
  }
  */

  if (protocol.startsWith('https')) {
    baseURL = 'https://' + hostname + ':' + port;
  } else {
    baseURL = 'http://' + hostname + ':' + port;
  }

  return baseURL;
}
