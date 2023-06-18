import {Component, Input, OnInit, Inject } from '@angular/core';
import {SelfService} from "../../services/self.service";
import {ActivatedRoute, Router} from "@angular/router";
import {catchError, tap} from "rxjs/operators";
import {of, switchMap} from "rxjs";
import {RecipeService} from "../../services/recipe.service";
import {Recipe} from "../../dtos/recipe";
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-recipe',
  templateUrl: './recipe.component.html',
  styleUrls: ['./recipe.component.scss']
})

export class RecipeComponent implements OnInit{

    recipe: Recipe;
    error: boolean;
    errorMessage: string;

    constructor(private recipeService: RecipeService, @Inject(MAT_DIALOG_DATA) public recipeId: string) {
    }

    ngOnInit() {
        console.log(this.recipe);
        this.fetchRecipe(this.recipeId);
    }

    fetchRecipe(id: string) {
        this.recipeService.findRecipeById(id).pipe(
            tap(response => {
                this.recipe = response;
                console.log(this.recipe);
                console.log('Successfully fetched recipe');
            }),
            catchError(error => {
                console.error('Error while fetching a recipe:', error);
                this.errorMessage = "Could not fetch the recipe";
                this.error = true;
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
