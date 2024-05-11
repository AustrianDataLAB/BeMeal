import {inject, Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {RecipeCollection} from "../dtos/recipe-collection";
import {ConfigService} from "@services/config.service";

@Injectable({
    providedIn: 'root'
})
export class RecipeCollectionService {
    private httpClient = inject(HttpClient);
    private configService = inject(ConfigService);
    private baseUri: string = this.configService.backendUri  + '/recipeCollection';

    getRandomizedRecipeCollectionSelection(): Observable<RecipeCollection[]> {
        return this.httpClient.get<RecipeCollection[]>(this.baseUri);
    }
}
