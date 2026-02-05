# Connecting to Releason Dashboard

This guide walks you through integrating this demo with the Releason Release Confidence Platform.

## Overview

Releason receives test and coverage metrics from your CI/CD pipeline and calculates a Release Confidence Score to help you make data-driven deployment decisions.

## Step 1: Get Your Releason Webhook URL

### Option A: Sign up for Releason (New Users)

1. Go to [https://releason.com/signup](https://releason.com/signup)
2. Create an account with your email
3. Verify your email address
4. Complete the onboarding wizard

### Option B: Access Existing Account

1. Go to [https://releason.com/login](https://releason.com/login)
2. Log in with your credentials
3. Navigate to your dashboard

### Get Webhook URL

1. Click **Settings** → **Integrations**
2. Click **"Create New Webhook"** button
3. Fill in webhook details:
   - **Name:** `releason-karate-demo`
   - **Description:** `Demo repository for Karate + Releason`
   - **Repository:** Select or create repository entry
4. Click **"Generate Webhook"**
5. Copy the webhook URL

Your webhook URL will look like:
```
https://releason.com/api/webhooks/wh_abc123xyz789def456
```

⚠️ **Important:** Keep this URL secure! It's your authentication token.

## Step 2: Add Webhook to GitHub Secrets

### Via GitHub Web Interface

1. Go to your repository on GitHub: `https://github.com/your-org/releason-karate-demo`
2. Click **Settings** (repository settings, not account settings)
3. In the left sidebar, click **Secrets and variables** → **Actions**
4. Click **"New repository secret"** button
5. Add the secret:
   - **Name:** `RELEASON_WEBHOOK_URL`
   - **Secret:** Paste your webhook URL from Step 1
6. Click **"Add secret"**

### Via GitHub CLI

```bash
# Install GitHub CLI if needed
brew install gh  # macOS
# or: sudo apt install gh  # Linux

# Authenticate
gh auth login

# Add secret
gh secret set RELEASON_WEBHOOK_URL --body "https://releason.com/api/webhooks/wh_abc123xyz789def456"
```

## Step 3: Verify Integration

### Trigger a Workflow Run

```bash
# Make a small change and push
git commit --allow-empty -m "Test Releason integration"
git push origin main
```

### Check GitHub Actions

1. Go to **Actions** tab in your GitHub repository
2. Find the latest workflow run
3. Click on it to see details
4. Look for step: **"Send metrics to Releason"**
5. Should show: `✅ Metrics sent to Releason`

### Check Releason Dashboard

1. Go to [https://releason.com/dashboard](https://releason.com/dashboard)
2. You should see your repository listed
3. Click on the repository to see detailed metrics
4. Data may take 10-30 seconds to appear

## Step 4: View Your Metrics

Once integrated, you'll see the following on your Releason dashboard:

### Release Confidence Card
```
Release 1.0.0 • Jan 30, 2026

      86%
Release Confidence Score

🟡 Medium Risk

Recommendation:
Fix 2 failing tests before deploying.
Estimated time to ship: 1h 30m
```

### Test Metrics
- ✅ Tests Passed: 10
- ❌ Tests Failed: 2
- 📊 Pass Rate: 83.3%
- 📈 Total Tests: 12

### Coverage Metrics
- 📊 Line Coverage: 85%
- 🌿 Branch Coverage: 83%
- 📦 Function Coverage: 88%
- 📈 Total Lines: 300 (255 covered)

### Risk Analysis
Releason identifies potential issues:
- ⚠️ 2 tests failing (email validation)
- ⚠️ Coverage below 90% threshold
- ✅ No critical security issues
- ✅ Build successful

## What Data Is Sent?

Your webhook receives the following data (JSON format):

```json
{
  "event_type": "branch_metrics",
  "repository": {
    "id": "12345",
    "name": "releason-karate-demo",
    "owner": "your-org",
    "full_name": "your-org/releason-karate-demo"
  },
  "branch": "main",
  "commit_sha": "abc123...",
  "commit_message": "Add new feature",
  "author": "developer-name",
  "workflow_run_id": "67890",
  "timestamp": "2026-01-30T10:30:00Z",
  "tests": {
    "passed": 10,
    "failed": 2,
    "total": 12,
    "pass_rate": 83.3
  },
  "coverage": {
    "lines_pct": 85.0,
    "lines_covered": 255,
    "lines_total": 300,
    "branches_pct": 83.0,
    "functions_pct": 88.0
  }
}
```

### Data Privacy

- **Stored:** Repository name, branch, commit SHA, test results, coverage
- **NOT Stored:** Source code, secrets, environment variables, logs
- **Retention:** 90 days (configurable in Releason settings)
- **Access:** Only your organization members with appropriate permissions

## Troubleshooting Webhook Issues

### Issue: Webhook not being sent

**Check 1:** Verify secret is set correctly
```bash
# List secrets (won't show values)
gh secret list

# Should show: RELEASON_WEBHOOK_URL
```

**Check 2:** Verify workflow is running
- Go to **Actions** tab
- Check if workflow runs on push
- Look for any error messages

**Check 3:** Check network connectivity
```bash
# Test webhook URL (use your actual URL)
curl -X POST https://releason.com/api/webhooks/wh_test \
  -H "Content-Type: application/json" \
  -d '{"test": true}'
```

### Issue: Webhook sent but data not appearing on dashboard

**Check 1:** Verify webhook URL is correct
- Compare URL in GitHub secret with URL in Releason
- Ensure no extra spaces or characters

**Check 2:** Check webhook logs in Releason
1. Dashboard → Settings → Webhook Logs
2. Find recent webhook deliveries
3. Check status codes and error messages

**Check 3:** Wait a bit longer
- Data can take 30-60 seconds to process
- Refresh the dashboard page

### Issue: "403 Forbidden" or "401 Unauthorized"

**Cause:** Invalid or expired webhook URL

**Solution:**
1. Generate a new webhook URL in Releason
2. Update the GitHub secret with new URL
3. Trigger a new workflow run

### Issue: Tests pass but showing as failed in Releason

**Cause:** Metrics extraction may be incorrect

**Solution:**
1. Check `target/surefire-reports/` for test results
2. Verify JaCoCo reports in `target/site/jacoco/`
3. Review GitHub Actions logs for metric extraction step

## Advanced Configuration

### Custom Webhook Headers

Add authentication or custom headers:

```yaml
# In .github/workflows/karate-test.yml
- name: Send metrics to Releason
  run: |
    curl -X POST "${{ secrets.RELEASON_WEBHOOK_URL }}" \
      -H "Content-Type: application/json" \
      -H "X-Custom-Header: value" \
      -H "Authorization: Bearer ${{ secrets.API_TOKEN }}" \
      -d '{...}'
```

### Multiple Environments

Send metrics for different environments:

```yaml
- name: Send metrics to Releason
  env:
    ENVIRONMENT: ${{ github.ref_name == 'main' && 'production' || 'staging' }}
  run: |
    curl -X POST "${{ secrets.RELEASON_WEBHOOK_URL }}" \
      -H "Content-Type: application/json" \
      -d '{
        "environment": "'$ENVIRONMENT'",
        ...
      }'
```

### Conditional Sending

Only send metrics on specific branches:

```yaml
- name: Send metrics to Releason
  if: github.ref == 'refs/heads/main' || github.ref == 'refs/heads/develop'
  run: |
    curl ...
```

## Next Steps

1. ✅ Integration complete
2. ✅ Read [DASHBOARD_WALKTHROUGH.md](DASHBOARD_WALKTHROUGH.md) to understand metrics
3. ✅ Set up alerts and notifications in Releason
4. ✅ Configure deployment gates based on confidence scores
5. ✅ Integrate with your team's workflow

## Support

- **Documentation:** [Releason Docs](https://docs.releason.com)
- **Webhook Logs:** Dashboard → Settings → Webhook Logs
- **API Reference:** [https://docs.releason.com/api](https://docs.releason.com/api)
- **Support:** support@releason.com
- **Status:** [status.releason.com](https://status.releason.com)
