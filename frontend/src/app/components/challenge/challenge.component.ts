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
import {SubmissionService} from "../../services/submission.service";
import {SubmissionDisplay} from "../../dtos/submission-display";

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
export class ChallengeComponent implements OnInit{
    public index = 0;
    cardState: string;
    upvotingEnabled = false;    // for the swiping thing
    canUpvote = false;  // for the "Rate for friends" box, if everything swiped already or nothing to swipe, hide
    upvoteSubmissions: SubmissionDisplay[];
    currentPicture: string;
    challenge: ChallengeInfo;
    submission: Submission = {} as Submission;
    error = false;
    errorMessage = '';
    leagueId: number;

    constructor(private elementRef: ElementRef, private renderer: Renderer2, private submissionService: SubmissionService, private leagueService: LeagueService, private route: ActivatedRoute) {
        const id = this.route.snapshot.paramMap.get('id');
        if (id !== null) {
            this.leagueId = parseInt(id);
        } else {
            // todo show error
        }
        console.log(`league id is: ${id}`);
        this.fetchChallenge(this.leagueId);
    }

    ngOnInit() {
        this.getAllSubmissionsToUpvote();
    }

    resetAnimationState(state: AnimationEvent) {
        console.log(state);
        this.cardState = '';
        console.log("RESET");
        if (state.toState === 'swiperight' || state.toState === 'swipeleft') {
            this.showNextSubmission();
        }

    }

    // called if swipe right (upvote) AND swipe left (dislike)
    doUpvoting(isUpvote: boolean) {
        // do voting and load next if success (change cardState):
        const id = this.upvoteSubmissions[this.index].id;
        this.submissionService.upvoteSubmission(id, isUpvote).pipe(
            tap(response => {
                console.log(response)
                console.log('Successfully voted submission');
            }),
            catchError(error => {
                console.error('Error while voting submission:', error);
                this.errorMessage = "Could not vote submission";
                this.error = true;
                // todo handle errors if not successful
                // Handle the error here
                return of(null);
            })
        ).subscribe();
    }

    onSwipeLeft() {
        this.cardState = 'swipeleft'; // trigger swipeleft animation
        this.doUpvoting(false);
    }

    onSwipeRight() {
        this.cardState = 'swiperight';
        this.doUpvoting(true);
    }


    showNextSubmission() {
        if ((this.index+1) < this.upvoteSubmissions.length) {
            this.index += 1;
            this.showImage();
            console.log("next submission exists")
        } else {
            this.canUpvote = false;
            this.switchUpvotingContainer();
            console.log("next submission does not exist")
            // todo show a message to user
            // no more img to show and submissions to upvote
        }
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
        this.submissionService.postSubmission(this.submission).pipe(
            tap(response => {
                console.log(response)
                console.log('Successfully submitted challenge');
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



    switchUpvotingContainer() {
        const element = this.elementRef.nativeElement.querySelector('.container');
        if (this.upvotingEnabled) {
            this.upvotingEnabled = false;
            this.index = 0;
            //this.renderer.setStyle(document.body, 'filter', 'unset');
            this.renderer.setStyle(element, 'filter', 'unset');
        } else {
            this.upvotingEnabled = true;
            this.renderer.setStyle(element, 'filter', 'blur(3px)');
        }
    }

    // takes the arraybuffer from submission and returns the base64
    showImage(): void {
        //console.log("current index for images: " + this.index);
        //this.cardState = '';
        this.currentPicture = 'data:image/png;base64,' + this.upvoteSubmissions[this.index].picture;
    }

    getAllSubmissionsToUpvote() {
        this.submissionService.getAllSubmissions().pipe(
            tap(response => {
                console.log(response);
                this.canUpvote = true;
                this.upvoteSubmissions = response;
                this.showImage();
                //console.log(this.currentPicture);
                //console.log(this.upvoteSubmissions)
                console.log('Successfully fetched submissions');
            }),
            catchError(error => {
                console.log("error fethcing submissions");
                console.log(error);
                // todo
                // Handle the error here
                return of(null);
            })
        ).subscribe();
    }


}
