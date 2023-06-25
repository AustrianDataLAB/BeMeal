import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {SelfService} from '../services/self.service';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(private selfService: SelfService,
                private router: Router) {}

    canActivate(): boolean {
        if (this.selfService.isLoggedIn()) {
            return true;
        } else {
            this.router.navigate(['/login']);
            return false;
        }
    }
}
