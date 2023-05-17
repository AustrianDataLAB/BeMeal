

export interface HeatMap {
    entries: {
        id: number, rate: number
    }[],
    type: HeatMapType,
    relative: boolean
}

export enum HeatMapType {
    RANDOM = 'RANDOM'
}
