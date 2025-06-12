-- schema.sql
CREATE TABLE IF NOT EXISTS PERSONS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS LOCATIONS (
    reference_id BIGINT NOT NULL,
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    FOREIGN KEY (reference_id) REFERENCES PERSONS(id)
);

-- Indexes for optimizing nearby people queries
-- Composite index for bounding box queries (latitude range)
CREATE INDEX IF NOT EXISTS idx_locations_lat_lon ON LOCATIONS(latitude, longitude);

-- Index for reference_id lookups
CREATE INDEX IF NOT EXISTS idx_locations_reference_id ON LOCATIONS(reference_id);

-- Additional index for longitude-based queries if needed
CREATE INDEX IF NOT EXISTS idx_locations_lon_lat ON LOCATIONS(longitude, latitude); 