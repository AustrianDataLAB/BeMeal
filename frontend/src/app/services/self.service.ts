import {Injectable} from '@angular/core';
import {Registration} from '../dtos/registration';
import {map, Observable} from 'rxjs';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';
import {tap} from 'rxjs/operators';

import jwt_decode from 'jwt-decode';
import {Globals} from '../shared/globals';
import {Login} from "../dtos/login";
import {Profile} from "../dtos/profile";

@Injectable({
    providedIn: 'root'
})
export class SelfService {

    private authBaseUri: string = this.globals.backendUri + '/self-service';

    constructor(private httpClient: HttpClient, private globals: Globals) {}


    /**
     * Register the user. If it was successful, a valid JWT token will be stored
     *
     * @param authRequest User data
     */
    registerParticipant(obj: Registration): Observable<string> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Access-Control-Allow-Origin': '*'
            })
        };
        return this.httpClient.post<any>(this.authBaseUri + '/registration/participant', obj, httpOptions)
            .pipe(map(response => {
                // todo: remove pipe if unnecessary
                //console.log(response);
                return response;
            }));
    }

    /**
     * Login in the user. If it was successful, a valid JWT token will be stored
     * @param authRequest User data
     */
    loginUser(obj: Login): Observable<HttpResponse<any>> {
        const httpOptions = {
            headers: new HttpHeaders({
                'Content-Type': 'application/json',
                'Authorization': 'Basic ' + btoa(`${obj.username}:${obj.password}`)
            })
        };
        return this.httpClient.get<any>(this.authBaseUri + '/login', { ...httpOptions, observe: 'response' })
            .pipe(map(response => {
                if (response.ok) {
                    //TODO find out why Authorization header is not there
                    const authHeader = response.headers.get('Authorization');
                    console.log(response.headers);
                    if (authHeader) {
                        this.setToken(authHeader);
                    }
                    console.log('login successful');
                }
                return response;
            }));
    }


    getProfile(): Observable<Profile> {
        return this.httpClient.get<Profile>(this.authBaseUri + '/profile');
    }

    /**
     * Check if a valid JWT token is saved in the localStorage
     */
    isLoggedIn() {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        return !!this.getToken() && (this.getTokenExpirationDate(this.getToken()).valueOf() > new Date().valueOf());
    }

    logoutUser() {
        console.log('Logout');
        localStorage.removeItem('authToken');
    }

    getToken() {
        return localStorage.getItem('authToken');
    }


    private setToken(tokenHeader: string) {
        console.log(tokenHeader);
        const tokenWithoutBearer = tokenHeader.substring(7);
        localStorage.setItem('authToken', tokenWithoutBearer);
    }

    private getTokenExpirationDate(token: string): Date | null {

        const decoded: any = jwt_decode(token);
        if (decoded.exp === undefined) {
            return null;
        }

        const date = new Date(0);
        date.setUTCSeconds(decoded.exp);
        return date;
    }

}
