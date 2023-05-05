import {Ingridient} from "./ingridient";

export class ChallengeInfo {

    constructor(
        public name: string,
        public description: string,
        public endDate: Date,
        public ingredients: Ingridient[],
        public challengeId: number
    ) {

    }
}
