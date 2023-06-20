import {Injectable} from '@angular/core';
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable} from 'rxjs';
import {tap} from 'rxjs/operators';
import {SelfService} from '../services/self.service';
import {Router} from '@angular/router';

@Injectable()
export class InvalidTokenInterceptor implements HttpInterceptor {

    constructor(private selfService: SelfService, private router: Router) {
    }

    intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
        return next.handle(request).pipe(tap(
            {
                error: err => {
                    if (err.status === 401 && !request.url.endsWith('/api/v1/self-service/login')) {
                        console.debug('Session token is invalid, perform logout');
                        this.selfService.logoutUser();
                        this.router.navigate(['/']).then(console.debug);
                    }
                }
            }
        ));
    }
}
