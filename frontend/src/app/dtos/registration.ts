import {Region} from "../shared/region";

export class Registration {

    /*email: string;
    username: string;
    password: string;
    region: Region;
    postalCode: string;*/
    constructor(
        public email: string,
        public username: string,
        public password: string,
        public region: Region,
        public postalCode: string
    ) {

    }
    toString(): string {
        return `object of type Registration:
         email: ${this.email},
         username: ${this.username},
         password: ${this.password},
         region: ${this.region.toString()},
         postalCode: ${this.postalCode},
         `;
    }
}

