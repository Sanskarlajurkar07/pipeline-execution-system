# AWS EC2 Free Tier Deployment Guide

Complete step-by-step guide to deploy your Pipeline Execution System on AWS EC2 Free Tier (1 year free).

## AWS Free Tier Includes

✅ **EC2 t2.micro instance** - 750 hours/month (enough for 24/7)
✅ **30 GB EBS storage** - More than enough for this project
✅ **15 GB data transfer out** - Sufficient for portfolio project
✅ **Valid for 12 months** from AWS account creation

## Prerequisites

- AWS account (create at https://aws.amazon.com/free/)
- Credit/debit card (for verification, won't be charged if you stay in free tier)
- Your Gemini API key

## Step 1: Create AWS Account

1. Go to https://aws.amazon.com/free/
2. Click "Create a Free Account"
3. Fill in email, password, account name
4. Choose "Personal" account type
5. Enter payment information (for verification only)
6. Verify phone number
7. Choose "Basic Support - Free"
8. Wait for account activation (usually instant)

## Step 2: Launch EC2 Instance

### 2.1 Access EC2 Dashboard

1. Sign in to AWS Console: https://console.aws.amazon.com/
2. Search for "EC2" in the top search bar
3. Click "EC2" to open EC2 Dashboard
4. Make sure you're in a region close to you (top-right corner)
   - Recommended: `us-east-1` (N. Virginia) or `ap-south-1` (Mumbai)

### 2.2 Launch Instance

1. Click **"Launch Instance"** button
2. **Name**: `pipeline-execution-system`

3. **Application and OS Images (AMI)**:
   - Choose: **Ubuntu Server 22.04 LTS**
   - Architecture: **64-bit (x86)**
   - ✅ Free tier eligible

4. **Instance Type**:
   - Choose: **t2.micro** (1 vCPU, 1 GB RAM)
   - ✅ Free tier eligible

5. **Key Pair (login)**:
   - Click "Create new key pair"
   - Key pair name: `pipeline-system-key`
   - Key pair type: **RSA**
   - Private key format: **.pem** (for Mac/Linux) or **.ppk** (for Windows PuTTY)
   - Click "Create key pair"
   - **IMPORTANT**: Save this file securely! You can't download it again.

6. **Network Settings**:
   - Click "Edit"
   - **Firewall (security groups)**: Create security group
   - Security group name: `pipeline-system-sg`
   - Description: `Security group for pipeline execution system`
   
   **Add these rules**:
   - ✅ SSH (port 22) - Source: My IP (for security)
   - ✅ HTTP (port 80) - Source: Anywhere (0.0.0.0/0)
   - ✅ HTTPS (port 443) - Source: Anywhere (0.0.0.0/0)
   - ✅ Custom TCP (port 8080) - Source: Anywhere (0.0.0.0/0) - for backend API

7. **Configure Storage**:
   - Size: **20 GB** (free tier allows up to 30 GB)
   - Volume type: **gp3** (General Purpose SSD)
   - ✅ Free tier eligible

8. **Advanced Details** (optional):
   - Leave as default

9. Click **"Launch Instance"**

10. Wait for instance to be in "Running" state (2-3 minutes)

## Step 3: Connect to Your EC2 Instance

### For Mac/Linux:

```bash
# Move your key to a safe location
mv ~/Downloads/pipeline-system-key.pem ~/.ssh/

# Set correct permissions
chmod 400 ~/.ssh/pipeline-system-key.pem

# Get your instance's public IP from EC2 dashboard
# Connect via SSH
ssh -i ~/.ssh/pipeline-system-key.pem ubuntu@YOUR_EC2_PUBLIC_IP
```

### For Windows:

**Option 1: Using PuTTY**
1. Download PuTTY: https://www.putty.org/
2. Convert .pem to .ppk using PuTTYgen
3. Open PuTTY
4. Host Name: `ubuntu@YOUR_EC2_PUBLIC_IP`
5. Connection > SSH > Auth > Browse to your .ppk file
6. Click "Open"

**Option 2: Using Windows Terminal/PowerShell**
```powershell
ssh -i C:\path\to\pipeline-system-key.pem ubuntu@YOUR_EC2_PUBLIC_IP
```

## Step 4: Install Required Software

Once connected to your EC2 instance:

```bash
# Update system packages
sudo apt update && sudo apt upgrade -y

# Install Docker
sudo apt install docker.io -y

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Add ubuntu user to docker group (so you don't need sudo)
sudo usermod -aG docker ubuntu

# Install Git
sudo apt install git -y

# Verify installations
docker --version
docker-compose --version
git --version

# Log out and log back in for docker group to take effect
exit
```

**Reconnect to your instance** (same SSH command as before)

## Step 5: Deploy Your Application

```bash
# Clone your repository (replace with your actual GitHub URL)
git clone https://github.com/Sanskarlajurkar07/pipeline-execution-system.git
cd pipeline-execution-system

# Set your Gemini API key
export GEMINI_API_KEY="your_actual_gemini_api_key_here"

# Or create a .env file
echo "GEMINI_API_KEY=your_actual_gemini_api_key_here" > .env

# Build and start the application
docker-compose up -d --build

# Check if containers are running
docker-compose ps

# View logs
docker-compose logs -f
```

## Step 6: Access Your Application

1. Get your EC2 instance's **Public IPv4 address** from AWS Console
2. Access your application:
   - **Frontend**: `http://YOUR_EC2_PUBLIC_IP`
   - **Backend API**: `http://YOUR_EC2_PUBLIC_IP:8080`

Example: `http://54.123.45.67`

## Step 7: Set Up Domain Name (Optional but Recommended)

### Option 1: Use Freenom (Free Domain)

1. Go to https://www.freenom.com/
2. Search for available domain (e.g., `yourname-pipeline.tk`)
3. Register for free (12 months)
4. In Freenom dashboard:
   - Manage Domain > Manage Freenom DNS
   - Add A record: `@` → Your EC2 Public IP
   - Add A record: `www` → Your EC2 Public IP
5. Wait 10-30 minutes for DNS propagation
6. Access: `http://yourname-pipeline.tk`

### Option 2: Use AWS Route 53 (Paid)

1. Register domain in Route 53 (~$12/year for .com)
2. Create hosted zone
3. Add A record pointing to EC2 IP

## Step 8: Set Up SSL/HTTPS (Optional but Professional)

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx -y

# Stop your containers temporarily
docker-compose down

# Install Nginx
sudo apt install nginx -y

# Configure Nginx as reverse proxy
sudo nano /etc/nginx/sites-available/pipeline

# Add this configuration:
```

```nginx
server {
    listen 80;
    server_name your-domain.com www.your-domain.com;

    location / {
        proxy_pass http://localhost:80;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }

    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

```bash
# Enable the site
sudo ln -s /etc/nginx/sites-available/pipeline /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx

# Get SSL certificate
sudo certbot --nginx -d your-domain.com -d www.your-domain.com

# Restart your application
cd ~/pipeline-execution-system
docker-compose up -d
```

## Step 9: Auto-Start on Reboot

```bash
# Create systemd service
sudo nano /etc/systemd/system/pipeline-system.service
```

Add this content:

```ini
[Unit]
Description=Pipeline Execution System
Requires=docker.service
After=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/home/ubuntu/pipeline-execution-system
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
User=ubuntu

[Install]
WantedBy=multi-user.target
```

```bash
# Enable and start the service
sudo systemctl enable pipeline-system
sudo systemctl start pipeline-system

# Check status
sudo systemctl status pipeline-system
```

## Monitoring and Maintenance

### Check Application Status

```bash
# Check if containers are running
docker-compose ps

# View logs
docker-compose logs -f

# View backend logs only
docker-compose logs -f backend

# View frontend logs only
docker-compose logs -f frontend

# Restart application
docker-compose restart

# Stop application
docker-compose down

# Start application
docker-compose up -d
```

### Update Application

```bash
cd ~/pipeline-execution-system

# Pull latest changes
git pull origin main

# Rebuild and restart
docker-compose down
docker-compose up -d --build
```

### Monitor System Resources

```bash
# Check disk usage
df -h

# Check memory usage
free -h

# Check CPU usage
top

# Check Docker stats
docker stats
```

## Cost Monitoring

### Stay Within Free Tier

✅ **Do's**:
- Use only t2.micro instance
- Keep storage under 30 GB
- Monitor data transfer (stay under 15 GB/month out)
- Stop instance when not needed (but you lose uptime)

❌ **Don'ts**:
- Don't upgrade to larger instance types
- Don't add extra EBS volumes
- Don't exceed 750 hours/month (24/7 is fine for 1 instance)

### Set Up Billing Alerts

1. Go to AWS Billing Dashboard
2. Click "Billing Preferences"
3. Enable "Receive Free Tier Usage Alerts"
4. Enter your email
5. Set alert threshold: $1 (to catch any charges early)

## Troubleshooting

### Can't Connect via SSH

**Problem**: Connection timeout
**Solution**:
1. Check security group allows SSH (port 22) from your IP
2. Verify instance is running
3. Check you're using correct key file
4. Try: `ssh -vvv -i key.pem ubuntu@IP` for debug info

### Application Not Accessible

**Problem**: Can't access via browser
**Solution**:
1. Check security group allows HTTP (port 80)
2. Verify containers are running: `docker-compose ps`
3. Check logs: `docker-compose logs`
4. Verify EC2 instance public IP hasn't changed

### Out of Memory

**Problem**: Application crashes or slow
**Solution**:
```bash
# Add swap space (virtual memory)
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### Docker Permission Denied

**Problem**: `permission denied while trying to connect to Docker daemon`
**Solution**:
```bash
sudo usermod -aG docker ubuntu
# Log out and log back in
exit
# Reconnect via SSH
```

## Security Best Practices

1. **Change SSH port** (optional but recommended):
```bash
sudo nano /etc/ssh/sshd_config
# Change Port 22 to Port 2222
sudo systemctl restart sshd
# Update security group to allow port 2222 instead of 22
```

2. **Set up firewall**:
```bash
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8080/tcp
sudo ufw enable
```

3. **Regular updates**:
```bash
sudo apt update && sudo apt upgrade -y
```

4. **Monitor logs**:
```bash
sudo tail -f /var/log/auth.log  # SSH attempts
docker-compose logs -f          # Application logs
```

## Backup Strategy

```bash
# Backup your application data
cd ~
tar -czf pipeline-backup-$(date +%Y%m%d).tar.gz pipeline-execution-system/

# Download backup to your local machine
# On your local machine:
scp -i ~/.ssh/pipeline-system-key.pem ubuntu@YOUR_EC2_IP:~/pipeline-backup-*.tar.gz ~/Downloads/
```

## Estimated Costs After Free Tier

After 12 months, if you continue:
- **t2.micro instance**: ~$8-10/month
- **20 GB EBS storage**: ~$2/month
- **Data transfer**: Usually free for small projects
- **Total**: ~$10-12/month

## Summary Checklist

- [ ] AWS account created
- [ ] EC2 t2.micro instance launched
- [ ] Security group configured (ports 22, 80, 8080)
- [ ] SSH key downloaded and secured
- [ ] Connected to instance via SSH
- [ ] Docker and Docker Compose installed
- [ ] Application cloned from GitHub
- [ ] Gemini API key configured
- [ ] Application deployed with docker-compose
- [ ] Application accessible via public IP
- [ ] (Optional) Domain name configured
- [ ] (Optional) SSL certificate installed
- [ ] Auto-start on reboot configured
- [ ] Billing alerts set up

## Your Deployment URLs

After deployment, update these:

- **Public IP**: `http://YOUR_EC2_PUBLIC_IP`
- **Domain** (if configured): `http://your-domain.com`
- **Backend API**: `http://YOUR_EC2_PUBLIC_IP:8080`

Add these URLs to:
1. Your GitHub README
2. Your resume
3. Your LinkedIn profile

## Need Help?

- AWS Free Tier FAQ: https://aws.amazon.com/free/free-tier-faqs/
- AWS EC2 Documentation: https://docs.aws.amazon.com/ec2/
- Docker Documentation: https://docs.docker.com/

---

**Congratulations!** 🎉 Your application is now deployed on AWS and accessible to recruiters worldwide!
