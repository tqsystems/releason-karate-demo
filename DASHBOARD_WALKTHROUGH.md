# Releason Dashboard Walkthrough

When you run the demo and it sends metrics to Releason, here's exactly what you'll see on the dashboard.

## Main Dashboard Overview

### Welcome Screen

```
┌─────────────────────────────────────────────────────────────────┐
│  Welcome back, [Your Name]!                                     │
│  Here's your release confidence overview for today              │
└─────────────────────────────────────────────────────────────────┘
```

### Release Confidence Card

This is the primary metric Releason calculates for you:

```
┌───────────────────────────────────────────────────────────────┐
│  Release 1.0.0 • Jan 30, 2026 • releason-karate-demo          │
│                                                               │
│                        86%                                    │
│              Release Confidence Score                         │
│                                                               │
│                    🟡 Medium Risk                             │
│                                                               │
│  Recommendation:                                              │
│  2 tests are failing. Fix email validation tests before      │
│  deploying. Estimated time to ship: 1h 30m                   │
│                                                               │
│  [View Details] [Approve Deployment] [View History]          │
└───────────────────────────────────────────────────────────────┘
```

### Metrics Grid

Quick overview of key metrics:

```
┌─────────────────┬──────────────────┬──────────────┬──────────────┐
│ Release         │  Test Coverage   │  Risk Level  │ Time to Ship │
│ Confidence      │                  │              │              │
├─────────────────┼──────────────────┼──────────────┼──────────────┤
│      86%        │      85%         │   Medium     │    1h 30m    │
│ ↑ 3% from last  │ 255/300 lines    │ (2 tests     │ With fixes   │
│ Above threshold │ 83% branches     │  failing)    │              │
│ ✅ Good         │ ⚠️  Below target │ ⚠️  Caution  │ 🕒 Soon      │
└─────────────────┴──────────────────┴──────────────┴──────────────┘
```

## Detailed Breakdown

Click **"View Details"** to see comprehensive analysis:

### Section 1: Test Results

```
┌────────────────────────────────────────────────────────────────┐
│  📋 Test Results                                               │
├────────────────────────────────────────────────────────────────┤
│  Tests: 10 passed, 2 failed (83.3% pass rate)                 │
│  Duration: 2m 15s                                              │
│  Latest Run: Jan 30, 2026 10:30 AM                            │
│                                                                │
│  ✅ Scenario: Get all users                                   │
│  ✅ Scenario: Get user by ID                                  │
│  ✅ Scenario: Create valid user                               │
│  ❌ Scenario: Create invalid user - missing email             │
│     Error: Expected 400, got 500                              │
│     File: users.feature:27                                    │
│  ❌ Scenario: Create invalid user - invalid email format      │
│     Error: Expected validation error, got success             │
│     File: users.feature:32                                    │
│  ✅ Scenario: Update user                                     │
│  ✅ Scenario: Delete user                                     │
│  ✅ Scenario: Get all posts                                   │
│  ✅ Scenario: Get post by ID                                  │
│  ✅ Scenario: Create new post                                 │
│  ✅ Scenario: Get all comments                                │
│  ✅ Scenario: Create comment on post                          │
│                                                                │
│  [View Full Test Report] [Download JUnit XML]                 │
└────────────────────────────────────────────────────────────────┘
```

### Section 2: Code Coverage

```
┌────────────────────────────────────────────────────────────────┐
│  📊 Code Coverage Breakdown                                    │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  Overall Coverage: 85%  ⚠️ Below 90% target                   │
│                                                                │
│  Lines:      85% ████████████████████░░░░ 255/300             │
│  Branches:   83% ███████████████████░░░░░ 82/99               │
│  Functions:  88% █████████████████████░░░ 42/48               │
│                                                                │
│  By Package:                                                   │
│  ✅ com.example.controller  92% (high)                        │
│  ✅ com.example.repository  89% (good)                        │
│  ⚠️  com.example.entity     75% (needs attention)             │
│                                                                │
│  Uncovered Critical Paths:                                     │
│  ⚠️  UserController.java:45-52 (error handling)               │
│  ⚠️  PostController.java:78-82 (edge case)                    │
│                                                                │
│  [View JaCoCo Report] [Download Coverage XML]                 │
└────────────────────────────────────────────────────────────────┘
```

### Section 3: Risk Items

Releason automatically identifies issues that affect deployment confidence:

