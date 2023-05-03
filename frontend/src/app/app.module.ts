import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS} from '@angular/common/http';
import {AuthInterceptor} from "./interceptors/auth.interceptor";
import {httpInterceptorProviders} from './interceptors';
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
import { CreateLeagueComponent } from './components/create-league/create-league.component';
import { HomeComponent } from './components/home/home.component';
import { LeagueInvitationComponent } from './components/league-invitation/league-invitation.component';
import { HeaderComponent } from './components/shared/header/header.component';
import { ProfileComponent } from './components/profile/profile.component';
import { ChallengeComponent } from './components/challenge/challenge.component';
import { ShowLeagueComponent } from './components/show-league/show-league.component';
import { DragDropModule } from '@angular/cdk/drag-drop';


@NgModule({
  declarations: [
    AppComponent,
    RegistrationComponent,
      LoginComponent,
      LeaguesComponent,
      ProfileComponent,
      CreateLeagueComponent,
      LeaguesComponent,
      HomeComponent,
      LeagueInvitationComponent,
      HeaderComponent,
      ChallengeComponent,
      ShowLeagueComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        ReactiveFormsModule,
        HttpClientModule,
        AngularMaterialModule,
        FlexLayoutModule,
        MatCardModule,
        DragDropModule
    ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule { }
