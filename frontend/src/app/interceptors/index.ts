/* "Barrel" of Http Interceptors */
import {HTTP_INTERCEPTORS} from '@angular/common/http';

import {AuthInterceptor} from './auth.interceptor';
import {InvalidTokenInterceptor} from './invalid-token.interceptor';

/** Http interceptor providers in outside-in order */
export const httpInterceptorProviders = [
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: InvalidTokenInterceptor, multi: true}
];