```
┌────────────────────────────────────────────────────────────────┐
│  ⚠️  Risk Items Requiring Attention                            │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  🔴 HIGH RISK - 2 Tests Failing                                │
│     Impact: Users can create accounts with invalid emails     │
│     Files: UserController.java, users.feature                 │
│     Recommendation: Fix email validation logic                │
│     Est. Time: 30 minutes                                     │
│     [View Code] [Assign to Developer]                         │
│                                                                │
│  🟡 MEDIUM RISK - Coverage Below Threshold                     │
│     Current: 85% | Target: 90%                                │
│     Gap: 15 lines need test coverage                          │
│     Files: UserController.java (error paths)                  │
│     Recommendation: Add integration tests for error scenarios │
│     Est. Time: 45 minutes                                     │
│     [View Uncovered Lines] [Generate Test Template]           │
│                                                                │
│  🟢 LOW RISK - Minor Performance Concern                       │
│     Test duration increased by 15% from baseline              │
│     Recommendation: Monitor for trends                        │
│     [View Performance Trends]                                 │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

### Section 4: Deployment Recommendation

The key decision support tool:

```
┌────────────────────────────────────────────────────────────────┐
│  🎯 Should We Deploy?                                          │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  NOT YET - But you're close!                                  │
│                                                                │
│  ✅ What's Good:                                              │
│     • Build successful                                        │
│     • Code coverage above 80% minimum                         │
│     • No security vulnerabilities detected                    │
│     • API performance within acceptable range                 │
│                                                                │
│  ❌ What Needs Fixing:                                        │
│     • 2 tests failing (email validation)                      │
│     • Coverage 5% below team target                           │
│                                                                │
│  📊 Confidence Calculation:                                    │
│     • Coverage Score:    85% × 0.6 = 51.0 points             │
│     • Test Pass Rate:   83.3% × 0.3 = 25.0 points            │
│     • Risk Adjustment:   90% × 0.1 = 9.0 points              │
│     ─────────────────────────────────────────                 │
│     Total:              85 / 100 = 86% Confidence            │
│                                                                │
│  🕒 Estimated Time to Fix: 1-2 hours                          │
│  📅 Suggested Action: Fix issues, then re-run tests           │
│                                                                │
│  Once Fixed:                                                   │
│     • Expected Confidence: 95%+                               │
│     • Risk Level: Low                                         │
│     • Deployment: ✅ Recommended                              │
│                                                                │
│  [Create Jira Ticket] [Notify Team] [Schedule Deploy]        │
└────────────────────────────────────────────────────────────────┘
```

## Historical Trends

View how your metrics change over time:

```
┌────────────────────────────────────────────────────────────────┐
│  📈 Release Confidence Trends (Last 30 Days)                   │
├────────────────────────────────────────────────────────────────┤
│                                                                │
│  100% ┤                                    ●                   │
│   95% ┤                          ●───●───●                     │
│   90% ┤                ●───●───●                               │
│   85% ┤      ●───●───●                                         │
│   80% ┤●───●                                                   │
│       └─────────────────────────────────────────────────────   │
│       Jan 1    Jan 8    Jan 15   Jan 22   Jan 30             │
│                                                                │
│  Key Events:                                                   │
│  • Jan 15: Improved test coverage (+8%)                       │
│  • Jan 22: Fixed 5 flaky tests                                │
│  • Jan 28: Added new validation tests                         │
│                                                                │
└────────────────────────────────────────────────────────────────┘
```

## Team Activity

See what your team is working on:

```
┌────────────────────────────────────────────────────────────────┐
│  👥 Recent Activity                                            │
├────────────────────────────────────────────────────────────────┤
│  • Alice fixed email validation (2 tests now passing) • 5m ago│
│  • Bob improved coverage in PostController (+3%) • 1h ago     │
│  • Charlie approved deployment for staging • 2h ago           │
│  • Alice merged PR #123 (confidence: 94%) • 3h ago            │
└────────────────────────────────────────────────────────────────┘
```

## How Metrics Are Calculated

### Release Confidence Score Formula

```
Score = (Coverage × 0.6) + (Pass Rate × 0.3) + (Risk Factor × 0.1)

Example (from demo):
  Coverage:     85% × 0.6 = 51.0 points
  Pass Rate:   83.3% × 0.3 = 25.0 points
  Risk Factor:  90% × 0.1 = 9.0 points
  ─────────────────────────────────────
  Total:               = 85.0 points = 86%
```

### Risk Level Thresholds

- **Low Risk:** 90%+ confidence, all tests passing
- **Medium Risk:** 80-89% confidence, or 1-3 tests failing
- **High Risk:** 70-79% confidence, or 4+ tests failing
- **Critical Risk:** <70% confidence, or build failure

### Time to Ship Estimation

Based on historical data and complexity:
- Failed test fix: 15-30 minutes per test
- Coverage gap: 5-10 minutes per percentage point
- Integration issues: 30-60 minutes
- Performance problems: 1-4 hours

## Next Steps After Viewing Dashboard

### If Confidence is High (90%+)
1. Click **"Approve Deployment"**
2. Deploy to staging/production
3. Monitor post-deployment metrics

### If Confidence is Medium (80-89%)
1. Review risk items
2. Fix critical issues
3. Re-run tests
4. Check updated confidence score

### If Confidence is Low (<80%)
1. **Stop** - Do not deploy
2. Review all failed tests
3. Improve test coverage
4. Address all high-risk items
5. Re-assess before proceeding

## Customization Options

In Releason Settings, you can customize:

- **Confidence Formula:** Adjust weights for coverage, tests, risk
- **Thresholds:** Set your own targets (e.g., 95% coverage)
- **Alerts:** Get notified when confidence drops
- **Integrations:** Connect to Slack, Jira, PagerDuty
- **Deployment Gates:** Automatically block deployments below threshold

## Support

- **Dashboard Tour:** Click "?" icon for guided walkthrough
- **Documentation:** [docs.releason.com](https://docs.releason.com)
- **Video Tutorials:** [releason.com/tutorials](https://releason.com/tutorials)
- **Support:** support@releason.com
