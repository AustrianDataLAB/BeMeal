import { Component } from '@angular/core';
import {SelfService} from "../../../services/self.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {

    constructor(private selfService: SelfService, private router: Router) {
    }

    logout() {
        this.selfService.logoutUser();
        this.router.navigate(['/']);
    }

    loggedin(): boolean{
        return this.selfService.isLoggedIn();
    }
}
