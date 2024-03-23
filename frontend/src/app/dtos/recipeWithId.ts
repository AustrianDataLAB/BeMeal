import {Ingridient} from "./ingridient";

export class RecipeWithId {
    constructor(
        public name: string,
        public recipeId: string,
        public description: string,
        public preparationTime: number,
        public cookingTime: number,
        public skillLevel: string,
        public ingredients: Ingridient[],
        public picture: string
    ) {
    }
}
