import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {RegistrationComponent} from './components/registration/registration.component'
import {LoginComponent} from './components/login/login.component'
import {HomeComponent} from './components/home/home.component'
import {LeaguesComponent} from "./components/leagues/leagues.component";
import {ProfileComponent} from "./components/profile/profile.component";
import {AuthGuard} from "./guards/auth.guard";


const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'register', component: RegistrationComponent},
    {path: 'login', component: LoginComponent},
    {path: 'leagues', component: LeaguesComponent},
    {path: 'profile',  canActivate: [AuthGuard], component: ProfileComponent}
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
