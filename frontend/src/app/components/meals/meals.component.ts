import {Component} from '@angular/core';
import {RecipeCollection} from "../../dtos/recipe-collection";
import {Router} from "@angular/router";
import {RecipeCollectionService} from "../../services/recipe-collection.service";
import {catchError, tap} from "rxjs/operators";
import {of} from "rxjs";
import {MatChipListboxChange} from "@angular/material/chips";
import {RecipeService} from "../../services/recipe.service";
import {Recipe} from "../../dtos/recipe";

@Component({
    selector: 'app-meals',
    templateUrl: './meals.component.html',
    styleUrls: ['./meals.component.scss']
})
export class MealsComponent {
    error = false;
    errorMessage = '';
    cookbooks: RecipeCollection[];
    recipes: Recipe[];

    constructor(private router: Router, private recipeCollectionService: RecipeCollectionService, private recipeService: RecipeService) {
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

    onChipSelectionChange(event: MatChipListboxChange) {
        const selectedCookbooks = event.value;
        this.recipeService.getRecipesFromCollections(selectedCookbooks).pipe(
            tap(response => {
                this.recipes = response;
                console.log(response)
            }),
            catchError(() => {
                this.recipes = [];
                // TODO: implement error handling here
                return of(null);
            })
        ).subscribe();
    }
}
