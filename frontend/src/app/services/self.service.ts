import {Injectable} from '@angular/core';
import {Registration} from '../dtos/registration';
import {map, Observable} from 'rxjs';
import {HttpClient, HttpHeaders, HttpResponse} from '@angular/common/http';

import jwt_decode from 'jwt-decode';
import {Globals} from '../shared/globals';
import {Login, PasswordReset} from '../dtos/login';

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

    /**
     * Request a password reset via email.
     * This requires the email of the user.
     * This request succeeds in any circumstance iff it reaches the server.
     * It does not provide any feedback per design, even if such email does not exist.
     * @param email the email address of the user to send the email
     */
    requestPasswordResetMail(email: string): Observable<any> {
        return this.httpClient.put(this.authBaseUri + `/password-token/${email}`, undefined);
    }

    /**
     * Reset the password with a token which was received per email before.
     *
     * @param passwordResetToken the token from the email
     * @param passwordReset the structure which contains the new password to set
     */
    resetPassword(passwordResetToken: string, passwordReset: PasswordReset): Observable<any> {
        return this.httpClient.put(this.authBaseUri + `/password/${passwordResetToken}`, passwordReset);
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
