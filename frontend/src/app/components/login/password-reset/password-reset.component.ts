import { Component } from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {SelfService} from '../../../services/self.service';

@Component({
    selector: 'app-password-reset',
    templateUrl: './password-reset.component.html',
    styleUrls: ['./password-reset.component.scss']
})
export class PasswordResetComponent {

    private _passwordResetToken: string | null = null;

    tokenRequestForm: FormGroup;
    passwordResetForm: FormGroup;
    faulty = false;
    emailSuccess = false;
    resetSuccess = false;

    get passwordResetToken() {
        return this._passwordResetToken;
    }

    constructor(private route: ActivatedRoute, private selfService: SelfService, private formBuilder: FormBuilder) {
        this.route.paramMap.subscribe(value => this._passwordResetToken = value.get('token'));
        this.tokenRequestForm = this.formBuilder.group({
            email: ['', [Validators.required]],
        });
        this.passwordResetForm = this.formBuilder.group({
            password: ['', [Validators.required, Validators.minLength(8)]],
            'password-repeat': ['', [Validators.required, Validators.minLength(8)]]
        });
    }

    requestToken() {
        console.debug('request token');
        const email: string = this.tokenRequestForm.controls['email'].value;
        this.selfService.requestPasswordResetMail(email).subscribe({next: () => this.emailSuccess = true, error: () => this.setErrorState()});
        this.selfService.logoutUser();
    }

    resetPassword() {
        console.debug('reset password');
        const password: string = this.passwordResetForm.controls['password'].value;
        this.selfService.resetPassword(this.passwordResetToken!, {password: password}).subscribe({next: () => this.resetSuccess = true, error: () => this.setErrorState()});
        this.selfService.logoutUser();
    }

    private setErrorState() {
        this.faulty = true;
        this.emailSuccess = this.resetSuccess = false;
        this._passwordResetToken = null;
    }
}
