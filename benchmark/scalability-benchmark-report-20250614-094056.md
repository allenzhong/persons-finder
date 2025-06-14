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
| 1000 | 10 | 12 | 30 | 27 | 30 | 25 | 
| 1000 | 50 | 29 | 15 | 14 | 31 | 25 | 
| 1000 | 100 | 18 | 21 | 18 | 32 | 23 | 
| 10000 | 10 | 13 | 14 | 27 | 22 | 34 | 
| 10000 | 50 | 21 | 24 | 19 | 28 | 37 | 
| 10000 | 100 | 26 | 21 | 14 | 32 | 29 | 
| 100000 | 10 | 12 | 25 | 32 | 72 | 78 | 
| 100000 | 50 | 24 | 30 | 31 | 76 | 64 | 
| 100000 | 100 | 15 | 30 | 24 | 60 | 75 | 
| 1000000 | 10 | 16 | 85 | 115 | 349 | 330 | 
| 1000000 | 50 | 29 | 40 | 46 | 290 | 340 | 
| 1000000 | 100 | 15 | 28 | 55 | 297 | 332 | 
| 10000000 | 10 | 35 | 191 | 315 | 2466 | 2699 | 
| 10000000 | 50 | 41 | 138 | 408 | 2417 | 2671 | 
| 10000000 | 100 | 24 | 140 | 407 | 2386 | 2730 | 
| 100000000 | 10 | 806 | 7118 | 7347 | 36666 | 39346 | 
| 100000000 | 50 | 89 | 736 | 5053 | 27031 | 36441 | 
| 100000000 | 100 | 68 | 719 | 3364 | 29419 | 49179 | 

### Result Count Matrix

| Dataset | Radius | Page Size | 1km | 5km | 10km | 50km | 100km |
|---------|--------|-----------|-----|-----|------|------|-------|
| 1000 | 10 | 0 | 0 | 1 | 70 | 93 | 
| 1000 | 50 | 0 | 0 | 1 | 70 | 93 | 
| 1000 | 100 | 0 | 0 | 1 | 70 | 93 | 
| 10000 | 10 | 0 | 7 | 33 | 744 | 962 | 
| 10000 | 50 | 0 | 7 | 33 | 744 | 962 | 
| 10000 | 100 | 0 | 7 | 33 | 744 | 962 | 
| 100000 | 10 | 1 | 91 | 335 | 7830 | 10131 | 
| 100000 | 50 | 1 | 91 | 335 | 7830 | 10131 | 
| 100000 | 100 | 1 | 91 | 335 | 7830 | 10131 | 
| 1000000 | 10 | 28 | 849 | 3394 | 77354 | 99676 | 
| 1000000 | 50 | 28 | 849 | 3394 | 77354 | 99676 | 
| 1000000 | 100 | 28 | 849 | 3394 | 77354 | 99676 | 
| 10000000 | 10 | 365 | 8291 | 33516 | 778350 | 1002452 | 
| 10000000 | 50 | 365 | 8291 | 33516 | 778350 | 1002452 | 
| 10000000 | 100 | 365 | 8291 | 33516 | 778350 | 1002452 | 
| 100000000 | 10 | 3370 | 83812 | 334567 | 7771979 | 10003143 | 
| 100000000 | 50 | 3370 | 83812 | 334567 | 7771979 | 10003143 | 
| 100000000 | 100 | 3370 | 83812 | 334567 | 7771979 | 10003143 | 

## Performance Analysis

### Seeding Performance
| Dataset Size | Seeding Time (seconds) | Records/Second |
|--------------|----------------------|----------------|
|  |  | N/A |
| 1000 | 2 | 500 |
| 10000 | 4 | 2500 |
| 100000 | 19 | 5263 |
| 1000000 | 171 | 5847 |
| 10000000 | 2272 | 4401 |
| 100000000 | 41376 | 2416 |

### Key Observations
1. **Response Time Scaling**: How response times scale with dataset size
2. **Radius Impact**: How search radius affects performance
3. **Page Size Impact**: How pagination affects response times
4. **Database Performance**: Overall system performance under load

## Test Environment
- **Date**: Sat Jun 14 21:58:28 NZST 2025
- **Database**: PostgreSQL PostgreSQL 15.3 (Debian 15.3-1.pgdg120+1) on aarch64-unknown-linux-gnu, compiled by gcc (Debian 12.2.0-14) 12.2.0, 64-bit
- **Results File**: benchmark/benchmark-results-20250614-094056.csv

