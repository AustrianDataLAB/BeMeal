// import {Region} from "../shared/region";
import {GameMode} from "../shared/game-mode";

export class League {

    constructor(
        public id: number | null,
        public name: string,
        public gameMode: GameMode,
        public challengeDuration: number,
        public lastWinners: string[]
    ) {

    }
}

export interface LeagueSecrets {
    hiddenIdentifier: string
}
