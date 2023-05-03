import {Component, ElementRef, Input, OnInit, Renderer2} from '@angular/core';
import {animate, AnimationEvent, keyframes, transition, trigger} from "@angular/animations";
import * as kf from './keyframes';
import {Subject} from "rxjs";

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

    constructor(private elementRef: ElementRef, private renderer: Renderer2) {}

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
