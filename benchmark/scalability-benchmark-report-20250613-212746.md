# Scalability Benchmark Report

## Test Configuration
- **Database**: PostgreSQL (port 23798)
- **Test Coordinates**: New York City (40.7128, -74.0060)
- **Radii Tested**: 1km, 5km, 10km, 50km, 100km
- **Page Sizes**: 10, 50, 100
- **Dataset Sizes**: 1K, 10K, 100K, 1M, 10M records
- **Data Distribution**: URBAN_CENTERS (concentrated around major cities including NYC)

## Performance Results

### Response Time Matrix (milliseconds)

| Dataset | Radius | Page Size | 1km | 5km | 10km | 50km | 100km |
|---------|--------|-----------|-----|-----|------|------|-------|
| 1000 | 10 | 12 | 28 | 23 | 31 | 24 | 
| 1000 | 50 | 27 | 25 | 25 | 26 | 22 | 
| 1000 | 100 | 27 | 22 | 24 | 30 | 30 | 
| 10000 | 10 | 13 | 23 | 30 | 39 | 41 | 
| 10000 | 50 | 19 | 30 | 26 | 26 | 41 | 
| 10000 | 100 | 13 | 14 | 31 | 35 | 36 | 
| 100000 | 10 | 13 | 16 | 34 | 84 | 93 | 
| 100000 | 50 | 28 | 17 | 19 | 75 | 79 | 
| 100000 | 100 | 30 | 57 | 36 | 78 | 75 | 
| 1000000 | 10 | 15 | 79 | 106 | 317 | 264 | 
| 1000000 | 50 | 23 | 37 | 51 | 269 | 256 | 
| 1000000 | 100 | 28 | 50 | 57 | 244 | 279 | 
| 10000000 | 10 | 45 | 157 | 349 | 2492 | 2753 | 
| 10000000 | 50 | 36 | 151 | 398 | 2475 | 2797 | 
| 10000000 | 100 | 43 | 106 | 392 | 2556 | 2640 | 

### Result Count Matrix

| Dataset | Radius | Page Size | 1km | 5km | 10km | 50km | 100km |
|---------|--------|-----------|-----|-----|------|------|-------|
| 1000 | 10 | 0 | 0 | 1 | 73 | 98 | 
| 1000 | 50 | 0 | 0 | 1 | 73 | 98 | 
| 1000 | 100 | 0 | 0 | 1 | 73 | 98 | 
| 10000 | 10 | 0 | 7 | 36 | 784 | 1021 | 
| 10000 | 50 | 0 | 7 | 36 | 784 | 1021 | 
| 10000 | 100 | 0 | 7 | 36 | 784 | 1021 | 
| 100000 | 10 | 3 | 103 | 339 | 7808 | 9929 | 
| 100000 | 50 | 3 | 103 | 339 | 7808 | 9929 | 
| 100000 | 100 | 3 | 103 | 339 | 7808 | 9929 | 
| 1000000 | 10 | 30 | 820 | 3372 | 77716 | 99945 | 
| 1000000 | 50 | 30 | 820 | 3372 | 77716 | 99945 | 
| 1000000 | 100 | 30 | 820 | 3372 | 77716 | 99945 | 
| 10000000 | 10 | 307 | 8424 | 33581 | 778224 | 1001227 | 
| 10000000 | 50 | 307 | 8424 | 33581 | 778224 | 1001227 | 
| 10000000 | 100 | 307 | 8424 | 33581 | 778224 | 1001227 | 

## Performance Analysis

### Seeding Performance
| Dataset Size | Seeding Time (seconds) | Records/Second |
|--------------|----------------------|----------------|
|  |  | N/A |
| 1000 | 2 | 500 |
| 10000 | 4 | 2500 |
| 100000 | 19 | 5263 |
| 1000000 | 175 | 5714 |
| 10000000 | 2376 | 4208 |

### Key Observations
1. **Response Time Scaling**: How response times scale with dataset size
2. **Radius Impact**: How search radius affects performance
3. **Page Size Impact**: How pagination affects response times
4. **Database Performance**: Overall system performance under load

## Test Environment
- **Date**: Fri Jun 13 22:12:15 NZST 2025
- **Database**: PostgreSQL PostgreSQL 15.3 (Debian 15.3-1.pgdg120+1) on aarch64-unknown-linux-gnu, compiled by gcc (Debian 12.2.0-14) 12.2.0, 64-bit
- **Results File**: benchmark-results-20250613-212746.csv

