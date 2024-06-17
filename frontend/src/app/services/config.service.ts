import {Injectable} from '@angular/core';
import {Env} from "@app/dtos/env";

// inspired by https://borstch.com/blog/development/using-angular-environment-variables-for-configuration
@Injectable({
  providedIn: 'root'
})
export class ConfigService {
    private env: Env  = {
        backendUri: 'http://localhost:8080/api/v1'
    }

    loadEnv() {
        if (window['__env']) {
            Object.assign(this.env, window['__env'])
        } else {
            console.debug('No window.__env found, using default env')
        }
    }

    get backendUri() : string {

        return this.env['BACKEND_URL'];
    }


}
