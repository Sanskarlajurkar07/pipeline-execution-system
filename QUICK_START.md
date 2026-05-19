# Quick Start Guide

The fastest way to get your project deployed and on your resume.

## 🚀 5-Minute Local Test

```bash
cd "vectorShiftAi_assessment_full_stack/Frontend Technical Assessment"

# Set your API key
export GEMINI_API_KEY=your_key_here

# Run with Docker
docker-compose up --build

# Access at http://localhost
```

## 📤 Push to GitHub (10 minutes)

```bash
# 1. Create repo on GitHub: https://github.com/new
#    Name: pipeline-execution-system
#    Public, no README

# 2. Initialize and push
cd "vectorShiftAi_assessment_full_stack/Frontend Technical Assessment"
git init
git add .
git commit -m "Initial commit: Pipeline execution system with Spring Boot and React"
git remote add origin https://github.com/Sanskarlajurkar07/pipeline-execution-system.git
git branch -M main
git push -u origin main
```

## ☁️ Deploy to AWS Free Tier (30 minutes)

### Quick Steps:

1. **Create AWS Account** (5 min)
   - Go to https://aws.amazon.com/free/
   - Sign up (need credit card for verification)

2. **Launch EC2 Instance** (5 min)
   - EC2 Dashboard → Launch Instance
   - Name: `pipeline-system`
   - AMI: Ubuntu 22.04 LTS
   - Instance type: **t2.micro** (free tier)
   - Create key pair: `pipeline-key.pem`
   - Security group: Allow ports 22, 80, 8080
   - Launch!

3. **Connect and Deploy** (15 min)
   ```bash
   # Connect
   ssh -i pipeline-key.pem ubuntu@YOUR_EC2_IP
   
   # Install Docker
   sudo apt update
   sudo apt install docker.io docker-compose git -y
   sudo usermod -aG docker ubuntu
   exit
   
   # Reconnect and deploy
   ssh -i pipeline-key.pem ubuntu@YOUR_EC2_IP
   git clone https://github.com/Sanskarlajurkar07/pipeline-execution-system.git
   cd pipeline-execution-system
   export GEMINI_API_KEY=your_key
   docker-compose up -d --build
   ```

4. **Access Your App**
   - Frontend: `http://YOUR_EC2_IP`
   - Backend: `http://YOUR_EC2_IP:8080`

## 📝 Update Resume (5 minutes)

Add this to your projects section:

```
Pipeline Execution System — github.com/Sanskarlajurkar07/pipeline-execution-system
Stack: React.js, Spring Boot, Google Gemini API, Docker, AWS EC2

• Developed full-stack application with Spring Boot backend and React frontend for building and executing data processing pipelines
• Implemented graph algorithms (DAG validation, topological sorting) using Kahn's algorithm for correct pipeline execution
• Integrated Google Gemini AI API supporting multiple models (2.5-flash, 2.0-flash, 1.5-pro, 1.5-flash)
• Built visual pipeline builder using React Flow with drag-and-drop interface
• Containerized application with Docker and deployed on AWS EC2 free tier
• Wrote comprehensive unit tests achieving 100% pass rate with JUnit 5 and Mockito
```

## 🔗 Update LinkedIn (5 minutes)

1. Go to LinkedIn → Profile → Add Project
2. Project name: "Pipeline Execution System"
3. Description: (same as resume)
4. Add URLs:
   - GitHub: `https://github.com/Sanskarlajurkar07/pipeline-execution-system`
   - Live Demo: `http://YOUR_EC2_IP`
5. Add skills: Spring Boot, React, Docker, AWS, AI Integration

## ✅ Final Checklist

- [ ] Code pushed to GitHub
- [ ] Repository is public
- [ ] README looks good
- [ ] Deployed on AWS EC2
- [ ] Application is accessible
- [ ] Resume updated
- [ ] LinkedIn updated
- [ ] GitHub profile updated

## 🎯 What Recruiters Will See

1. **GitHub Repository**
   - Professional README
   - Clean, tested code
   - Docker setup
   - CI/CD pipeline
   - Good documentation

2. **Live Deployment**
   - Working application
   - Accessible 24/7
   - Professional URL

3. **Your Resume**
   - Full-stack project
   - Modern tech stack
   - Deployed on AWS
   - GitHub link

## 💡 Interview Talking Points

**"Tell me about this project"**
> "I built a full-stack pipeline execution system that allows users to visually create and execute data processing workflows with AI integration. The backend is Spring Boot with REST APIs, and I implemented graph algorithms like DAG validation and topological sorting. The frontend is React with a visual builder. I containerized it with Docker and deployed it on AWS EC2."

**"What was challenging?"**
> "I had to implement Kahn's algorithm for DAG validation and topological sorting to ensure pipelines execute in the correct order. I also integrated Google's Gemini API and had to handle different model selections properly. I wrote comprehensive tests to ensure everything worked correctly."

**"How did you deploy it?"**
> "I containerized both frontend and backend with Docker using multi-stage builds for optimization. Then I deployed it on AWS EC2 free tier, configured security groups, and set up auto-restart on reboot. I also documented the entire deployment process."

## 📚 Detailed Guides

- **Full AWS Guide**: See `AWS_DEPLOYMENT_GUIDE.md`
- **All Deployment Options**: See `DEPLOYMENT.md`
- **GitHub Push Steps**: See `GITHUB_PUSH_CHECKLIST.md`
- **Project Details**: See `PROJECT_SUMMARY.md`

## 🆘 Need Help?

**Common Issues:**

1. **Docker permission denied**
   ```bash
   sudo usermod -aG docker ubuntu
   exit  # and reconnect
   ```

2. **Can't access application**
   - Check security group allows port 80 and 8080
   - Verify containers are running: `docker-compose ps`

3. **Out of memory on EC2**
   ```bash
   sudo fallocate -l 2G /swapfile
   sudo chmod 600 /swapfile
   sudo mkswap /swapfile
   sudo swapon /swapfile
   ```

## 🎉 You're Done!

Your project is now:
- ✅ On GitHub (public)
- ✅ Deployed on AWS (accessible 24/7)
- ✅ On your resume
- ✅ On your LinkedIn
- ✅ Ready for recruiter review

**Total time**: ~1 hour from start to finish!

---

**Next Steps**: Start applying to jobs and be ready to discuss this project in interviews! 🚀
