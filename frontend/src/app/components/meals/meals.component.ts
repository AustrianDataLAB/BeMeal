import {Component, OnInit} from '@angular/core';
import {RecipeCollection} from "../../dtos/recipe-collection";
import {Router} from "@angular/router";
import {RecipeCollectionService} from "../../services/recipe-collection.service";
import {catchError, tap} from "rxjs/operators";
import {of} from "rxjs";
import {MatChipListboxChange} from "@angular/material/chips";
import {RecipeService} from "../../services/recipe.service";
import {Recipe} from "../../dtos/recipe";
import {Pagination} from "../../dtos/pagination";

@Component({
    selector: 'app-meals',
    templateUrl: './meals.component.html',
    styleUrls: ['./meals.component.scss']
})
export class MealsComponent implements OnInit {
    error = false;
    errorMessage = '';
    cookbooks: RecipeCollection[];
    recipes: Recipe[];
    selectedCookbooks: string[];

    pagination: Pagination;
    pageIndex = 1;
    totalElements = 0;

    constructor(private router: Router, private recipeCollectionService: RecipeCollectionService, private recipeService: RecipeService) {
    }

    ngOnInit(): void {
        this.getRandomizedRecipeCollectionSelection();
    }

    getRandomizedRecipeCollectionSelection() {
        this.recipeCollectionService.getRandomizedRecipeCollectionSelection().pipe(
            tap(response => {
                this.cookbooks = response;
                this.recipes = [];
                this.pageIndex = 1;
                this.totalElements = 0;
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
        this.selectedCookbooks = event.value;
        this.recipeService.getRecipesFromCollections(this.selectedCookbooks, null).pipe(
            tap(response => {
                this.pagination = response;
                this.recipes = response.content;
                this.totalElements = this.pagination.totalElements;
            }),
            catchError(() => {
                this.recipes = [];
                // TODO: implement error handling here
                return of(null);
            })
        ).subscribe();
    }

    onTableDataChange(event: number) {
        this.pageIndex = event;
        this.recipeService.getRecipesFromCollections(this.selectedCookbooks, this.pageIndex - 1).pipe(
            tap(response => {
                this.pagination = response;
                this.recipes = response.content;
            }),
            catchError(() => {
                this.recipes = [];
                // TODO: implement error handling here
                return of(null);
            })
        ).subscribe();
    }
}
