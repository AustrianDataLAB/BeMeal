import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {LeagueService} from "../../services/league.service";
import {catchError, tap} from "rxjs/operators";
import {async, firstValueFrom, map, Observable, of} from 'rxjs';
import {League} from "../../dtos/league";
import {InvitationService} from '../../services/invitation.service';
import { Clipboard } from '@angular/cdk/clipboard';

@Component({
  selector: 'app-leagues',
  templateUrl: './leagues.component.html',
  styleUrls: ['./leagues.component.scss']
})
export class LeaguesComponent {

    error = false;
    errorMessage = '';
    showInvitationLinks: Array<boolean>;
    InvitationLinksFeedback: Array<string>;
    enableInviteFriends: Array<boolean>;
    leagues: League[] = [];
    invitationLinks = new Map<number|null,Promise<string|null>>;

    constructor(private clipboard: Clipboard, private router: Router, private leagueService: LeagueService, private invitationService: InvitationService) {
        this.fetchLeagues();
    }

    createLeague() {
        this.router.navigate(['/create-league']);
    }

    viewLeague(id: number | null) {
        if (id !== null) this.router.navigate([`/league/${id}`]);
    }

    fetchLeagues() {
        this.leagueService.fetchLeagues().pipe(
            tap(response => {
                this.leagues = response;
                console.log(this.leagues);
                console.log('Successfully fetched leagues');
                this.showInvitationLinks = new Array<boolean>(this.leagues.length).fill(false); // initialize boolean array with all false values and same size as leagues
                this.enableInviteFriends = new Array<boolean>(this.leagues.length).fill(true); // initialize boolean array with all false values and same size as leagues
                this.InvitationLinksFeedback = new Array<string>(this.leagues.length).fill("");
                let index = 0;
                for (const l of this.leagues) {
                    const link = firstValueFrom(
                        this.invitationService.getHiddenIdentifier(l.id!, false).pipe(
                                map(value => `${location.origin}/league/join/${value.hiddenIdentifier}`),
                                catchError(error => {
                                    this.enableInviteFriends[index] = false;
                                    index++;
                                    console.log("couldnt get hidden identifier");
                                    return of(null);
                                })
                            )
                    );
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

    // getInvitationLink(id: number): Observable<string> {
    //     return this.invitationService.getHiddenIdentifier(id, false).pipe(map(value => `${location.origin}/league/join/${value.hiddenIdentifier}`));
    // }

    /**
     * Takes in a string and makes it presentable to the frontend. Removes camelcase and uppercases
     * @param str
     */
    prettyString(str: string): string {
        str = str.replace(/_/g, ' ').toLowerCase();
        return str.replace(/(^|\s)\S/g, (match) => match.toUpperCase());
    }

    showHiddenIdentifier(index: number, leagueId: number|null) {

        const link = firstValueFrom(
            this.invitationService.getHiddenIdentifier(leagueId!, true).pipe(
                map(value => `${location.origin}/league/join/${value.hiddenIdentifier}`),
                catchError(error => {
                    this.enableInviteFriends[index] = false;
                    index++;
                    console.log("couldnt get hidden identifier");
                    return of(null);
                })
            )
        );
        this.invitationLinks.set(leagueId, link);

        this.showInvitationLinks[index] = true;
        // reset all fields
        for (let i = 0; i < this.InvitationLinksFeedback.length; i++) {
            this.InvitationLinksFeedback[i] = "";
        }
        // copy to clipboard
        this.invitationLinks.get(leagueId)?.then(
            success => {
                if (success !== null) {
                    this.clipboard.copy(success);
                }
                // copy link to clipboard and show feedback
                this.InvitationLinksFeedback[index] = "Invitation copied to Clipboard!"
                console.log(success);
            },
            error => {
                console.log(error);
                this.InvitationLinksFeedback[index] = "Error: could not get invitation!"
                return of(null);
            }
        )
    }
}
