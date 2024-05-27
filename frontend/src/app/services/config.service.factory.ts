import {ConfigService} from "@services/config.service";

export const configServiceFactory = (service : ConfigService) => {
    return () => service.loadEnv();
}
