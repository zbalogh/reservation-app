import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, delay, mapTo, tap } from 'rxjs/operators';
import { getBaseWebURL } from '../store/entity-metadata';
import { User } from './user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private isLoggedIn = false;

  private readonly JWT_TOKEN = 'RESERVATION_APP_JWT_TOKEN';

  private readonly USER_LOGGED_IN = 'RESERVATION_APP_USER_LOGGED_IN';

  constructor(private http: HttpClient) {
    // read the 'loggedIn' flag from the local storage
    // this.isLoggedIn = this.isUserLoggedIn();
  }

  // store the requested/attempted URL so we can redirect after successful logging in
  redirectUrl: string;

  /*
  loginOld(username: string, password: string): Observable<boolean> {
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
            this.setLoggedInFlag(this.isLoggedIn);
          }
        )
      );
  }
  */

  /**
   * Authenticate the user with the given username and password.
   */
  login(username: string, password: string): Observable<boolean>
  {
    return this.http.post<User>(getBaseWebURL() + '/api/auth/account/login', { username, password })
      .pipe(
        tap(user => {
          this.isLoggedIn = true;
          this.setLoggedInFlag(this.isLoggedIn);
          this.storeJwtToken(user.token);
        }),
        mapTo(true),
        catchError(error => {
          return of(false);
        })
      );
  }

  logout(): void
  {
    this.isLoggedIn = false;
    this.removeLoggedInFlag();
    this.removeJwtToken();
  }

  isUserLoggedIn(): boolean
  {
    return localStorage.getItem(this.USER_LOGGED_IN) === 'true' ? true : false;
  }

  private setLoggedInFlag(isLoggedIn: boolean): void
  {
    localStorage.setItem(this.USER_LOGGED_IN, isLoggedIn ? 'true' : 'false');
  }

  private removeLoggedInFlag(): void
  {
    localStorage.removeItem(this.USER_LOGGED_IN);
  }

  getJwtToken() {
    return localStorage.getItem(this.JWT_TOKEN);
  }

  private storeJwtToken(jwt: string) {
    localStorage.setItem(this.JWT_TOKEN, jwt);
  }

  private removeJwtToken() {
    localStorage.removeItem(this.JWT_TOKEN);
  }

}
