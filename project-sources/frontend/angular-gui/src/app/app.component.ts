import { AuthService } from './auth/auth.service';
import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  title = 'Zbalogh Spring/Angular Demo';

  constructor(private authService: AuthService) {}

  isLoggedin() {
    return this.authService.isUserLoggedIn();
  }

}
