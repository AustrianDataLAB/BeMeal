import { Component } from '@angular/core';
import {RecipeCollection} from "../../dtos/recipe-collection";
import {Router} from "@angular/router";
import {RecipeCollectionService} from "../../services/recipe-collection.service";
import {catchError, tap} from "rxjs/operators";
import {of} from "rxjs";

@Component({
  selector: 'app-meals',
  templateUrl: './meals.component.html',
  styleUrls: ['./meals.component.scss']
})
export class MealsComponent {
    error = false;
    errorMessage = '';
    cookbooks: RecipeCollection[];
    //recipes: Recipe[];

    constructor(private router: Router, private recipeCollectionService: RecipeCollectionService) {
        this.getRandomizedRecipeCollectionSelection();
    }

    getRandomizedRecipeCollectionSelection() {
        this.recipeCollectionService.getRandomizedRecipeCollectionSelection().pipe(
            tap(response => {
                this.cookbooks = response;
            }),
            catchError(() => {
                this.errorMessage = "Could not find our cookbooks...";
                this.error = true;
                // Handle the error here
                return of(null);
            })
        ).subscribe();
    }
}
