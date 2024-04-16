FROM --platform=$BUILDPLATFORM node:21-alpine AS build

WORKDIR /app

ENV PATH /app/node_modules/.bin:$PATH
RUN npm install -g @angular/cli

COPY package.json /app/package.json
COPY . .

RUN --mount=type=cache,target=/app/node_modules \
    npm install && \
    ng build --output-path=dist

FROM --platform=$TARGETPLATFORM nginxinc/nginx-unprivileged:1.25-alpine

USER nginx

COPY --chmod=0755 docker/read-environment.sh /docker-entrypoint.d/40-read-environment.sh
COPY docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /bemeal-frontend

EXPOSE 8080

CMD ["nginx", "-g", "daemon off;"]
