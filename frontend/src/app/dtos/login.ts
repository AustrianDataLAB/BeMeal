import {Region} from "../shared/region";

export class Login {

    constructor(
        public username: string,
        public password: string
    ) {}
    toString(): string {
        return `object of type Login:
         username: ${this.username},
         password: ${this.password}
         `;
    }

}

