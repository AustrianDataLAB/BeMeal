FROM mcr.microsoft.com/playwright:v1.43.1-jammy

WORKDIR /playwright

ENV PATH /playwright/node_modules/.bin:$PATH

COPY package.json package-lock.json playwright.config.ts ./
VOLUME /playwright/playwright-report
VOLUME /playwright/tests

RUN npm ci


CMD ["npm", "test"]
