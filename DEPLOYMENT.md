# Deployment Guide

This guide covers different deployment options for the Pipeline Execution System.

## Quick Start with Docker

The fastest way to deploy:

```bash
# 1. Clone the repository
git clone <your-repo-url>
cd Frontend\ Technical\ Assessment

# 2. Set your Gemini API key
export GEMINI_API_KEY=your_actual_api_key_here

# 3. Build and run
docker-compose up --build -d

# 4. Access the application
# Frontend: http://localhost
# Backend API: http://localhost:8080
```

## Deployment Options

### 1. Render.com (Free Tier Available)

**Backend Deployment:**
1. Create a new Web Service on Render
2. Connect your GitHub repository
3. Configure:
   - Build Command: `cd pipeline-backend && ./mvnw clean package -DskipTests`
   - Start Command: `java -jar pipeline-backend/target/*.jar`
   - Add environment variable: `GEMINI_API_KEY`

**Frontend Deployment:**
1. Create a new Static Site on Render
2. Configure:
   - Build Command: `cd frontend && npm install && npm run build`
   - Publish Directory: `frontend/build`

### 2. Railway.app (Easy Docker Deployment)

1. Install Railway CLI: `npm install -g @railway/cli`
2. Login: `railway login`
3. Initialize: `railway init`
4. Deploy: `railway up`
5. Add environment variable in Railway dashboard: `GEMINI_API_KEY`

### 3. AWS EC2

**Prerequisites:**
- AWS account
- EC2 instance (t2.micro for free tier)
- Security group allowing ports 80, 8080, 22

**Steps:**
```bash
# SSH into your EC2 instance
ssh -i your-key.pem ubuntu@your-ec2-ip

# Install Docker
sudo apt update
sudo apt install docker.io docker-compose -y
sudo usermod -aG docker ubuntu

# Clone and deploy
git clone <your-repo-url>
cd Frontend\ Technical\ Assessment
export GEMINI_API_KEY=your_key
docker-compose up -d
```

### 4. Google Cloud Run

**Backend:**
```bash
# Build and push to Google Container Registry
gcloud builds submit --tag gcr.io/YOUR_PROJECT/pipeline-backend ./pipeline-backend

# Deploy to Cloud Run
gcloud run deploy pipeline-backend \
  --image gcr.io/YOUR_PROJECT/pipeline-backend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated \
  --set-env-vars GEMINI_API_KEY=your_key
```

**Frontend:**
```bash
# Build and push
gcloud builds submit --tag gcr.io/YOUR_PROJECT/pipeline-frontend ./frontend

# Deploy
gcloud run deploy pipeline-frontend \
  --image gcr.io/YOUR_PROJECT/pipeline-frontend \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

### 5. Heroku

**Backend:**
```bash
cd pipeline-backend
heroku create your-app-backend
heroku config:set GEMINI_API_KEY=your_key
git push heroku main
```

**Frontend:**
```bash
cd frontend
heroku create your-app-frontend
heroku buildpacks:set heroku/nodejs
git push heroku main
```

### 6. DigitalOcean App Platform

1. Connect your GitHub repository
2. Select "Docker Compose" as deployment type
3. Add environment variable: `GEMINI_API_KEY`
4. Deploy

## Environment Configuration

### Production Environment Variables

**Backend (.env or environment):**
```
GEMINI_API_KEY=your_actual_api_key
PORT=8080
```

**Frontend (if needed):**
```
REACT_APP_API_URL=https://your-backend-url.com
```

## SSL/HTTPS Setup

Most cloud platforms provide automatic SSL. For custom domains:

### Using Let's Encrypt with Nginx

```bash
# Install certbot
sudo apt install certbot python3-certbot-nginx

# Get certificate
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com

# Auto-renewal
sudo certbot renew --dry-run
```

## Monitoring and Logs

### Docker Logs
```bash
# View all logs
docker-compose logs -f

# View specific service
docker-compose logs -f backend
docker-compose logs -f frontend
```

### Application Logs
Backend logs are available in the Spring Boot console output.

## Scaling Considerations

For production use, consider:

1. **Database**: Add PostgreSQL/MySQL for persistence
2. **Caching**: Add Redis for API response caching
3. **Load Balancer**: Use Nginx or cloud load balancer
4. **CDN**: Use CloudFlare or AWS CloudFront for frontend
5. **Monitoring**: Add Prometheus + Grafana or use cloud monitoring

## Troubleshooting

### Backend won't start
- Check if GEMINI_API_KEY is set correctly
- Verify Java 17 is installed
- Check port 8080 is not in use

### Frontend can't connect to backend
- Update REACT_APP_API_URL in frontend
- Check CORS configuration in backend
- Verify backend is accessible

### Docker build fails
- Ensure Docker has enough memory (4GB+)
- Clear Docker cache: `docker system prune -a`
- Check .dockerignore files

## Cost Estimates

- **Render.com**: Free tier available, $7/month for paid
- **Railway.app**: $5/month with free trial
- **AWS EC2**: ~$5-10/month (t2.micro)
- **Google Cloud Run**: Pay per use, ~$5-15/month
- **Heroku**: $7/month per dyno
- **DigitalOcean**: $5/month for basic droplet

## Security Checklist

- [ ] Never commit .env files
- [ ] Use environment variables for secrets
- [ ] Enable HTTPS in production
- [ ] Set up firewall rules
- [ ] Regular security updates
- [ ] Monitor API usage and costs

## Support

For issues or questions:
- Open an issue on GitHub
- Contact: sanskarlajurkar9645@gmail.com
