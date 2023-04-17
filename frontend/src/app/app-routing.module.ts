import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {RegistrationComponent} from './components/registration/registration.component'
import {LoginComponent} from './components/login/login.component'
import {HomeComponent} from './components/home/home.component'
import {LeaguesComponent} from "./components/leagues/leagues.component";
import {AuthGuard} from "./guards/auth.guard";
import {LeagueInvitationComponent} from "./components/league-invitation/league-invitation.component";
import {CreateLeagueComponent} from "./components/create-league/create-league.component";


const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'register', component: RegistrationComponent},
    {path: 'login', component: LoginComponent},
    {path: 'leagues',  canActivate: [AuthGuard], component: LeaguesComponent},
    {path: 'league/join/:secret',  canActivate: [AuthGuard], component: LeagueInvitationComponent},
    {path: 'create-league',  canActivate: [AuthGuard], component: CreateLeagueComponent}
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
