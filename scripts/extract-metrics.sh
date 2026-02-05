#!/bin/bash

# extract-metrics.sh
# Extracts test and coverage metrics from JaCoCo and Surefire reports

set -e

echo "================================================"
echo "Extracting Test and Coverage Metrics"
echo "================================================"
echo ""

# Extract test metrics from Surefire reports
if [ -f target/surefire-reports/TEST-*.xml ]; then
  echo "📊 Test Results:"
  TOTAL=$(grep -oP 'tests="\K[0-9]+' target/surefire-reports/TEST-*.xml | awk '{s+=$1} END {print s}')
  FAILURES=$(grep -oP 'failures="\K[0-9]+' target/surefire-reports/TEST-*.xml | awk '{s+=$1} END {print s}')
  ERRORS=$(grep -oP 'errors="\K[0-9]+' target/surefire-reports/TEST-*.xml | awk '{s+=$1} END {print s}')
  SKIPPED=$(grep -oP 'skipped="\K[0-9]+' target/surefire-reports/TEST-*.xml | awk '{s+=$1} END {print s}')
  
  FAILED=$((FAILURES + ERRORS))
  PASSED=$((TOTAL - FAILED - SKIPPED))
  
  echo "  ✅ Tests Passed: $PASSED"
  echo "  ❌ Tests Failed: $FAILED"
  echo "  ⏭️  Tests Skipped: $SKIPPED"
  echo "  📊 Total Tests: $TOTAL"
  
  if [ $TOTAL -gt 0 ]; then
    PASS_RATE=$(awk "BEGIN {printf \"%.1f\", ($PASSED / $TOTAL) * 100}")
    echo "  📈 Pass Rate: ${PASS_RATE}%"
  fi
  
  echo ""
else
  echo "⚠️  No test reports found in target/surefire-reports/"
  echo ""
fi

# Extract coverage metrics from JaCoCo XML report
if [ -f target/site/jacoco/jacoco.xml ]; then
  echo "📊 Coverage Results:"
  
  # Line coverage
  LINES_COVERED=$(grep -oP 'type="LINE".*covered="\K[0-9]+' target/site/jacoco/jacoco.xml | head -1)
  LINES_MISSED=$(grep -oP 'type="LINE".*missed="\K[0-9]+' target/site/jacoco/jacoco.xml | head -1)
  LINES_TOTAL=$((LINES_COVERED + LINES_MISSED))
  
  if [ $LINES_TOTAL -gt 0 ]; then
    LINES_PCT=$(awk "BEGIN {printf \"%.2f\", ($LINES_COVERED / $LINES_TOTAL) * 100}")
    echo "  📈 Line Coverage: ${LINES_PCT}% ($LINES_COVERED / $LINES_TOTAL lines)"
  fi
  
  # Branch coverage
  BRANCHES_COVERED=$(grep -oP 'type="BRANCH".*covered="\K[0-9]+' target/site/jacoco/jacoco.xml | head -1)
  BRANCHES_MISSED=$(grep -oP 'type="BRANCH".*missed="\K[0-9]+' target/site/jacoco/jacoco.xml | head -1)
  BRANCHES_TOTAL=$((BRANCHES_COVERED + BRANCHES_MISSED))
  
  if [ $BRANCHES_TOTAL -gt 0 ]; then
    BRANCHES_PCT=$(awk "BEGIN {printf \"%.2f\", ($BRANCHES_COVERED / $BRANCHES_TOTAL) * 100}")
    echo "  🌿 Branch Coverage: ${BRANCHES_PCT}% ($BRANCHES_COVERED / $BRANCHES_TOTAL branches)"
  fi
  
  # Method coverage
  METHODS_COVERED=$(grep -oP 'type="METHOD".*covered="\K[0-9]+' target/site/jacoco/jacoco.xml | head -1)
  METHODS_MISSED=$(grep -oP 'type="METHOD".*missed="\K[0-9]+' target/site/jacoco/jacoco.xml | head -1)
  METHODS_TOTAL=$((METHODS_COVERED + METHODS_MISSED))
  
  if [ $METHODS_TOTAL -gt 0 ]; then
    METHODS_PCT=$(awk "BEGIN {printf \"%.2f\", ($METHODS_COVERED / $METHODS_TOTAL) * 100}")
    echo "  📦 Method Coverage: ${METHODS_PCT}% ($METHODS_COVERED / $METHODS_TOTAL methods)"
  fi
  
  echo ""
  echo "📁 Coverage Report Location:"
  echo "  HTML: target/site/jacoco/index.html"
  echo "  XML:  target/site/jacoco/jacoco.xml"
  echo "  CSV:  target/site/jacoco/jacoco.csv"
  echo ""
  
else
  echo "⚠️  No coverage report found at target/site/jacoco/jacoco.xml"
  echo "Run: mvn jacoco:report"
  echo ""
fi

# Calculate Release Confidence Score (simplified)
if [ -n "$LINES_PCT" ] && [ -n "$PASS_RATE" ]; then
  echo "🎯 Release Confidence Estimate:"
  
  # Formula: (Coverage * 0.6) + (Pass Rate * 0.3) + (Risk Factor * 0.1)
  COVERAGE_SCORE=$(awk "BEGIN {printf \"%.2f\", $LINES_PCT * 0.6}")
  PASS_SCORE=$(awk "BEGIN {printf \"%.2f\", $PASS_RATE * 0.3}")
  RISK_SCORE=9.0  # Assume 90% risk factor for demo
  
  CONFIDENCE=$(awk "BEGIN {printf \"%.0f\", $COVERAGE_SCORE + $PASS_SCORE + $RISK_SCORE}")
  
  echo "  Coverage Score:  ${COVERAGE_SCORE} (${LINES_PCT}% × 0.6)"
  echo "  Pass Rate Score: ${PASS_SCORE} (${PASS_RATE}% × 0.3)"
  echo "  Risk Adjustment: ${RISK_SCORE} (90% × 0.1)"
  echo "  ─────────────────────────────────────"
  echo "  Total Confidence: ${CONFIDENCE}%"
  echo ""
  
  if [ "$CONFIDENCE" -ge 90 ]; then
    echo "  ✅ High Confidence - Ready to deploy"
  elif [ "$CONFIDENCE" -ge 80 ]; then
    echo "  🟡 Medium Confidence - Review before deploying"
  else
    echo "  ❌ Low Confidence - Do not deploy yet"
  fi
  echo ""
fi

echo "================================================"
echo "Metrics extraction complete!"
echo "================================================"
