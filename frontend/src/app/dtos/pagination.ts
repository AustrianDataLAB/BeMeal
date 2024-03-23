export class Pagination {
    constructor(
        public totalElements: number,
        public size: number,
        public number: number,
        public pageNumber: number
    ) {
    }
}
