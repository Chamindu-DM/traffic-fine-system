-- Database initialization script for Docker Compose MySQL
-- Each microservice gets its own database

CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS fines_db;
CREATE DATABASE IF NOT EXISTS payments_db;
CREATE DATABASE IF NOT EXISTS notifications_db;
CREATE DATABASE IF NOT EXISTS reporting_db;

-- Grant full privileges to the application user on all databases
GRANT ALL PRIVILEGES ON auth_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON fines_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON payments_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON notifications_db.* TO 'root'@'%';
GRANT ALL PRIVILEGES ON reporting_db.* TO 'root'@'%';
FLUSH PRIVILEGES;
