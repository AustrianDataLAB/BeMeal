import {Region} from "../shared/region";
import {Win} from "./wins";

export class Profile {

    constructor(
        public username: string,
        public email: string,
        public postalCode: number,
        // eslint-disable-next-line @typescript-eslint/ban-types
        public wins: Object,
        public region: Region,
        public registered: string,
    ) {}

}
