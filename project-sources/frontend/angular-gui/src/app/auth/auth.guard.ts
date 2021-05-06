import { AuthService } from './auth.service';
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

      // get URL which is requested/attempted when user tries to navigate to the protected page
      const requestedURL: string = state.url;

      // check if the user is already authenticated (logged in)
      // if so then returns true otherwise false
      return this.checkLogin(requestedURL);
  }

  checkLogin(requestedURL: string): boolean {
      // if user already logged in then we return true
      // otherwise we navigate the user to the login page
      if (this.authService.isUserLoggedIn()) {
        return true;
      }

      // Store the attempted URL for redirecting
      this.authService.redirectUrl = requestedURL;

      // Navigate to the login page
      this.router.navigate(['/login']);

      // The user is not logged in so return false
      return false;
  }

}
