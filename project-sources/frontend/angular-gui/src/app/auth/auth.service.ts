import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private isLoggedIn = false;

  constructor() {
    // read the 'loggedIn' flag from the local storage
    this.isLoggedIn = localStorage.getItem('app-auth-isLoggedIn') === 'true' ? true : false;
  }

  // store the requested/attempted URL so we can redirect after successful logging in
  redirectUrl: string;

  /**
   * Authenticate the user with the given username and password.
   */
  login(username: string, password: string): Observable<boolean> {
      // it holds the authentication result
      let result = false;

      if (username === 'admin' && password === '12345') {
        // the entered user and pwd are correct
        result = true;
      }

      // set the 'loggedIn' flag and save it in the local storage.
      // return a Boolean (result) as Observable
      return of(result).pipe(
        delay(1000),  // simulate 1 second delay...
        tap(val => {
            // set the 'loggedIn' flag and save it in the browser local storage
            this.isLoggedIn = result;
            localStorage.setItem('app-auth-isLoggedIn', this.isLoggedIn ? 'true' : 'false');
          }
        )
      );
  }

  logout(): void {
    this.isLoggedIn = false;
    localStorage.removeItem('app-auth-isLoggedIn');
  }

  isUserLoggedIn(): boolean {
    return localStorage.getItem('app-auth-isLoggedIn') === 'true' ? true : false;
  }

}
