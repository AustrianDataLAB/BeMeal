import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {League} from "../dtos/league";
import {map, Observable} from "rxjs";
import {ChallengeInfo} from "../dtos/challengeInfo";
import {LeaderboardUser} from "../dtos/leaderboard-user";
import {WinningSubmissionDisplay} from "../dtos/winning-submission-display";
import {ConfigService} from "@services/config.service";

@Injectable({
  providedIn: 'root'
})
export class LeagueService {

    private baseUri: string = this.configService.backendUri  + '/league';

    constructor(private httpClient: HttpClient, private configService: ConfigService) {
    }

    createLeague(obj: League): Observable<string> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*'
            })
        };
        console.debug(obj);
        return this.httpClient.post<any>(this.baseUri, obj, httpOptions)
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
