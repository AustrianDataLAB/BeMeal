import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {RegistrationComponent} from './components/registration/registration.component'
import {LoginComponent} from './components/login/login.component'
import {HomeComponent} from './components/home/home.component'
import {LeaguesComponent} from "./components/leagues/leagues.component";
import {AuthGuard} from "./guards/auth.guard";
import {PasswordResetComponent} from './components/login/password-reset/password-reset.component';


const routes: Routes = [
    {path: '', component: HomeComponent},
    {path: 'register', component: RegistrationComponent},
    {path: 'login', component: LoginComponent},
    {path: 'leagues',  canActivate: [AuthGuard], component: LeaguesComponent},
    {path: 'password-reset', component: PasswordResetComponent},
    {path: 'password-reset/:token', component: PasswordResetComponent}
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
