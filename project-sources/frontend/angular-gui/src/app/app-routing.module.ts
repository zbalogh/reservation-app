import { LoginComponent } from './auth/login/login.component';
import { AuthGuard } from './auth/auth.guard';
import { DeskreservationEditorComponent } from './admin/deskreservation-editor/deskreservation-editor.component';
import { DeskreservationFormComponent } from './deskreservation/deskreservation-form/deskreservation-form.component';
import { DeskreservationComponent } from './deskreservation/deskreservation.component';
import { AdminSettingsComponent } from './admin/settings/settings.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { HomeComponent } from './home/home.component';

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';


const routes: Routes = [
  // admin settings page
  {
    path: 'admin/settings',
    component: AdminSettingsComponent,
    canActivate: [AuthGuard],
  },

  // desk reservation editor for administrator
  {
    path: 'deskreservation-editor',
    component: DeskreservationEditorComponent,
    canActivate: [AuthGuard],
  },

  {
    path: 'login',
    component: LoginComponent
  },

  // desk reservation
  {
    path: 'deskreservation',
    component: DeskreservationComponent
  },

  // desk reservation form
  {
    path: 'deskreservation-form',
    component: DeskreservationFormComponent
  },

  // home component the default when open browser without path
  {
    path: '',
    component: HomeComponent
  },

  // last route entry for case when no matching, then we display 'page not found' component
  {
    path: '**',
    component: PageNotFoundComponent
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { useHash: true })
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule { }
