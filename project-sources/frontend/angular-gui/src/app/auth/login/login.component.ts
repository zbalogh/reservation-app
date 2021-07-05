import { AuthService } from './../auth.service';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  message = '';
  username = '';
  password = '';

  logoutText = '';
  loginText = '';
  authInProgress = '';
  authFailed = '';

  constructor(public authService: AuthService,
              public router: Router,
              public translate: TranslateService) {
    //
    // get the translated labels/messages
    translate.get('loginPage.text1').subscribe( str => {
        this.logoutText = str;
    });

    translate.get('loginPage.text2').subscribe( str => {
      this.loginText = str;
    });

    translate.get('loginPage.text3').subscribe( str => {
      this.authInProgress = str;
    });

    translate.get('loginPage.text4').subscribe( str => {
      this.authFailed = str;
      // initialize the message based on the authentication state
      this.initMessage();
    });
  }

  ngOnInit() {
  }

  initMessage() {
    if (this.isLoggedin()) {
        this.message =  this.logoutText;
    } else {
        this.message =  this.loginText;
    }
  }

  setMessage(message: string) {
    this.message = message;
  }

  login() {
    this.setMessage(this.authInProgress);

    this.authService.login(this.username, this.password).subscribe(() => {
      // clear the message
      this.setMessage('');

      // if the authentication is success then redirect the requested/attempted URL
      if (this.isLoggedin()) {
        // Get the redirect URL from our auth service
        // If no redirect has been set, use the default
        const redirect = this.authService.redirectUrl ? this.router.parseUrl(this.authService.redirectUrl) : '/admin/settings';

        // Redirect the user
        this.router.navigateByUrl(redirect);
      } else {
        // the authentication is failed, show error message
        this.setMessage(this.authFailed);
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
