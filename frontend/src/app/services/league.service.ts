import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {Globals} from "../shared/globals";
import {League} from "../dtos/league";
import {map, Observable, throwError} from "rxjs";
import {JoinLeague} from "../dtos/join-league";
import {catchError} from "rxjs/operators";
import {ChallengeInfo} from "../dtos/challengeInfo";
import {Submission} from "../dtos/submission";
import {LeaderboardUser} from "../dtos/leaderboard-user";
import {WinningSubmissionDisplay} from "../dtos/winning-submission-display";

@Injectable({
  providedIn: 'root'
})
export class LeagueService {

    private baseUri: string = this.globals.backendUri + '/league';

    constructor(private httpClient: HttpClient, private globals: Globals) {
    }

    createLeague(obj: League): Observable<string> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*'
            })
        };
        console.log(obj);
        return this.httpClient.post<any>(this.baseUri + '/create-league', obj, httpOptions)
            .pipe(map(response => {
                return response;
            }));
    }

    fetchLeagues(): Observable<League[]> {
        return this.httpClient.get<League[]>(this.baseUri + '/leagues');
    }

    getChallengeForLeague(id: number): Observable<ChallengeInfo> {
        return this.httpClient.get<ChallengeInfo>(this.baseUri + '/challenge/' + id);
    }

    getLeagueById(id: number): Observable<League> {
        return this.httpClient.get<(League)>(`${this.baseUri}/${id}`);
    }

    getLeagueByHiddenIdentifier(hiddenIdentifier: string): Observable<League> {
        return this.httpClient.get<League>(`${this.baseUri}/hidden-identifier/${hiddenIdentifier}`);
    }

    getLeaderboardByLeagueId(id: number): Observable<LeaderboardUser[]> {
        return this.httpClient.get<LeaderboardUser[]>(`${this.baseUri}/${id}/leaderboard`);
    }


    getLastWinningSubmissions(leagueId: number) {
        return this.httpClient.get<WinningSubmissionDisplay[]>(`${this.baseUri}/${leagueId}/winningSubmissions`);
    }
}
