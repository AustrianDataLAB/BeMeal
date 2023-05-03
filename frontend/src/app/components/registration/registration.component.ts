import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {SelfService} from "../../services/self.service";
import {Registration} from "../../dtos/registration";
import {Region} from "../../shared/region";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {HttpErrorResponse} from "@angular/common/http";
import {Router} from "@angular/router";
import {catchError, tap} from "rxjs/operators";
import {of} from "rxjs";

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit{

    submitted = true;
    error = false;
    errorMessage = '';

    registerForm: FormGroup;
    regions = Region;
    regionKeys = Object.keys;

    constructor(private router: Router, private formBuilder: FormBuilder, private selfService: SelfService, public fb: FormBuilder) {
        this.registerForm = this.formBuilder.group({
            email: ['', [Validators.required, Validators.pattern('^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$')]],
            username: ['', [Validators.required]],
            postalCode: ['', [Validators.required]],
            region: ['', [Validators.required]],
            password: ['', [Validators.required, Validators.minLength(8)]]
        });

    }

    ngOnInit() {
        if(this.selfService.isLoggedIn()) {
            this.router.navigate(['/leagues'])
        }
    }

    toLogin() {
        this.router.navigate(['/login']);
    }
    registerParticipant() {
        this.submitted = true;
        this.error = false;
        if (this.registerForm.valid) {
            const regionEnumIndex = Object.keys(Region).indexOf(this.registerForm.controls['region'].value);
            const regionValue = Object.values(Region)[regionEnumIndex];
            const registerObj: Registration = new Registration(
                this.registerForm.controls['email'].value,
                this.registerForm.controls['username'].value,
                this.registerForm.controls['password'].value,
                regionValue,
                this.registerForm.controls['postalCode'].value
            );
            this.selfService.registerParticipant(registerObj).pipe(
                tap(response => {
                    console.log(response);
                    console.log('Successful registration for user: ' + registerObj.username);
                    this.router.navigate(['/login']);
                }),
                catchError(error => {
                    console.error('Error while registration:', error);
                    this.errorMessage = "Error: " + error.error.message;
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
