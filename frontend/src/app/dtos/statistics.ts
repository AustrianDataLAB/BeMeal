

export interface HeatMap {
    entries: {
        id: number, rate: number
    }[],
    type: HeatMapType,
    relative: boolean
}

export enum HeatMapType {
    RANDOM = 'RANDOM', USER_BASE = 'USER_BASE', SUBMISSIONS = 'SUBMISSIONS', VOTES = 'VOTES', WINS = 'WINS', UP_VOTES = 'UP_VOTES', DOWN_VOTES = 'DOWN_VOTES', USERNAME = 'USERNAME'
}

export function heatMapTypeToString(type: HeatMapType) {
    switch (type) {
        case HeatMapType.RANDOM:
            return 'Random';
        case HeatMapType.USER_BASE:
            return 'User Base';
        case HeatMapType.SUBMISSIONS:
            return 'Submissions';
        case HeatMapType.VOTES:
            return 'Votes';
        case HeatMapType.WINS:
            return 'Wins';
        case HeatMapType.UP_VOTES:
            return 'Issued Up-Votes';
        case HeatMapType.DOWN_VOTES:
            return 'Issued Down-Votes';
        case HeatMapType.USERNAME:
            return 'Username Length';
    }
}
