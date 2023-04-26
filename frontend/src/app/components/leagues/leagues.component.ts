import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {LeagueService} from "../../services/league.service";
import {catchError, tap} from "rxjs/operators";
import {firstValueFrom, map, Observable, of} from 'rxjs';
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
    showLink = false;

    leagues: League[];
    displayedColumns: string[] = ['Name', 'gameMode', 'challengeDuration', 'region', 'action', 'invitationLink']
    invitationLinks = new Map<number|null,Promise<string>>;

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

    getInvitationLink(id: number): Observable<string> {
        return this.invitationService.getHiddenIdentifier(id).pipe(map(value => `${location.origin}/league/join/${value.hiddenIdentifier}`));
    }

    /**
     * Takes in a string and makes it presentable to the frontend. Removes camelcase and uppercases
     * @param str
     */
    prettyString(str: string): string {
        str = str.replace(/_/g, ' ').toLowerCase();
        return str.replace(/(^|\s)\S/g, (match) => match.toUpperCase());
    }
}
