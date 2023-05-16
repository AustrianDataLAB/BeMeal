import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Globals} from '../shared/globals';
import {LeagueSecrets} from '../dtos/league';
import {League} from "../dtos/league";
import {Observable} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class InvitationService {

    private baseUri: string = this.globals.backendUri + '/invitation';

    constructor(private httpClient: HttpClient, private globals: Globals) {
    }

    /**
     * Receive the hidden identifier for a league.
     * This call only works, if the callee is the creator of the league.
     * This identifier may be used for the invitation link.
     * @param id the id of the league to receive the hidden identifier
     */
    getHiddenIdentifier(id: number): Observable<LeagueSecrets> {
        return this.httpClient.get<LeagueSecrets>(this.baseUri + `/hidden-identifier/${id}`);
    }

    joinLeague(hiddenIdentifier: string): Observable<League> {
        return this.httpClient.get<any>(`${this.baseUri}/join-league/${hiddenIdentifier}`);
    }
}
