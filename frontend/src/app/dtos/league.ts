import {Region} from "../shared/region";

export class League {

    constructor(
        public gamemode: string,
        public challengeDuration: string,
        public region: Region
    ) {

    }
}
