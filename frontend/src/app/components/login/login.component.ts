import { Component } from '@angular/core';
import {SelfService} from "../../services/self.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {Region} from "../../shared/region";
import {Registration} from "../../dtos/registration";
import {Login} from "../../dtos/login";
import {catchError, tap} from "rxjs/operators";
import {of} from "rxjs";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent {

    submitted = true;
    error = false;
    errorMessage = '';


    loginForm: FormGroup;

    constructor(private router: Router, private formBuilder: FormBuilder, private selfService: SelfService, public fb: FormBuilder) {
        this.loginForm = this.formBuilder.group({
            username: ['', [Validators.required]],
            password: ['', [Validators.required, Validators.minLength(8)]]
        });

    }


    loginUser() {
        this.submitted = true;
        if (this.loginForm.valid) {
           const loginObj: Login = new Login(
                this.loginForm.controls['username'].value,
                this.loginForm.controls['password'].value
            );
            console.log('Try to authenticate user: ' + loginObj.toString());
            // todo: error handling
            this.selfService.loginUser(loginObj).pipe(
                tap(response => {
                    console.log(response);
                    console.log('Successful login for user: ' + loginObj.username);
                    this.router.navigate(['/leagues']);
                }),
                catchError(error => {
                    console.error('Error logging in:', error);
                    this.errorMessage = "Wrong credentials";
                    this.error = true;
                    // Handle the error here
                    return of(null);
                })
            ).subscribe();

        } else {
            this.error = true;
            this.errorMessage = 'Invalid input';
            console.log('Invalid input');
        }

    }


    vanishError() {
        this.error = false;
    }

}
