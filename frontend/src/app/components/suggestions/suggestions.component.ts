import { Component } from '@angular/core';
import {SelfService} from "../../services/self.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LeagueService} from "../../services/league.service";
import {RecipeService} from "../../services/recipe.service";
import {Recipe} from "../../dtos/recipe";
import {catchError, finalize, tap} from "rxjs/operators";
import {of} from "rxjs";
import {animate, AnimationEvent, keyframes, transition, trigger} from "@angular/animations";
import * as kf from "../challenge/keyframes";
import {RecipeWithId} from "../../dtos/recipeWithId";
import {Suggestion} from "../../dtos/suggestion";

@Component({
  selector: 'app-suggestions',
  templateUrl: './suggestions.component.html',
  styleUrls: ['./suggestions.component.scss'],
  animations: [
      trigger('cardAnimator', [
          transition('* => swiperight', animate(750, keyframes(kf.swiperight))),
          transition('* => swipeleft', animate(750, keyframes(kf.swipeleft)))
      ])
  ]
})
export class SuggestionsComponent {

    randomRecipes: RecipeWithId[] = [];
    likedRecipes: string[] = [];

    suggestions: Suggestion;
    sugg = true;

    limit = 5;

    public index = 0;
    toggle = true;
    cardState: string;
    error: boolean;
    errorMessage: string;
    currentPicture: string;
    isFetching = false;

    constructor(private router: Router, private recipeService: RecipeService, private route: ActivatedRoute) {
        this.getRandomRecipes();
    }

    getRandomRecipes() {
        this.recipeService.getMultipleRandomRecipes().pipe(
            tap(reponse => {
                this.randomRecipes = reponse;
                console.debug(this.randomRecipes);
                console.debug("Successfully fetched recipes");
                this.showImage();
            }),
            catchError( error => {
                console.error("Caught error while fetching multiple random recipes", error)
                this.errorMessage = "aught error while fetching multiple random recipes";
                this.error = true;
                // Handle the error here
                return of(null);
            })
        ).subscribe();
    }
    switchPref() {
        this.toggle = !this.toggle;
    }

    getSuggestions() {
        this.isFetching = true;
        this.sugg = false;
        this.recipeService.getSuggestionFromRecipes(this.likedRecipes).pipe(
            tap(response => {
                this.suggestions = response;
                console.debug("Response");
                console.debug(this.suggestions);
            }),
            catchError(() => {
                return of(null);
            }),
            finalize(() => {
                this.isFetching = false;
                }
            )
        ).subscribe();
    }

    reset() {
        this.sugg = true;
        this.likedRecipes = [];
        this.suggestions = new Suggestion([],[]);
        this.getRandomRecipes();
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
        const rec = this.randomRecipes[this.index];
        if(isUpvote) {
            this.likedRecipes.push(rec.recipeId)
        }
        if (this.likedRecipes.length > (this.limit - 1)) {
            this.getSuggestions();
        }
        console.debug(this.likedRecipes);
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
        if ((this.index+1) < this.randomRecipes.length) {
            this.index += 1;
            console.debug(this.index);
            this.showImage();
            console.debug("next submission exists");
        } else {
            this.getRandomRecipes();
            this.index = 0;
            this.showImage();
        }
    }

    showImage(): void {
        //this.cardState = '';
        this.currentPicture = 'data:image/png;base64,' + this.randomRecipes[this.index].picture;
    }

    prettyString(str: string): string {
        str = str.replace(/_/g, ' ').toLowerCase();
        return str.replace(/(^|\s)\S/g, (match) => match.toUpperCase());
    }

}
