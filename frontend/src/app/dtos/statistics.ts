

export interface HeatMap {
    entries: {
        id: number, rate: number
    }[],
    type: HeatMapType,
    relative: boolean
}

export enum HeatMapType {
    RANDOM = 'RANDOM', USER_BASE = 'USER_BASE', SUBMISSIONS = 'SUBMISSIONS'
}

export function heatMapTypeToString(type: HeatMapType) {
    switch (type) {
        case HeatMapType.RANDOM:
            return 'Random';
        case HeatMapType.USER_BASE:
            return 'User Base';
        case HeatMapType.SUBMISSIONS:
            return 'Submissions';
    }
}
