import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {SelfService} from '../services/self.service';
import {Observable} from 'rxjs';
import {Globals} from '../shared/globals';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(private selfService: SelfService, private globals: Globals) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const backendUri = this.globals.backendUri + '/env';
        const authUri = this.globals.backendUri + '/authentication';

        // Do not intercept authentication requests
        if ( req.url == backendUri || req.url === authUri)  {
            return next.handle(req);
        }
        if (req.url.includes('login')) {
            return next.handle(req);
        }

        let authReq;
        if (req.url.includes('registration')) {
            authReq = req.clone({
                //headers: req.headers.set('Authorization', 'Bearer ' + this.authService.getToken())
            });
        } else {
            authReq = req.clone({
                headers: req.headers.set('Authorization', 'Bearer ' + this.selfService.getToken())
            });
        }

        return next.handle(authReq);
    }
}
