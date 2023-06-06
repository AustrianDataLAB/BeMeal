import { TestBed } from '@angular/core/testing';

import { InvalidTokenInterceptor } from './invalid-token.interceptor';

describe('InvalidTokenInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      InvalidTokenInterceptor
      ]
  }));

  it('should be created', () => {
    const interceptor: InvalidTokenInterceptor = TestBed.inject(InvalidTokenInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
