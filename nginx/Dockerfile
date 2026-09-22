FROM nginx:alpine
RUN apk add --no-cache curl jq

RUN rm /etc/nginx/conf.d/default.conf

RUN rm -rf /usr/share/nginx/html/*

EXPOSE 80 443

CMD ["nginx", "-g", "daemon off;"]