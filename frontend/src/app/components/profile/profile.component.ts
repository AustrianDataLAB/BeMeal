import {Component} from '@angular/core';
import {Profile} from "../../dtos/profile";
import {SelfService} from "../../services/self.service";
import {catchError, tap} from "rxjs/operators";
import {of} from "rxjs";

@Component({
    selector: 'app-profile',
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.scss']
})
export class ProfileComponent {

    public profile: Profile;
    error = false;
    errorMessage = "";

    constructor(private selfService: SelfService) {
    }

    ngOnInit() {
        this.getProfile();
    }

    public getProfile() {
        this.selfService.getProfile()
            .pipe(tap(response => {
                    console.log(response)
                    this.profile = response
                }),
                catchError(error => {
                    console.error('Error retrieving profile:', error);
                    this.errorMessage = "Wrong credentials";
                    this.error = true;
                    // Handle the error here
                    return of(null);
                })
            ).subscribe();
    }
}
