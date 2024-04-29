FROM mcr.microsoft.com/playwright:v1.43.1-jammy

WORKDIR /test
# todo do not use root
#USER pwuser

ENV PATH /test/node_modules/.bin:$PATH

COPY package.json .
COPY package-lock.json .
COPY tests ./tests/

RUN npm install


CMD ["npm", "test"]
