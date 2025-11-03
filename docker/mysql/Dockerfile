# Use official MySQL 8 Community Edition
FROM mysql:8.0

# Set environment variables for default DB and root user
ENV MYSQL_ROOT_PASSWORD=secretD3b
ENV MYSQL_DATABASE=users_db
# ENV MYSQL_USER=root
ENV MYSQL_PASSWORD=secretD3B

# Expose MySQL port
EXPOSE 3306

# Copy optional initialization scripts
# (Any .sql or .sh in this folder runs on first container start)
# COPY ./init.sql /docker-entrypoint-initdb.d/

# Switch back to root for development flexibility
USER root

# Default command (inherited from MySQL image)
CMD ["mysqld"]

