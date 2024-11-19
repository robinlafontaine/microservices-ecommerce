FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json .
RUN npm install
COPY . .

ARG PUBLIC_STRIPE_KEY
ENV PUBLIC_STRIPE_KEY=${PUBLIC_STRIPE_KEY}

RUN npm run build
RUN npm prune --production

FROM node:18-alpine
LABEL maintainer1="Robin Lafontaine"
LABEL maintainer2="Bastien Ubassy"
WORKDIR /app
COPY --from=builder /app/build build/
COPY --from=builder /app/node_modules node_modules/
COPY package.json .
EXPOSE 3000
ENV NODE_ENV=production
CMD [ "node", "build" ]