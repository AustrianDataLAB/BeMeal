FROM --platform=$BUILDPLATFORM node:21-alpine AS build

WORKDIR /app

ENV PATH /app/node_modules/.bin:$PATH
RUN npm install -g @angular/cli

COPY package.json /app/package.json
COPY . .

RUN --mount=type=cache,target=/app/node_modules \
    npm install && \
    ng build --output-path=dist

FROM --platform=$TARGETPLATFORM nginx:1.25-alpine

COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /bemeal-frontend

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]