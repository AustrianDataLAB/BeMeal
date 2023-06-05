import {Component, NgModule, OnInit} from '@angular/core';
import {SelfService} from "../../services/self.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LeagueService} from "../../services/league.service";
import {catchError, tap} from "rxjs/operators";
import {firstValueFrom, map, of, switchMap} from "rxjs";
import {League} from "../../dtos/league";
import {LeaderboardUser} from "../../dtos/leaderboard-user";
import {WinningSubmissionDisplay} from "../../dtos/winning-submission-display";
import {IvyCarouselModule} from 'angular-responsive-carousel';


@Component({
  selector: 'app-show-league',
  templateUrl: './show-league.component.html',
  styleUrls: ['./show-league.component.scss']
})
export class ShowLeagueComponent implements OnInit{

    leagueId: number | null;
    league: League;
    error: boolean;
    errorMessage: string;
    leaderboardUsers: LeaderboardUser[] = [];
    lastWinningSubmissions: WinningSubmissionDisplay[];

    displayedColumns: string[] = ['position', 'name', 'points'];
    constructor(private selfService: SelfService, private router: Router, private leagueService: LeagueService, private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.extractLeagueId();
        if (this.leagueId !== null) {
            this.fetchLeague(this.leagueId);
            this.getLeaderboardById(this.leagueId);
        }
    }

    showChallenge() {
        this.router.navigate([`league/${this.leagueId}/challenge`]);
    }

    extractLeagueId(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (id !== null) {
            this.leagueId = parseInt(id);
        } else {
            // todo show error
        }
        console.log(`league id is: ${id}`);
    }

    fetchLeague(id: number) {
        this.leagueService.getLeagueById(id).pipe(
            tap(response => {
                this.league = response;
                console.log(this.league);
                console.log('Successfully fetched league');
            }),
            catchError(error => {
                console.error('Error while fetching a leauge:', error);
                this.errorMessage = "Could not fetch the league";
                this.error = true;
                // Handle the error here
                return of(null);
            }),
            switchMap(() => {
                return this.fetchWinningSubmissions();
            })
        ).subscribe();
    }

    /**
     * Takes in a string and makes it presentable to the frontend. Removes camelcase and uppercases
     * @param str
     */
    prettyString(str: string): string {
        str = str.replace(/_/g, ' ').toLowerCase();
        return str.replace(/(^|\s)\S/g, (match) => match.toUpperCase());
    }

    getLeaderboardById(id: number) {
        this.leagueService.getLeaderboardByLeagueId(id).pipe(
            tap(response => {
                this.leaderboardUsers = response;   // already sorted by backend
                console.log(this.leaderboardUsers);
                console.log('Successfully fetched leaderboard');
            }),
            catchError(error => {
                console.error('Error while fetching leaderboard', error);
                this.errorMessage = "Could not fetch the leaderboard";
                this.error = true;
                // Handle the error here
                return of(null);
            })
        ).subscribe();
    }


    private fetchWinningSubmissions() {
        if (!this.leagueId || !this.league.lastWinners) return of(null);
        return this.leagueService.getLastWinningSubmissions(this.leagueId).pipe(
            tap(response => {
                this.lastWinningSubmissions = response;
                console.log(response);
                console.log('Successfully fetched winningSubmissions');
            }),
            catchError(error => {
                console.error('Error while fetching winningSubmissions');
                this.error = true;
                // Handle the error here
                return of(null);
            })
        );
    }
}

export interface Leaderboard {
    username: string;
    position: number;
    points: number;
}

