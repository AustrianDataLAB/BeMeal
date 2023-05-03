import {Component, OnInit} from '@angular/core';
import {SelfService} from "../../services/self.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LeagueService} from "../../services/league.service";
import {catchError, tap} from "rxjs/operators";
import {firstValueFrom, map, of} from "rxjs";
import {League} from "../../dtos/league";

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

    constructor(private selfService: SelfService, private router: Router, private leagueService: LeagueService, private route: ActivatedRoute) {
    }

    ngOnInit() {
        this.extractLeagueId();
        if (this.leagueId !== null) this.fetchLeague(this.leagueId);
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
                console.error('Error while creating a leauge:', error);
                this.errorMessage = "Could not create the league";
                this.error = true;
                // Handle the error here
                return of(null);
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


}
