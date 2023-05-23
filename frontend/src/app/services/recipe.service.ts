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
}
