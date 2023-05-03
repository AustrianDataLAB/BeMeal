import {Component, OnInit} from '@angular/core';
import {SelfService} from "../../services/self.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LeagueService} from "../../services/league.service";
import {catchError, tap} from "rxjs/operators";
import {of} from "rxjs";
import {League} from "../../dtos/league";
import {Registration} from "../../dtos/registration";
import {JoinLeague} from "../../dtos/join-league";

@Component({
  selector: 'app-league-invitation',
  templateUrl: './league-invitation.component.html',
  styleUrls: ['./league-invitation.component.scss']
})
export class LeagueInvitationComponent implements OnInit{

    error = false;
    errorMessage: string;
    username: string;
    hiddenIdentifier: string | null;
    league: League;
    leagueValid = false;
    leagueJoinSuccess = false;
    constructor(private selfService: SelfService, private router: Router, private leagueService: LeagueService, private route: ActivatedRoute) {

    }

    ngOnInit() {
        this.getUsername();
        this.getLeagueName();
    }

    /**
     * Displays the username in the headline
     */
    getUsername() {
        this.username = "";
        this.selfService.getProfile().pipe(
            tap(response => {
                this.username = response.username;
            })).subscribe();

    }
    joinLeague() {
        const obj: JoinLeague = new JoinLeague(
            this.league.id,
            this.hiddenIdentifier
        );
        this.leagueService.joinLeague(obj).pipe(
            tap(response => {
                console.log(response);
                console.log('Successful joined league');
                this.leagueJoinSuccess = true;
            }),
            catchError(error => {
                console.error('Error while joining league:', error);
                this.error = true;
                this.errorMessage = "Error: " + error.error.message;
                // Handle the    error here
                return of(null);
            })
        ).subscribe();
    }

    getLeagueName() {
        this.hiddenIdentifier = this.getHiddenIdentifierFromUrl();
        if (this.hiddenIdentifier == null) {
            // todo display error
            return;
        }
        this.leagueService.getLeagueByHiddenIdentifier(this.hiddenIdentifier).pipe(
            tap(response => {
                if (response == null) {
                    // error
                    this.leagueValid = false;
                    console.log("could not get league for hidden identifier");
                    return;
                }
                this.league = response;
                this.leagueValid = true;
                console.log(this.league);
                console.log('Successfully fetched league');
            }),
            catchError(error => {
                console.error('Error ', error);
                this.error = true;
                this.errorMessage = "Error: could not fetch league";
                return of(null);
            })
        ).subscribe();
    }


    getHiddenIdentifierFromUrl(): string | null {
        const hi = this.route.snapshot.paramMap.get('hiddenIdentifier');
        console.log(`hidden identifier from url is: ${hi}`);
        return hi;
    }

    doNotJoin() {
        this.router.navigate(['/leagues'])
    }
}
