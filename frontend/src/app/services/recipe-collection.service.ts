import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Globals} from "../shared/globals";
import {Observable} from "rxjs";
import {RecipeCollection} from "../dtos/recipe-collection";

@Injectable({
    providedIn: 'root'
})
export class RecipeCollectionService {
    private baseUri: string = this.globals.backendUri + '/recipeCollection';

    constructor(private httpClient: HttpClient, private globals: Globals) {}

    getRandomizedRecipeCollectionSelection(): Observable<RecipeCollection[]> {
        return this.httpClient.get<RecipeCollection[]>(this.baseUri);
    }
}
