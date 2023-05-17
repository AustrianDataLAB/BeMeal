import {Component} from "@angular/core";
import {Observable} from "rxjs";
import {DarkModeService} from "angular-dark-mode";

@Component({
    selector: 'app-dark-mode',
    templateUrl: './dark-mode.component.html',
    styleUrls: ['./dark-mode.component.scss']
})
export class DarkModeComponent {
    darkMode: Observable<boolean> = this.darkModeService.darkMode$;
    lightActive = true;
    constructor(private darkModeService: DarkModeService) {}

    onToggle(): void {
        if (this.lightActive) {
            this.lightActive = false;
        } else {
            this.lightActive = true;
        }
        this.darkModeService.toggle();
    }
}
