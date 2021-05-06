export interface DeskReservation {

    id: number;

    deskNumber: number;

    reservationAt: Date;

    status: number;

    firstname: string;

    lastname: string;

    email: string;

    telephone: string;

}

export function compareDeskReservation(c1: DeskReservation, c2: DeskReservation) {
    const compare = c1.deskNumber - c2.deskNumber;
    if (compare > 0) {
      return 1;
    } else if ( compare < 0) {
      return -1;
    } else {
        return 0;
    }
}
