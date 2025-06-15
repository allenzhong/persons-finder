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
| 1000 | 10 | 13 | 28 | 22 | 26 | 22 | 
| 1000 | 50 | 28 | 25 | 23 | 30 | 23 | 
| 1000 | 100 | 22 | 28 | 17 | 25 | 20 | 
| 10000 | 10 | 14 | 31 | 37 | 30 | 37 | 
| 10000 | 50 | 25 | 17 | 35 | 36 | 38 | 
| 10000 | 100 | 25 | 27 | 37 | 36 | 31 | 
| 100000 | 10 | 15 | 26 | 35 | 47 | 60 | 
| 100000 | 50 | 21 | 22 | 20 | 62 | 54 | 
| 100000 | 100 | 25 | 21 | 31 | 51 | 54 | 
| 1000000 | 10 | 17 | 71 | 87 | 159 | 161 | 
| 1000000 | 50 | 36 | 26 | 68 | 158 | 197 | 
| 1000000 | 100 | 30 | 42 | 40 | 157 | 151 | 
| 10000000 | 10 | 30 | 213 | 328 | 1223 | 1286 | 
| 10000000 | 50 | 40 | 218 | 353 | 1183 | 1293 | 
| 10000000 | 100 | 45 | 150 | 361 | 1178 | 1278 | 
| 100000000 | 10 | 508 | 5320 | 8500 | 21640 | 25459 | 
| 100000000 | 50 | 58 | 801 | 4999 | 18406 | 25749 | 
| 100000000 | 100 | 69 | 4586 | 2816 | 11315 | 13901 | 

### Result Count Matrix

| Dataset | Radius | Page Size | 1km | 5km | 10km | 50km | 100km |
|---------|--------|-----------|-----|-----|------|------|-------|
| 1000 | 10 | 0 | 0 | 6 | 92 | 102 | 
| 1000 | 50 | 0 | 0 | 6 | 92 | 102 | 
| 1000 | 100 | 0 | 0 | 6 | 92 | 102 | 
| 10000 | 10 | 0 | 6 | 39 | 909 | 1026 | 
| 10000 | 50 | 0 | 6 | 39 | 909 | 1026 | 
| 10000 | 100 | 0 | 6 | 39 | 909 | 1026 | 
| 100000 | 10 | 5 | 88 | 426 | 9095 | 10134 | 
| 100000 | 50 | 5 | 88 | 426 | 9095 | 10134 | 
| 100000 | 100 | 5 | 88 | 426 | 9095 | 10134 | 
| 1000000 | 10 | 47 | 1049 | 4269 | 0 | 99958 | 
| 1000000 | 50 | 47 | 1049 | 4269 | 0 | 99958 | 
| 1000000 | 100 | 47 | 1049 | 4269 | 90086 | 99958 | 
| 10000000 | 10 | 430 | 0 | 0 | 0 | 999002 | 
| 10000000 | 50 | 430 | 10692 | 0 | 0 | 999002 | 
| 10000000 | 100 | 430 | 10692 | 42905 | 0 | 999002 | 
| 100000000 | 10 | 0 | 0 | 0 | 0 | 10001387 | 
| 100000000 | 50 | 4176 | 0 | 0 | 0 | 10001387 | 
| 100000000 | 100 | 4176 | 0 | 0 | 0 | 10001387 | 

## Performance Analysis

### Seeding Performance
| Dataset Size | Seeding Time (seconds) | Records/Second |
|--------------|----------------------|----------------|
|  |  | N/A |
| 1000 | 2 | 500 |
| 10000 | 4 | 2500 |
| 100000 | 22 | 4545 |
| 1000000 | 192 | 5208 |
| 10000000 | 2515 | 3976 |
| 100000000 | 53455 | 1870 |

### Key Observations
1. **Response Time Scaling**: How response times scale with dataset size
2. **Radius Impact**: How search radius affects performance
3. **Page Size Impact**: How pagination affects response times
4. **Database Performance**: Overall system performance under load

## Test Environment
- **Date**: Mon Jun 16 06:03:26 NZST 2025
- **Database**: PostgreSQL PostgreSQL 15.3 (Debian 15.3-1.pgdg120+1) on aarch64-unknown-linux-gnu, compiled by gcc (Debian 12.2.0-14) 12.2.0, 64-bit
- **Results File**: benchmark/benchmark-results-20250615-142210.csv

