import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {SelfService} from "../../services/self.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit{

    constructor(private selfService: SelfService, private router: Router) {

    }

    ngOnInit() {
        if(this.selfService.isLoggedIn()) {
            this.router.navigate(['/leagues'])
        }
    }

    register() {
        this.router.navigate(['/register']);
    }
}
