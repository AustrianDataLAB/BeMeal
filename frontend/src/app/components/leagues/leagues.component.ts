import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {LeagueService} from "../../services/league.service";
import {catchError, tap} from "rxjs/operators";
import {map, Observable, of} from 'rxjs';
import {League} from "../../dtos/league";
import {InvitationService} from '../../services/invitation.service';

@Component({
  selector: 'app-leagues',
  templateUrl: './leagues.component.html',
  styleUrls: ['./leagues.component.scss']
})
export class LeaguesComponent {

    error = false;
    errorMessage = '';

    leagues: League[];
    displayedColumns: string[] = ['Name', 'gameMode', 'challengeDuration', 'region', 'action', 'invitationLink']

    constructor(private router: Router, private leagueService: LeagueService, private invitationService: InvitationService) {
        this.fetchLeagues();
    }

    createLeague() {
        this.router.navigate(['/create-league']);
    }
    fetchLeagues() {
        this.leagueService.fetchLeagues().pipe(
            tap(response => {
                this.leagues = response;
                console.log(this.leagues);
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

    getInvitationLink(id: number): Observable<string> {
        return this.invitationService.getHiddenIdentifier(id).pipe(map(value => `${location.origin}/league/join/${value.hiddenIdentifier}`));
    }
}
