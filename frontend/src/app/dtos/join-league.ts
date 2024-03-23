import {Region} from "../shared/region";
import {GameMode} from "../shared/game-mode";

export class JoinLeague {

    constructor(
        public leagueId: number | null,
        public hiddenIdentifier: string | null,
    ) {

    }
}

