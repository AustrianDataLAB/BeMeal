import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {RegistrationComponent} from './components/registration/registration.component'
import {LoginComponent} from './components/login/login.component'
import {LeaguesComponent} from "./components/leagues/leagues.component";


const routes: Routes = [
    {path: '', component: RegistrationComponent},
    {path: 'login', component: LoginComponent},
    {path: 'leagues', component: LeaguesComponent}
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
