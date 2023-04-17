import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RegistrationComponent } from './components/registration/registration.component';
import { AngularMaterialModule } from './material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { FlexLayoutModule } from '@angular/flex-layout';
import { LoginComponent } from './components/login/login.component';
import {MatCardModule} from "@angular/material/card";
import { LeaguesComponent } from './components/leagues/leagues.component';
import { HomeComponent } from './components/home/home.component';
import { ProfileComponent } from './components/profile/profile.component';
import {httpInterceptorProviders} from './interceptors';


@NgModule({
  declarations: [
    AppComponent,
    RegistrationComponent,
      LoginComponent,
      LeaguesComponent,
      HomeComponent,
      ProfileComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        ReactiveFormsModule,
        HttpClientModule,
        AngularMaterialModule,
        FlexLayoutModule,
        MatCardModule
    ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }
