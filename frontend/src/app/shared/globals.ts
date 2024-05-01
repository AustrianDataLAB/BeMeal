import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
    providedIn: 'root'
})
export class Globals {
    private _backendUri: string;
    private httpClient = inject(HttpClient);

    get backendUri(): string {
        if (this._backendUri!!) {
            return this._backendUri;
        }
        this.httpClient.get<{ BACKEND_URL: string }>('/assets/env.json').subscribe({
            next: urlVar => this._backendUri = urlVar.BACKEND_URL,
            error: error => {
                if (window.location.port === '4200') { // local `ng serve`, backend at localhost:8080
                    this._backendUri = 'http://localhost:8080/api/v1';
                } else {
                    console.error(`Could not load environment variables ${error}`);
                }
            }
        });
        return this._backendUri;
    }
}



