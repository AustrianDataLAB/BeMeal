import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {HttpClientModule} from '@angular/common/http';
import {httpInterceptorProviders} from './interceptors';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {RegistrationComponent} from './components/registration/registration.component';
import {AngularMaterialModule} from './material.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {FlexLayoutModule} from '@angular/flex-layout';
import {LoginComponent} from './components/login/login.component';
import {MatCardModule} from "@angular/material/card";
import {LeaguesComponent} from './components/leagues/leagues.component';
import {CreateLeagueComponent} from './components/create-league/create-league.component';
import {HomeComponent} from './components/home/home.component';
import {LeagueInvitationComponent} from './components/league-invitation/league-invitation.component';
import {HeaderComponent} from './components/shared/header/header.component';
import {ProfileComponent} from './components/profile/profile.component';
import {ChallengeComponent} from './components/challenge/challenge.component';
import {ShowLeagueComponent} from './components/show-league/show-league.component';
import {DragDropModule} from '@angular/cdk/drag-drop';
import {DarkModeComponent} from "./components/shared/dark-mode/dark-mode.component";
import {MealsComponent} from "./components/meals/meals.component";
import {NgxPaginationModule} from "ngx-pagination";
import {AnalyticsComponent} from './components/analytics/analytics.component';
import {HeatMapComponent} from './components/analytics/heat-map/heat-map.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {SearchComponent} from "./components/search/search.component";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatSliderModule} from "@angular/material/slider";
import {NgImageSliderModule} from 'ng-image-slider';
import {PasswordResetComponent} from './components/login/password-reset/password-reset.component';
import { SuggestionsComponent } from './components/suggestions/suggestions.component';
import {MatExpansionModule} from "@angular/material/expansion";

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
        ShowLeagueComponent,
        DarkModeComponent,
        MealsComponent,
        AnalyticsComponent,
        HeatMapComponent,
        SearchComponent,
        PasswordResetComponent,
        SuggestionsComponent
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
        DragDropModule,
        FormsModule,
        NgxPaginationModule,
        MatSlideToggleModule,
        MatSliderModule,
        MatProgressSpinnerModule,
        MatExpansionModule,
        NgImageSliderModule
    ],
    providers: [httpInterceptorProviders],
    bootstrap: [AppComponent]
})
export class AppModule {
}
