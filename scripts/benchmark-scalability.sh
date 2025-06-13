#!/bin/bash

export PGPASSWORD=

# Comprehensive Scalability Benchmark Script
# Tests 1K, 10K, 1M, and 10M records with proper cleanup and table-formatted results

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
POSTGRES_HOST="localhost"
POSTGRES_PORT="23798"
DATABASE_NAME="personsfinder"
USERNAME="postgres"

# Test configurations
DATASET_SIZES=(1000 10000 1000000 10000000)  # 1K, 10K, 1M, 10M
RADII=(1 5 10 50 100)  # km
PAGE_SIZES=(10 50 100)

# Results file
RESULTS_FILE="benchmark-results-$(date +%Y%m%d-%H%M%S).csv"
REPORT_FILE="scalability-benchmark-report-$(date +%Y%m%d-%H%M%S).md"

# Function to log messages
log() {
    echo -e "${GREEN}[$(date '+%H:%M:%S')]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[$(date '+%H:%M:%S')] WARNING:${NC} $1"
}

error() {
    echo -e "${RED}[$(date '+%H:%M:%S')] ERROR:${NC} $1"
    exit 1
}

# Function to stop application
stop_application() {
    log "Stopping application..."
    lsof -i :8080 | grep LISTEN | awk '{print $2}' | xargs kill -9 2>/dev/null || true
    sleep 2
}

# Function to clear database
clear_database() {
    log "Clearing database..."
    psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$USERNAME" -d "$DATABASE_NAME" -c "TRUNCATE TABLE locations, persons RESTART IDENTITY CASCADE;" > /dev/null 2>&1 || true
    log "Database cleared"
}

# Function to seed data
seed_data() {
    local dataset_size=$1
    log "Seeding $dataset_size records..."
    
    START_TIME=$(date +%s)
    java -jar build/libs/PersonsFinder-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres --seeding.enabled=true --seeding.personCount=$dataset_size --seeding.distribution=WORLDWIDE > /dev/null 2>&1
    END_TIME=$(date +%s)
    SEEDING_DURATION=$((END_TIME - START_TIME))
    
    # Ensure minimum duration of 1 second to avoid division by zero
    if [ "$SEEDING_DURATION" -eq 0 ]; then
        SEEDING_DURATION=1
    fi
    
    log "Seeding completed in ${SEEDING_DURATION} seconds"
    echo "$SEEDING_DURATION"
}

# Function to start application
start_application() {
    log "Starting application..."
    java -jar build/libs/PersonsFinder-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres > /dev/null 2>&1 &
    APP_PID=$!
    
    # Wait for application to start
    for i in {1..15}; do
        if curl -s "http://localhost:8080/api/v1/persons/nearby?lat=0&lon=0&radiusKm=1&pageSize=1" > /dev/null 2>&1; then
            log "Application started (attempt $i/15)"
            break
        fi
        if [ $i -eq 15 ]; then
            error "Application failed to start"
        fi
        sleep 2
    done
    
    echo $APP_PID
}

# Function to run benchmark for a dataset
run_benchmark() {
    local dataset_size=$1
    local app_pid=$2
    
    log "Running benchmarks for $dataset_size records..."
    
    # Test coordinates (New York City area)
    local lat=40.7128
    local lon=-74.0060
    
    for radius in "${RADII[@]}"; do
        for page_size in "${PAGE_SIZES[@]}"; do
            # Measure response time
            local start_time=$(date +%s%N)
            
            local response=$(curl -s -w "\n%{http_code}" \
                "http://localhost:8080/api/v1/persons/nearby?lat=$lat&lon=$lon&radiusKm=$radius&pageSize=$page_size")
            
            local end_time=$(date +%s%N)
            
            # Extract HTTP status code (last line)
            local http_code=$(echo "$response" | tail -n1)
            
            # Extract response body (all lines except last)
            local response_body=$(echo "$response" | head -n -1)
            
            # Calculate duration in milliseconds
            local duration=$(( (end_time - start_time) / 1000000 ))
            
            # Extract result count from JSON response
            local result_count=$(echo "$response_body" | grep -o '"totalItems":[0-9]*' | grep -o '[0-9]*' || echo "0")
            
            # Write to CSV
            echo "$dataset_size,$radius,$page_size,$duration,$result_count,$http_code" >> "$RESULTS_FILE"
            
            log "  Radius: ${radius}km, Page: $page_size, Time: ${duration}ms, Results: $result_count"
            
            # Small delay between requests
            sleep 0.5
        done
    done
    
    # Stop application
    kill $app_pid 2>/dev/null || true
    wait $app_pid 2>/dev/null || true
}

