import { Component } from '@angular/core';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.scss']
})
export class PasswordResetComponent {

    private _passwordResetToken: string | null = null;

    get passwordResetToken() {
        return this._passwordResetToken;
    }

    constructor(private route: ActivatedRoute) {
        this.route.paramMap.subscribe(value => this._passwordResetToken = value.get('token'));
    }
}
