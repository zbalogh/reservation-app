import { AuthService } from './../auth.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  message = '';
  username = '';
  password = '';

  constructor(public authService: AuthService, public router: Router) {
    // initialize the message based on the authentication state
    this.initMessage();
  }

  ngOnInit() {
  }

  initMessage() {
    if (this.authService.isUserLoggedIn()) {
        this.message = 'A kilépéshez kattints a Kijelentkezés gombra.';
    } else {
        this.message = 'A belépéshez kattints a Bejelentkezés gombra.';
    }
  }

  setMessage(message: string) {
    this.message = message;
  }

  login() {
    this.setMessage('Bejelentkezés és hitelesítés folyamatban...');

    this.authService.login(this.username, this.password).subscribe(() => {
      // clear the message
      this.setMessage('');

      // if the authentication is success then redirect the requested/attempted URL
      if (this.authService.isUserLoggedIn()) {
        // Get the redirect URL from our auth service
        // If no redirect has been set, use the default
        const redirect = this.authService.redirectUrl ? this.router.parseUrl(this.authService.redirectUrl) : '/admin/settings';

        // Redirect the user
        this.router.navigateByUrl(redirect);
      } else {
        // the authentication is failed, show error message
        this.setMessage('Helytelen felhasználónév vagy jelszó!');
      }
    });
  }

  logout() {
    this.authService.logout();
    this.initMessage();
  }

  isLoggedin() {
    return this.authService.isUserLoggedIn();
  }

}