# Function to generate report
generate_report() {
    log "Generating benchmark report..."
    
    cat > "$REPORT_FILE" << EOF
# Scalability Benchmark Report

## Test Configuration
- **Database**: PostgreSQL (port 23798)
- **Test Coordinates**: New York City (40.7128, -74.0060)
- **Radii Tested**: 1km, 5km, 10km, 50km, 100km
- **Page Sizes**: 10, 50, 100
- **Dataset Sizes**: 1K, 10K, 1M, 10M records

## Performance Results

### Response Time Matrix (milliseconds)

| Dataset | Radius | Page Size | 1km | 5km | 10km | 50km | 100km |
|---------|--------|-----------|-----|-----|------|------|-------|
EOF

    # Generate table rows for each dataset and page size
    for dataset_size in "${DATASET_SIZES[@]}"; do
        for page_size in "${PAGE_SIZES[@]}"; do
            echo -n "| $dataset_size | $page_size | " >> "$REPORT_FILE"
            
            for radius in "${RADII[@]}"; do
                # Extract response time from CSV
                local response_time=$(grep "^$dataset_size,$radius,$page_size," "$RESULTS_FILE" | cut -d',' -f4)
                echo -n "$response_time | " >> "$REPORT_FILE"
            done
            echo "" >> "$REPORT_FILE"
        done
    done

    cat >> "$REPORT_FILE" << EOF

### Result Count Matrix

| Dataset | Radius | Page Size | 1km | 5km | 10km | 50km | 100km |
|---------|--------|-----------|-----|-----|------|------|-------|
EOF

    # Generate table rows for result counts
    for dataset_size in "${DATASET_SIZES[@]}"; do
        for page_size in "${PAGE_SIZES[@]}"; do
            echo -n "| $dataset_size | $page_size | " >> "$REPORT_FILE"
            
            for radius in "${RADII[@]}"; do
                # Extract result count from CSV
                local result_count=$(grep "^$dataset_size,$radius,$page_size," "$RESULTS_FILE" | cut -d',' -f5)
                echo -n "$result_count | " >> "$REPORT_FILE"
            done
            echo "" >> "$REPORT_FILE"
        done
    done

    cat >> "$REPORT_FILE" << EOF

## Performance Analysis

### Seeding Performance
EOF

    # Add seeding performance data
    if [ -f "seeding-times.txt" ]; then
        echo "| Dataset Size | Seeding Time (seconds) | Records/Second |" >> "$REPORT_FILE"
        echo "|--------------|----------------------|----------------|" >> "$REPORT_FILE"
        while IFS=',' read -r size time; do
            # Validate that time is not zero or empty
            if [ -n "$time" ] && [ "$time" -gt 0 ] 2>/dev/null; then
                local rate=$((size / time))
                echo "| $size | $time | $rate |" >> "$REPORT_FILE"
            else
                echo "| $size | $time | N/A |" >> "$REPORT_FILE"
            fi
        done < seeding-times.txt
    fi

    cat >> "$REPORT_FILE" << EOF

### Key Observations
1. **Response Time Scaling**: How response times scale with dataset size
2. **Radius Impact**: How search radius affects performance
3. **Page Size Impact**: How pagination affects response times
4. **Database Performance**: Overall system performance under load

## Test Environment
- **Date**: $(date)
- **Database**: PostgreSQL $(psql -h "$POSTGRES_HOST" -p "$POSTGRES_PORT" -U "$USERNAME" -d "$DATABASE_NAME" -t -c "SELECT version();" | xargs)
- **Results File**: $RESULTS_FILE

EOF

    log "Report generated: $REPORT_FILE"
}

# Main execution
main() {
    log "Starting comprehensive scalability benchmark"
    log "Results will be saved to: $RESULTS_FILE"
    log "Report will be saved to: $REPORT_FILE"
    
    # Initialize results file
    echo "Dataset Size,Radius (km),Page Size,Response Time (ms),Total Results,HTTP Status" > "$RESULTS_FILE"
    
    # Initialize seeding times file
    echo "" > seeding-times.txt
    
    for dataset_size in "${DATASET_SIZES[@]}"; do
        log "=== Testing $dataset_size records ==="
        
        # Stop any running application
        stop_application
        
        # Clear database
        clear_database
        
        # Seed data and record time
        seeding_time=$(seed_data $dataset_size)
        echo "$dataset_size,$seeding_time" >> seeding-times.txt
        
        # Start application
        app_pid=$(start_application)
        
        # Run benchmarks
        run_benchmark $dataset_size $app_pid
        
        log "Completed testing for $dataset_size records"
        echo ""
    done
    
    # Generate final report
    generate_report
    
    log "Benchmark completed!"
    log "Results: $RESULTS_FILE"
    log "Report: $REPORT_FILE"
    
    # Display summary
    echo ""
    echo -e "${GREEN}🎉 Benchmark completed successfully!${NC}"
    echo "📊 Results: $RESULTS_FILE"
    echo "📋 Report: $REPORT_FILE"
}

# Run main function
main "$@" 