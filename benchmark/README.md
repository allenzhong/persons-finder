# Benchmark Suite

This folder contains the scalability benchmark results and reports for the Persons Finder application.

## Overview

The benchmark suite tests the performance and scalability of the Persons Finder API across different dataset sizes, search radii, and pagination settings. It provides comprehensive insights into how the system performs under various load conditions.

## How to Run

### Prerequisites

1. **PostgreSQL Database**: Ensure PostgreSQL is running on port 23798
   ```bash
   # Check if PostgreSQL is running
   lsof -i :8080
   ```

2. **Application Built**: Make sure the application JAR is built
   ```bash
   ./gradlew build
   ```

3. **Database Access**: Ensure you have access to the `personsfinder` database
   ```bash
   psql -h localhost -p 23798 -U postgres -d personsfinder
   ```

### Running the Benchmark

Execute the benchmark script from the project root:

```bash
./scripts/benchmark-scalability.sh
```

The script will automatically:
- Test multiple dataset sizes (1K, 10K, 100K, 1M, 10M records)
- Test different search radii (1km, 5km, 10km, 50km, 100km)
- Test various page sizes (10, 50, 100)
- Generate comprehensive reports

### Expected Duration

- **1K records**: ~2-3 minutes
- **10K records**: ~3-5 minutes
- **100K records**: ~5-10 minutes
- **1M records**: ~15-30 minutes
- **10M records**: ~1-2 hours

**Total estimated time**: 2-3 hours

## Report Structure

### Generated Files

1. **`benchmark-results-YYYYMMDD-HHMMSS.csv`**: Raw benchmark data
2. **`scalability-benchmark-report-YYYYMMDD-HHMMSS.md`**: Formatted report
3. **`seeding-times.txt`**: Seeding performance data

### Report Sections

#### 1. Test Configuration
- Database details
- Test coordinates (NYC: 40.7128, -74.0060)
- Radii tested (1km, 5km, 10km, 50km, 100km)
- Page sizes (10, 50, 100)
- Dataset sizes (1K, 10K, 100K, 1M, 10M)
- Data distribution (URBAN_CENTERS)

#### 2. Response Time Matrix
Shows response times in milliseconds for each combination:
- **Rows**: Dataset size × Page size
- **Columns**: Search radius (1km, 5km, 10km, 50km, 100km)
- **Values**: Average response time in milliseconds

#### 3. Result Count Matrix
Shows the number of people found for each search:
- **Rows**: Dataset size × Page size
- **Columns**: Search radius (1km, 5km, 10km, 50km, 100km)
- **Values**: Total number of people found within the radius

#### 4. Seeding Performance
Performance metrics for data seeding:
- **Dataset Size**: Number of records seeded
- **Seeding Time**: Time taken in seconds
- **Records/Second**: Throughput rate

#### 5. Key Observations
Analysis of:
- Response time scaling with dataset size
- Impact of search radius on performance
- Effect of pagination on response times
- Overall database performance under load

## Interpreting Results

### Response Times
- **< 50ms**: Excellent performance
- **50-100ms**: Good performance
- **100-500ms**: Acceptable performance
- **> 500ms**: May need optimization

### Result Counts
- **0 results**: No people found within radius (expected for small radii)
- **1-10 results**: Few people nearby
- **10-100 results**: Moderate population density
- **> 100 results**: High population density

### Seeding Performance
- **> 1000 records/sec**: Excellent seeding performance
- **500-1000 records/sec**: Good seeding performance
- **< 500 records/sec**: May need optimization

## Performance Patterns to Look For

### 1. Linear Scaling
Response times should scale linearly with dataset size for well-optimized queries.

### 2. Radius Impact
Larger search radii should return more results but may take longer due to increased computational complexity.

### 3. Pagination Impact
Page size should have minimal impact on response time if pagination is implemented efficiently.

### 4. Database Performance
- Index utilization should keep response times consistent
- Connection pooling should handle concurrent requests efficiently
- Query optimization should minimize database load

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   ```bash
   # Check PostgreSQL status
   brew services list | grep postgresql
   # Restart if needed
   brew services restart postgresql
   ```

2. **Application Won't Start**
   ```bash
   # Check if port 8080 is available
   lsof -i :8080
   # Kill existing processes if needed
   kill -9 <PID>
   ```

3. **Seeding Takes Too Long**
   - Check database performance
   - Verify indexes are created
   - Monitor system resources

4. **No Results Found**
   - Verify data distribution (URBAN_CENTERS)
   - Check test coordinates
   - Ensure data was seeded successfully

### Debug Mode

The script includes debug output for the first test case. Look for:
- API response samples
- JSON parsing results
- Database verification messages

## File Naming Convention

- **Results**: `benchmark-results-YYYYMMDD-HHMMSS.csv`
- **Reports**: `scalability-benchmark-report-YYYYMMDD-HHMMSS.md`
- **Seeding**: `seeding-times.txt`

## Data Distribution

The benchmark uses **URBAN_CENTERS** distribution, which concentrates people around major cities including:
- New York City (40.7128, -74.0060)
- Los Angeles (34.0522, -118.2437)
- Chicago (41.8781, -87.6298)
- And other major urban centers

This ensures realistic population density patterns for testing.

## Performance Baselines

Based on typical results:

| Dataset Size | Expected Seeding Time | Expected Response Time (1km) |
|--------------|---------------------|------------------------------|
| 1K           | 2-3 seconds         | 10-30ms                      |
| 10K          | 3-5 seconds         | 15-40ms                      |
| 100K         | 5-10 seconds        | 20-60ms                      |
| 1M           | 15-30 seconds       | 50-150ms                     |
| 10M          | 2-5 minutes         | 100-500ms                    |

## Next Steps

After running benchmarks:
1. Analyze response time trends
2. Identify performance bottlenecks
3. Optimize database queries if needed
4. Consider scaling strategies for larger datasets
5. Document performance improvements

---

*Last updated: June 13, 2025* 