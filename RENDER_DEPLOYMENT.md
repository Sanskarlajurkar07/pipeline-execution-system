# Render.com Deployment Guide (EASIEST - Recommended)

Deploy your full-stack application on Render.com in 10 minutes. **100% FREE** with no credit card required!

## Why Render?

✅ **Completely FREE** - No credit card needed
✅ **Auto-deploy from GitHub** - Push code, it deploys automatically
✅ **Free SSL/HTTPS** - Automatic secure URLs
✅ **Easy setup** - No server management
✅ **Perfect for portfolio** - Professional URLs
✅ **750 hours/month free** - Enough for 24/7 uptime

## Step 1: Push Code to GitHub (If Not Done)

```bash
cd "vectorShiftAi_assessment_full_stack/Frontend Technical Assessment"

# Initialize git
git init
git add .
git commit -m "Initial commit: Pipeline execution system"

# Create repo on GitHub and push
git remote add origin https://github.com/Sanskarlajurkar07/pipeline-execution-system.git
git branch -M main
git push -u origin main
```

## Step 2: Sign Up for Render

1. Go to https://render.com/
2. Click **"Get Started for Free"**
3. Sign up with **GitHub** (easiest option)
4. Authorize Render to access your repositories
5. No credit card required! ✅

## Step 3: Deploy Backend (Spring Boot)

### 3.1 Create Web Service

1. From Render Dashboard, click **"New +"** → **"Web Service"**
2. Click **"Connect a repository"**
3. Find and select your `pipeline-execution-system` repository
4. Click **"Connect"**

### 3.2 Configure Backend Service

Fill in these settings:

**Basic Settings:**
- **Name**: `pipeline-backend` (or your choice)
- **Region**: Choose closest to you (e.g., Oregon, Frankfurt, Singapore)
- **Branch**: `main`
- **Root Directory**: `pipeline-backend`

**Build & Deploy:**
- **Runtime**: `Docker`
- **Dockerfile Path**: `pipeline-backend/Dockerfile`

**OR if you prefer Maven (without Docker):**
- **Runtime**: `Java`
- **Build Command**: 
  ```bash
  cd pipeline-backend && ./mvnw clean package -DskipTests
  ```
- **Start Command**: 
  ```bash
  java -jar pipeline-backend/target/*.jar
  ```

**Instance Type:**
- Select **"Free"** (750 hours/month)

**Environment Variables:**
Click **"Advanced"** → **"Add Environment Variable"**
- **Key**: `GEMINI_API_KEY`
- **Value**: `your_actual_gemini_api_key_here`

### 3.3 Deploy

1. Click **"Create Web Service"**
2. Wait 5-10 minutes for build and deployment
3. You'll get a URL like: `https://pipeline-backend.onrender.com`
4. Test it: `https://pipeline-backend.onrender.com/` should return `{"status":"ok"}`

**Important Note**: Free tier services sleep after 15 minutes of inactivity. First request after sleep takes ~30 seconds to wake up.

## Step 4: Deploy Frontend (React)

### 4.1 Create Static Site

1. From Render Dashboard, click **"New +"** → **"Static Site"**
2. Select your `pipeline-execution-system` repository
3. Click **"Connect"**

### 4.2 Configure Frontend

**Basic Settings:**
- **Name**: `pipeline-frontend`
- **Branch**: `main`
- **Root Directory**: `frontend`

**Build Settings:**
- **Build Command**: 
  ```bash
  npm install && npm run build
  ```
- **Publish Directory**: 
  ```bash
  build
  ```

**Environment Variables:**
Click **"Advanced"** → **"Add Environment Variable"**
- **Key**: `REACT_APP_API_URL`
- **Value**: `https://pipeline-backend.onrender.com` (your backend URL from Step 3)

### 4.3 Deploy

1. Click **"Create Static Site"**
2. Wait 3-5 minutes for build
3. You'll get a URL like: `https://pipeline-frontend.onrender.com`
4. Open it in browser - your app should work!

## Step 5: Update Frontend to Use Backend URL

If your frontend is hardcoded to use `localhost:8080`, you need to update it:

### Option 1: Use Environment Variable (Recommended)

Update your frontend code to use environment variable:

```javascript
// In your frontend API calls
const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Example in submit.js or wherever you make API calls
fetch(`${API_URL}/pipelines/parse`, {
  method: 'POST',
  // ...
})
```

Then set `REACT_APP_API_URL` in Render dashboard (already done in Step 4.2).

### Option 2: Update Directly

Find where you make API calls in frontend and update:

```javascript
// Change from:
fetch('http://localhost:8080/pipelines/parse', ...)

// To:
fetch('https://pipeline-backend.onrender.com/pipelines/parse', ...)
```

Commit and push changes - Render will auto-deploy!

## Step 6: Configure CORS (If Needed)

Make sure your Spring Boot backend allows requests from your frontend domain.

In `CorsConfig.java` or `application.yml`:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:3000",  // for local dev
                "https://pipeline-frontend.onrender.com"  // your Render frontend URL
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*");
    }
}
```

Or allow all origins (simpler for portfolio):

```java
.allowedOrigins("*")
```

## Step 7: Custom Domain (Optional)

### Free Option: Use Render's Free Domain

You already have:
- Backend: `https://pipeline-backend.onrender.com`
- Frontend: `https://pipeline-frontend.onrender.com`

### Paid Option: Custom Domain ($12/year)

1. Buy domain from Namecheap, GoDaddy, etc.
2. In Render Dashboard → Your Service → Settings → Custom Domain
3. Add your domain (e.g., `pipeline.yourdomain.com`)
4. Update DNS records as instructed by Render
5. Render provides free SSL automatically!

