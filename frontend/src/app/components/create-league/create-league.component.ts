import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {SelfService} from "../../services/self.service";
import {Region} from "../../shared/region";
import {Registration} from "../../dtos/registration";
import {catchError, tap} from "rxjs/operators";
import {of} from "rxjs";
import {League} from "../../dtos/league";
import {LeagueService} from "../../services/league.service";
import {GameMode} from "../../shared/game-mode";

@Component({
  selector: 'app-create-league',
  templateUrl: './create-league.component.html',
  styleUrls: ['./create-league.component.scss']
})
export class CreateLeagueComponent {

    registerForm: FormGroup;
    regions = Region;
    regionKeys = Object.keys;

    gamemodes = GameMode;
    gameModeKeys = Object.keys;
    submitted = true;
    error = false;
    errorMessage = '';

    constructor(private router: Router, private formBuilder: FormBuilder, private leagueService: LeagueService, public fb: FormBuilder) {
        this.registerForm = this.formBuilder.group({
            name: ['', [Validators.required]],
            gamemode: ['', [Validators.required]],
            challengeDuration: ['', [Validators.required]],
            region: ['', [Validators.required]]
        });
    }

    createLeague() {
        this.submitted = true;
        if (this.registerForm.valid) {
            const regionEnumIndex = Object.keys(Region).indexOf(this.registerForm.controls['region'].value);
            const regionValue = Object.values(Region)[regionEnumIndex];
            const gameModeEnumIndex = Object.keys(GameMode).indexOf(this.registerForm.controls['gamemode'].value);
            const gameModeValue = Object.values(GameMode)[gameModeEnumIndex];
            const leagueObj: League = new League(null,
                this.registerForm.controls['name'].value,
                gameModeValue,
                parseInt(this.registerForm.controls['challengeDuration'].value), // TODO hier checken auch ob es wirklich eine zahl ist
                regionValue,
            );
            this.leagueService.createLeague(leagueObj).pipe(
                tap(response => {
                    console.log(response);
                    console.log('Successfully create league');
                    this.router.navigate(['/leagues']);
                }),
                catchError(error => {
                    console.error('Error while creating a leauge:', error);
                    this.error = true;
                    this.errorMessage = "Error: " + error.error.message;
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
