import {Region} from "../shared/region";
import {GameMode} from "../shared/game-mode";

export class League {

    constructor(
        public name: string,
        public gameMode: GameMode,
        public challengeDuration: number,
        public region: Region
    ) {

    }
}
