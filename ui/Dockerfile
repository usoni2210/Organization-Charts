FROM php:5.6-apache

RUN apt-get update && \
    docker-php-ext-install json 

WORKDIR /var/www/html

COPY . . 
