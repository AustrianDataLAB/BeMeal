import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {LeagueService} from "../../services/league.service";
import {catchError, tap} from "rxjs/operators";
import {firstValueFrom, map, of} from 'rxjs';
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
    invitationLinks = new Map<number,Promise<string>>;

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
                for (const l of this.leagues) {
                    const link = firstValueFrom(this.invitationService.getHiddenIdentifier(l.id!).pipe(map(value => `${location.origin}/league/join/${value.hiddenIdentifier}`)));
                    this.invitationLinks.set(l.id!, link);
                }
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
