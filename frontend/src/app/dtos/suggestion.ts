import {Recipe} from "./recipe";

export class Suggestion {

    constructor(
        public given: Recipe[],
        public suggestions: Recipe[]
    ) {

    }
}
