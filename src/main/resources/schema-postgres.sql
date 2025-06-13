-- PostgreSQL optimized schema for large-scale geo-queries
CREATE TABLE IF NOT EXISTS persons (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS locations (
    reference_id BIGINT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (reference_id) REFERENCES persons(id) ON DELETE CASCADE
);

-- Optimized indexes for geo-queries
CREATE INDEX IF NOT EXISTS idx_locations_lat_lon 
    ON locations(latitude, longitude);

CREATE INDEX IF NOT EXISTS idx_locations_lat 
    ON locations(latitude);

CREATE INDEX IF NOT EXISTS idx_locations_lon 
    ON locations(longitude);

CREATE INDEX IF NOT EXISTS idx_locations_ref_id 
    ON locations(reference_id);

-- Composite index for bounding box queries (most important for performance)
CREATE INDEX IF NOT EXISTS idx_locations_geo_bbox 
    ON locations(latitude, longitude, reference_id);

-- Partial indexes for common query patterns
CREATE INDEX IF NOT EXISTS idx_locations_northern_hemisphere 
    ON locations(latitude, longitude) 
    WHERE latitude > 0;

CREATE INDEX IF NOT EXISTS idx_locations_southern_hemisphere 
    ON locations(latitude, longitude) 
    WHERE latitude < 0;

-- Statistics for query planner
ANALYZE persons;
ANALYZE locations; 