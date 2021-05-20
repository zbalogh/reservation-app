import { entityConfig, defaultDataServiceConfig } from './store/entity-metadata';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { AdminSettingsComponent } from './admin/settings/settings.component';
import { DeskreservationComponent } from './deskreservation/deskreservation.component';
import { DeskreservationFormComponent } from './deskreservation/deskreservation-form/deskreservation-form.component';
import { StoreModule } from '@ngrx/store';
import { reducers, metaReducers } from './store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';
import * as fromDeskReservationInfoReducer from './store/reducers/desk-reservation-info.reducer';
import { EffectsModule } from '@ngrx/effects';
import { DefaultDataServiceConfig, EntityDataModule, EntityDefinitionService } from '@ngrx/data';
import { DeskReservationInfoEffects } from './store/effects/desk-reservation-info.effects';
import * as fromDeskReservation from './store/reducers/desk-reservation.reducer';
import { DeskReservationEffects } from './store/effects/desk-reservation.effects';
import { ManageDeskreservationComponent } from './admin/manage-deskreservation/manage-deskreservation.component';
import { DeskreservationEditorComponent } from './admin/deskreservation-editor/deskreservation-editor.component';
import { ModalModule } from 'ngx-bootstrap/modal';
import { LoginComponent } from './auth/login/login.component';
import { JwtInterceptor } from './auth/jwt.interceptor';
import { ErrorInterceptor } from './auth/error.interceptor';


@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    PageNotFoundComponent,
    AdminSettingsComponent,
    DeskreservationComponent,
    DeskreservationFormComponent,
    ManageDeskreservationComponent,
    DeskreservationEditorComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
    ModalModule.forRoot(),
    AppRoutingModule,

    // register the root state
    StoreModule.forRoot(reducers, {
      metaReducers,
      runtimeChecks: {
        strictStateImmutability: true,
        strictActionImmutability: true,
      }
    }),

    // register the feature states
    StoreModule.forFeature(fromDeskReservationInfoReducer.deskReservationInfoFeatureKey, fromDeskReservationInfoReducer.reducer),
    StoreModule.forFeature(fromDeskReservation.deskReservationsFeatureKey, fromDeskReservation.reducer),

    // register the root effect
    EffectsModule.forRoot([]),

    // register the NGRX Data module
    EntityDataModule.forRoot(entityConfig),

    // register the feature effects
    EffectsModule.forFeature([
      DeskReservationInfoEffects,
      DeskReservationEffects
    ]),

    !environment.production ? StoreDevtoolsModule.instrument() : []
  ],
  providers: [
    { provide: DefaultDataServiceConfig, useValue: defaultDataServiceConfig },
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

}
