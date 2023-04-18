import {Region} from "../shared/region";

export class Profile {

    constructor(
        public username: string,
        public email: string,
        public postalCode: number,
        public wins: number,
        public region: Region,
        public registered: string,
    ) {}

}
