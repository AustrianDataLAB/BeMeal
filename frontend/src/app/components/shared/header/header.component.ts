import { Component } from '@angular/core';
import {SelfService} from "../../../services/self.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-header', templateUrl: './header.component.html', styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

    private isParticipant = true;

    constructor(private selfService: SelfService, private router: Router) {
        this.selfService.getProfile().subscribe({
            next: () => console.debug('user is a participant'), error: () => {
                console.debug('user is a gamemaster');
                this.isParticipant = false;
            }
        });
    }

    get viewAnalytics(): boolean {
        return !this.isParticipant || localStorage.getItem('bemeal.menu.showanalytics') != null;
    }

    logout() {
        this.selfService.logoutUser();
        this.router.navigate(['/']);
    }

    loggedin(): boolean{
        return this.selfService.isLoggedIn();
    }
}
