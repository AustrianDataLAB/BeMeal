import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {RecipeService} from "../../services/recipe.service";
import {catchError, tap} from "rxjs/operators";
import {of} from "rxjs";
import {Pagination} from "../../dtos/pagination";
import {MatChipListboxChange} from "@angular/material/chips";
import {Recipe} from "../../dtos/recipe";

@Component({
    selector: 'app-search',
    templateUrl: './search.component.html',
    styleUrls: ['./search.component.scss']
})

export class SearchComponent {
    dietTypes = ['Vegetarian', 'Vegan', 'Dairy-free', 'Gluten-free', 'Healthy', 'Low-calorie', 'Low-sugar', 'Low-salt', 'Nut-free'];
    skillLevels = ['Easy', 'More effort', 'A challenge']

    recipes: Recipe[];
    searchString = "";
    sliderValue: number;
    selectedDietTypes: string[];
    selectedSkillLevels: string[];
    stateSearched = false;

    error = false;
    errorMessage = "";  // todo impl error handling
    pagination: Pagination;
    pageIndex = 1;
    totalElements = 0;

    constructor(private router: Router, private recipeService: RecipeService) {
        this.resetSearchParams();
    }

    findRecipesBySearchString() {
        this.resetSearchParams();
        this.stateSearched = true;
        this.recipeService.findRecipesBySearchString(this.searchString).pipe(
            tap(response => {
                this.pagination = response;
                this.recipes = response.content;
                this.totalElements = this.pagination.totalElements;
                this.pageIndex = this.pagination.pageNumber;
            }),
            catchError(() => {
                this.recipes = [];
                // TODO: implement error handling here
                return of(null);
            })
        ).subscribe();
    }

    findRecipesBySearchStringWithFilter() {
        this.recipeService.findRecipesBySearchStringWithFilter(this.searchString, this.selectedSkillLevels, this.sliderValueToSeconds(this.sliderValue), this.selectedDietTypes, null).pipe(
            tap(response => {
                this.pagination = response;
                this.recipes = response.content;
                this.totalElements = this.pagination.totalElements;
                this.pageIndex = this.pagination.pageNumber;
            }),
            catchError(() => {
                this.recipes = [];
                // TODO: implement error handling here
                return of(null);
            })
        ).subscribe();
    }

    private resetSearchParams() {
        this.recipes = [];
        this.sliderValue = 6;
        this.selectedDietTypes = [];
        this.selectedSkillLevels = [];
        this.pageIndex = 1;
    }

    onChipSelectionChange(event: MatChipListboxChange) {
        this.selectedDietTypes = event.value;
    }

    onSkillLevelChange(event: MatChipListboxChange) {
        this.selectedSkillLevels = event.value;
    }

    onTableDataChange(event: number) {
        this.pageIndex = event;
        this.recipeService.findRecipesBySearchStringWithFilter(this.searchString, this.selectedSkillLevels, this.sliderValueToSeconds(this.sliderValue), this.selectedDietTypes, this.pageIndex - 1).pipe(
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

    checkForNewSearch() {
        if (this.searchString.length == 0) {
            this.stateSearched = false;
            this.resetSearchParams();
        }
    }

    formatLabel(value: number): string {
        let seconds = 0;
        switch (value) {
            case 0:
                seconds = 1800;
                break;
            case 1:
                seconds = 3600;
                break;
            case 2:
                seconds = 7200;
                break;
            case 3:
                seconds = 10800;
                break;
            case 4:
                seconds = 18000;
                break;
            case 5:
                seconds = 28800;
                break;
            case 6:
                seconds = 36000;
                break;
        }
        if (seconds == 1800) {
            return '30min'
        } else if (seconds == 36000) {
            return '>10h'
        }
        return seconds / 3600 + 'h';
    }

    private sliderValueToSeconds(value: number): number {
        switch (value) {
            case 0:
                return 1800;
            case 1:
                return 3600;
            case 2:
                return 7200;
            case 3:
                return 10800;
            case 4:
                return 18000;
            case 5:
                return 28800;
        }
        // indicates that no upper time limit is set
        return 0;
    }
}
