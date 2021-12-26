FROM nginx:alpine

COPY docker/nginx.conf /etc/nginx/conf.d/default.conf

COPY docker/health.html /usr/share/nginx/html/health.html

COPY docker/start.html /usr/share/nginx/html/index.html

COPY dist/angular-gui /usr/share/nginx/html/web-gui/angular-gui

EXPOSE 8050
