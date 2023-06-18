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
    currentWins = 0;

    constructor(private selfService: SelfService) {
    }

    ngOnInit() {
        this.getProfile();
    }

    resetPassword() {
        alert("todo");
        // todo
    }
    public getProfile() {
        this.selfService.getProfile()
            .pipe(tap(response => {
                    console.log(response)
                    this.profile = response;
                    for(const[a,b] of Object.entries(this.profile.wins)) {
                        this.currentWins+=b;
                    }
                    // let k: keyof typeof this.profile.wins;
                    // for (k in this.profile.wins) {
                    //     this.currentWins+=this.profile.wins[k]
                    // }
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


    /**
     * Takes in a string and makes it presentable to the frontend. Removes camelcase and uppercases
     * @param str
     */
    prettyString(str: string): string {
        str = str.replace(/_/g, ' ').toLowerCase();
        return str.replace(/(^|\s)\S/g, (match) => match.toUpperCase());
    }
}