## Your Deployment URLs

After deployment, you'll have:

- **Frontend**: `https://pipeline-frontend.onrender.com`
- **Backend API**: `https://pipeline-backend.onrender.com`
- **Health Check**: `https://pipeline-backend.onrender.com/`

## Auto-Deploy on Git Push

Render automatically deploys when you push to GitHub!

```bash
# Make changes to your code
git add .
git commit -m "Update feature"
git push origin main

# Render automatically detects and deploys! 🚀
```

## Monitoring and Logs

### View Logs

1. Go to Render Dashboard
2. Click on your service (backend or frontend)
3. Click **"Logs"** tab
4. See real-time logs

### View Metrics

1. Click **"Metrics"** tab
2. See CPU, Memory, Request count

### Restart Service

1. Click **"Manual Deploy"** → **"Clear build cache & deploy"**

## Troubleshooting

### Backend Returns 404

**Problem**: Backend endpoints not working
**Solution**:
1. Check logs in Render dashboard
2. Verify `GEMINI_API_KEY` is set
3. Check build completed successfully
4. Test health endpoint: `https://your-backend.onrender.com/`

### Frontend Can't Connect to Backend

**Problem**: CORS errors or connection refused
**Solution**:
1. Update `REACT_APP_API_URL` in frontend environment variables
2. Check CORS configuration in backend
3. Verify backend URL is correct (https, not http)
4. Check backend is running (not sleeping)

### Service Sleeping

**Problem**: First request takes 30+ seconds
**Solution**: This is normal for free tier. Options:
1. Keep service awake with UptimeRobot (free): https://uptimerobot.com/
   - Create monitor to ping your backend every 5 minutes
2. Upgrade to paid plan ($7/month) for always-on service

### Build Fails

**Problem**: Build errors in logs
**Solution**:
1. Check build command is correct
2. Verify all dependencies are in `package.json` or `pom.xml`
3. Check Node/Java version compatibility
4. Review error logs in Render dashboard

## Cost Breakdown

### Free Tier (What You Get)

✅ **Web Services**: 750 hours/month (enough for 1 service 24/7)
✅ **Static Sites**: Unlimited
✅ **Bandwidth**: 100 GB/month
✅ **Build Minutes**: 500 minutes/month
✅ **SSL**: Free automatic HTTPS
✅ **Auto-deploy**: Unlimited

### If You Need More (Optional)

- **Starter Plan**: $7/month per service
  - No sleeping
  - More resources
  - Priority support

## Comparison: Render vs AWS EC2

| Feature | Render Free | AWS EC2 Free |
|---------|-------------|--------------|
| **Setup Time** | 10 minutes | 30-60 minutes |
| **Credit Card** | Not required | Required |
| **Auto-deploy** | ✅ Yes | ❌ Manual |
| **SSL/HTTPS** | ✅ Free | ❌ Manual setup |
| **Server Management** | ✅ None | ❌ You manage |
| **Sleeping** | After 15 min | No |
| **Free Duration** | Forever | 12 months |
| **Best For** | Portfolio | Learning AWS |

## Update Your Resume

Add your Render URLs:

```
Pipeline Execution System — Live: https://pipeline-frontend.onrender.com
GitHub: github.com/Sanskarlajurkar07/pipeline-execution-system
Stack: React.js, Spring Boot, Google Gemini API, Docker

• Developed full-stack application with Spring Boot backend and React frontend
• Deployed on Render.com with automatic CI/CD from GitHub
• Implemented graph algorithms (DAG validation, topological sorting)
• Integrated Google Gemini AI API with multiple model support
• Built visual pipeline builder with React Flow
```

## Update GitHub README

Add badges and live demo link:

```markdown
# Pipeline Execution System

[![Live Demo](https://img.shields.io/badge/Live-Demo-success)](https://pipeline-frontend.onrender.com)
[![Backend API](https://img.shields.io/badge/API-Live-blue)](https://pipeline-backend.onrender.com)

[🚀 Live Demo](https://pipeline-frontend.onrender.com) | [📖 Documentation](./README.md)
```

## Maintenance

### Update Application

```bash
# Make changes locally
git add .
git commit -m "Update feature"
git push origin main

# Render auto-deploys! ✅
```

### View Deployment Status

1. Go to Render Dashboard
2. See deployment status (In Progress, Live, Failed)
3. Click for detailed logs

### Rollback

1. Go to service → Events
2. Find previous successful deploy
3. Click "Rollback to this version"

## Security Best Practices

1. **Never commit `.env` files** - Use Render's environment variables
2. **Use HTTPS** - Render provides this automatically
3. **Rotate API keys** - Update in Render dashboard, not in code
4. **Monitor logs** - Check for suspicious activity

## Summary Checklist

- [ ] Code pushed to GitHub
- [ ] Render account created (no credit card needed)
- [ ] Backend deployed on Render
- [ ] Frontend deployed on Render
- [ ] Environment variables configured
- [ ] CORS configured correctly
- [ ] Application accessible via HTTPS
- [ ] URLs added to resume
- [ ] URLs added to LinkedIn
- [ ] GitHub README updated with live demo link

## Your Final URLs

After completing this guide:

- **Live App**: `https://pipeline-frontend.onrender.com`
- **API**: `https://pipeline-backend.onrender.com`
- **GitHub**: `https://github.com/Sanskarlajurkar07/pipeline-execution-system`

**Share these with recruiters!** 🎉

---

**Congratulations!** Your application is now live on the internet with professional HTTPS URLs, automatic deployments, and zero cost! 🚀
