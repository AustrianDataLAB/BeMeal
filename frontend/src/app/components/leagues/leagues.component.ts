import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {FormBuilder, Validators} from "@angular/forms";
import {LeagueService} from "../../services/league.service";
import {catchError, tap} from "rxjs/operators";
import {of} from "rxjs";

@Component({
  selector: 'app-leagues',
  templateUrl: './leagues.component.html',
  styleUrls: ['./leagues.component.scss']
})
export class LeaguesComponent {

    error = false;
    errorMessage = '';

    constructor(private router: Router, private leagueService: LeagueService) {
        this.fetchLeagues();
    }

    fetchLeagues() {
        this.leagueService.fetchLeagues().pipe(
            tap(response => {
                console.log(response);
                console.log('Successfully fetched leagues');
                this.router.navigate(['/leagues']);
            }),
            catchError(error => {
                console.error('Error while creating a leauge:', error);
                this.errorMessage = "Could not create the league";
                this.error = true;
                // Handle the error here
                return of(null);
            })
        ).subscribe();
    }

    vanishError() {
        this.error = false;
    }
}
