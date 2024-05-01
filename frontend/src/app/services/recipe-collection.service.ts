import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Globals} from "../shared/globals";
import {Observable} from "rxjs";
import {RecipeCollection} from "../dtos/recipe-collection";

@Injectable({
    providedIn: 'root'
})
export class RecipeCollectionService {
    private httpClient = inject(HttpClient);
    private globals = inject(Globals);
    private baseUri: string = this.globals.backendUri + '/recipeCollection';

    getRandomizedRecipeCollectionSelection(): Observable<RecipeCollection[]> {
        return this.httpClient.get<RecipeCollection[]>(this.baseUri);
    }
}
