import {Component, ElementRef, Input, Renderer2} from '@angular/core';
import {animate, AnimationEvent, keyframes, transition, trigger} from "@angular/animations";
import * as kf from './keyframes';
import {finalize, firstValueFrom, map, of, Subject, switchMap} from "rxjs";
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
export class ChallengeComponent {
    public index = 0;
    cardState: string;
    upvotingEnabled = false;    // for the swiping thing
    canUpvote = false;  // for the "Rate for friends" box, if everything swiped already or nothing to swipe, hide
    upvoteSubmissions: SubmissionDisplay[];
    currentPicture: string;
    challenge: ChallengeInfo;
    submission: Submission = {} as Submission;
    currentSubmission : SubmissionDisplay; // if participant already uploaded a submission to the current challenge
    error = false;
    errorMessage = '';
    leagueId: number;
    countdownString = "Time ";
    isUploading = false;

    constructor(private elementRef: ElementRef, private renderer: Renderer2, private submissionService: SubmissionService, private leagueService: LeagueService, private route: ActivatedRoute) {
        const id = this.route.snapshot.paramMap.get('id');
        if (id !== null) {
            this.leagueId = parseInt(id);
        } else {
            // todo show error
        }
        console.debug(`league id is: ${id}`);
        console.debug(this.submission.image);
        this.fetchChallenge(this.leagueId);
        setInterval(() => { this.deadlineCountdown(); }, 1000);
    }

    resetAnimationState(state: AnimationEvent) {
        console.debug(state);
        this.cardState = '';
        console.debug("RESET");
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
                console.debug(response);
                console.debug('Successfully voted submission');
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
            console.debug("next submission exists");
        } else {
            this.canUpvote = false;
            this.switchUpvotingContainer();
            console.debug("next submission does not exist");
            // todo show a message to user
            // no more img to show and submissions to upvote
        }
    }

    deadlineCountdown(): void {
        if (this.challenge) {
            const now = new Date();
            const challDate = new Date(this.challenge.endDate);
            const timeDiffMs = challDate.getTime() - now.getTime();
            const days = Math.floor(timeDiffMs / (1000 * 60 * 60 * 24)) + 1;
            const hours = Math.floor((timeDiffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutes = Math.floor((timeDiffMs % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((timeDiffMs % (1000 * 60)) / 1000);
            this.countdownString = `${days} days, ${hours} hours, ${minutes} minutes & ${seconds} seconds `;
        }
    }
    fetchChallenge(id: number) {
        this.leagueService.getChallengeForLeague(id).pipe(
            tap(response => {
                this.challenge = response;
                console.debug(this.challenge);
                this.submission.challengeId = this.challenge.challengeId;
                console.debug(this.submission);
                console.debug('Successfully fetched challenge');
                this.getAllSubmissionsToUpvote();
            }),
            catchError(error => {
                console.error('Error while fetching challenge:', error);
                this.errorMessage = "Could not fetch challenge";
                this.error = true;
                // Handle the error here
                return of(null);
            }),
            switchMap(() => {
                return this.getCurrentSubmission();
            })
        ).subscribe();
    }

    onImageChangeNOCOMPRESSION(event: Event) {
        const input = event.target as HTMLInputElement;
        if (input.files != null) {
            this.submission.image = input.files[0];
            console.debug(this.submission);
        }

    }

    submit() {
        this.isUploading = true;
        this.submissionService.postSubmission(this.submission).pipe(
            tap(response => {
                console.debug(response);
                console.debug('Successfully submitted challenge');
                const newSubmission: Submission = {} as Submission;
                newSubmission.challengeId = this.submission.challengeId;
                this.submission = newSubmission;
            }),
            catchError(error => {
                console.error('Error while fetching challenge:', error);
                this.errorMessage = "Could not fetch challenge";
                this.error = true;
                // Handle the error here
                return of(null);
            }),
            switchMap(() => {
                return this.getCurrentSubmission();
            }),
            finalize(() => {
                this.isUploading = false;
            })
        ).subscribe();
    }

    getCurrentSubmission() {
        return this.submissionService.getCurrentSubmission(this.challenge.challengeId).pipe(
            tap(response => {
                console.debug(response);
                this.currentSubmission = response;
                console.debug('Successfully fetched current submission');
            }),
            catchError(error => {
                console.debug("error fetching current submission");
                console.debug(error);
                // todo
                // Handle the error here
                return of(null);
            })
        );
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
        //this.cardState = '';
        this.currentPicture = 'data:image/png;base64,' + this.upvoteSubmissions[this.index].picture;
    }

    getAllSubmissionsToUpvote() {
        console.debug('Fetching submissions with challengeId ' + this.challenge.challengeId);
        this.submissionService.getAllSubmissions(this.challenge.challengeId).pipe(
            tap(response => {
                console.debug(response);
                this.canUpvote = true;
                this.upvoteSubmissions = response;
                this.showImage();
                console.debug('Successfully fetched submissions');
            }),
            catchError(error => {
                console.debug("error fetching submissions");
                console.debug(error);
                // todo
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
