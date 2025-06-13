-- schema.sql
CREATE TABLE IF NOT EXISTS persons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS locations (
    reference_id BIGINT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    FOREIGN KEY (reference_id) REFERENCES persons(id)
);

DROP INDEX IF EXISTS idx_locations_lat_lon_ref;
CREATE INDEX idx_locations_lat_lon_ref
    ON LOCATIONS(latitude, longitude, reference_id);
