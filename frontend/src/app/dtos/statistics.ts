

export interface HeatMap {
    data: Map<number, number>,
    type: HeatMapTye,
    relative: boolean
}

export enum HeatMapTye {
    RANDOM= "RANDOM"
}
