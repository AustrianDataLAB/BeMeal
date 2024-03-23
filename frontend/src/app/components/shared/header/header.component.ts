import { Component } from '@angular/core';
import {SelfService} from "../../../services/self.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-header', templateUrl: './header.component.html', styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

    private isParticipant = true;

    constructor(private selfService: SelfService, private router: Router) {
        this.selfService.isGameMaster.subscribe({
            next: value => {
                this.isParticipant = !value;
                console.debug('updated is participant state', this.isParticipant);
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
