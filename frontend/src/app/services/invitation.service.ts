import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {League, LeagueSecrets} from '../dtos/league';
import {Observable} from 'rxjs';
import {ConfigService} from "@services/config.service";

@Injectable({
    providedIn: 'root'
})
export class InvitationService {

    private baseUri: string = this.configService.backendUri + '/invitation';

    constructor(private httpClient: HttpClient, private configService: ConfigService) {
    }

    /**
     * Receive the hidden identifier for a league.
     * This call only works, if the callee is the creator of the league.
     * This identifier may be used for the invitation link.
     * @param id the id of the league to receive the hidden identifier
     */
    getHiddenIdentifier(id: number, refresh: boolean): Observable<LeagueSecrets> {
        return this.httpClient.get<LeagueSecrets>(this.baseUri + `/hidden-identifier/${id}/${refresh}`);
    }

    joinLeague(hiddenIdentifier: string): Observable<League> {
        return this.httpClient.get<any>(`${this.baseUri}/join-league/${hiddenIdentifier}`);
    }
}
