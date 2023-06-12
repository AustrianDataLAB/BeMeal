import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RegistrationComponent} from './components/registration/registration.component';
import {LoginComponent} from './components/login/login.component';
import {HomeComponent} from './components/home/home.component';
import {LeaguesComponent} from './components/leagues/leagues.component';
import {ProfileComponent} from './components/profile/profile.component';
import {AuthGuard} from './guards/auth.guard';
import {LeagueInvitationComponent} from './components/league-invitation/league-invitation.component';
import {CreateLeagueComponent} from './components/create-league/create-league.component';
import {ShowLeagueComponent} from './components/show-league/show-league.component';
import {ChallengeComponent} from './components/challenge/challenge.component';
import {AnalyticsComponent} from './components/analytics/analytics.component';
import {MealsComponent} from "./components/meals/meals.component";
import {SearchComponent} from "./components/search/search.component";
import {PasswordResetComponent} from './components/login/password-reset/password-reset.component';


const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'register', component: RegistrationComponent},
    {path: 'login', component: LoginComponent},
    {path: 'profile', canActivate: [AuthGuard], component: ProfileComponent},
    {path: 'leagues', canActivate: [AuthGuard], component: LeaguesComponent},
    {path: 'league/:id', canActivate: [AuthGuard], component: ShowLeagueComponent},
    {path: 'league/:id/challenge', canActivate: [AuthGuard], component: ChallengeComponent},
    {path: 'league/join/:hiddenIdentifier', canActivate: [AuthGuard], component: LeagueInvitationComponent},
    {path: 'create-league', canActivate: [AuthGuard], component: CreateLeagueComponent},
    {path: 'analytics', canActivate: [AuthGuard], component: AnalyticsComponent},
    {path: 'meals',  canActivate: [AuthGuard], component: MealsComponent},
    {path: 'search', canActivate: [AuthGuard], component: SearchComponent},
    {path: 'password-reset', component: PasswordResetComponent},
    {path: 'password-reset/:token', component: PasswordResetComponent}
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
