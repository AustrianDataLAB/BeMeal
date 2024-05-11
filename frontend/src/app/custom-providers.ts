import {ConfigService} from "@services/config.service";
import {configServiceFactory} from "@services/config.service.factory";
import {APP_INITIALIZER} from "@angular/core";

export const customProviders = [{
    provide: APP_INITIALIZER,
    useFactory: configServiceFactory,
    multi: true,
    deps: [ConfigService]
}]
