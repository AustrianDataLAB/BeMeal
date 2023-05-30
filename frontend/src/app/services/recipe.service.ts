import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Globals} from "../shared/globals";
import {Observable, of} from "rxjs";
import {Recipe} from "../dtos/recipe";

@Injectable({
    providedIn: 'root'
})
export class RecipeService {
    private baseUri: string = this.globals.backendUri + '/recipe';

    constructor(private httpClient: HttpClient, private globals: Globals) {
    }

    getRecipesFromCollections(cookBooks: string[], page: number | null): Observable<any> {
        let uri = this.baseUri + "/collections";
        if (cookBooks.length > 0) {
            uri += "?name="
            for (let i = 0; i < cookBooks.length; i++) {
                uri += cookBooks[i];
                if (i != (cookBooks.length - 1))
                    uri += "&name=";
            }
            if (page != null) {
                uri += "&page=" + page;
            }
            return this.httpClient.get<Recipe[]>(uri);
        }
        return of(null);
    }

    getSuggestionFromRecipes(recipes: string[]): Observable<any> {
        let uri = this.baseUri + "/suggestion";
        if (recipes.length > 0) {
            uri += "?id="
            for (let i = 0; i < recipes.length; i++) {
                uri += recipes[i];
                if (i != (recipes.length - 1))
                    uri += "&id=";
            }
            return this.httpClient.get<Recipe[]>(uri);
        }
        return of(null);
    }
}
