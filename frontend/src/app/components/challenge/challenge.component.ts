import {Component, ElementRef, Input, OnInit, Renderer2} from '@angular/core';
import {animate, AnimationEvent, keyframes, transition, trigger} from "@angular/animations";
import * as kf from './keyframes';
import {firstValueFrom, map, of, Subject} from "rxjs";
import {catchError, tap} from "rxjs/operators";
import {League} from "../../dtos/league";
import {ChallengeInfo} from "../../dtos/challengeInfo";
import {LeagueService} from "../../services/league.service";
import {ActivatedRoute} from "@angular/router";
import {Submission} from "../../dtos/submission";

@Component({
  selector: 'app-challenge',
  templateUrl: './challenge.component.html',
  styleUrls: ['./challenge.component.scss'],
  animations: [
    trigger('cardAnimator', [
        transition('* => swiperight', animate(750, keyframes(kf.swiperight))),
        transition('* => swipeleft', animate(750, keyframes(kf.swipeleft)))
    ])
]
})
export class ChallengeComponent {
    public index = 0;
    cardState: string;
    upvotingEnabled = false;
    challenge: ChallengeInfo;
    submission: Submission = {} as Submission;

    error = false;
    errorMessage = '';
    leagueId: number;

    constructor(private elementRef: ElementRef, private renderer: Renderer2, private leagueService: LeagueService, private route: ActivatedRoute) {
        const id = this.route.snapshot.paramMap.get('id');
        if (id !== null) {
            this.leagueId = parseInt(id);
        } else {
            // todo show error
        }
        console.log(`league id is: ${id}`);
        this.fetchChallenge(this.leagueId);
    }

    resetAnimationState(state: AnimationEvent) {
        this.cardState = '';
        this.index++;
    }

    onSwipeRight() {
        this.cardState = 'swiperight'; // trigger swiperight animation
    }

    onSwipeLeft() {
        this.cardState = 'swipeleft'; // trigger swipeleft animation
    }

    fetchChallenge(id: number) {
        this.leagueService.getChallengeForLeague(id).pipe(
            tap(response => {
                this.challenge = response;
                this.submission.challengeId = this.challenge.challengeId;
                console.log(this.challenge);
                console.log(this.submission)
                console.log('Successfully fetched challenge');
            }),
            catchError(error => {
                console.error('Error while fetching challenge:', error);
                this.errorMessage = "Could not fetch challenge";
                this.error = true;
                // Handle the error here
                return of(null);
            })
        ).subscribe();
    }

    onImageChangeNOCOMPRESSION(event: Event) {
        const input = event.target as HTMLInputElement;
        if (input.files != null) {
            this.submission.image = input.files[0];
            console.log(this.submission)
        }

    }

    submit() {
        console.log(this.submission)
    }



    switchUpvotingContainer() {
        const element = this.elementRef.nativeElement.querySelector('.container');
        if (this.upvotingEnabled) {
            this.upvotingEnabled = false;
            //this.renderer.setStyle(document.body, 'filter', 'unset');
            this.renderer.setStyle(element, 'filter', 'unset');
        } else {
            this.upvotingEnabled = true;
            this.renderer.setStyle(element, 'filter', 'blur(3px)');
        }
    }


}
