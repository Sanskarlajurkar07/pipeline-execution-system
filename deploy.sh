#!/bin/bash

# Pipeline Execution System - Quick Deploy Script
# This script helps you deploy the application quickly

echo "🚀 Pipeline Execution System - Deployment Script"
echo "================================================"
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    echo "Visit: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ docker-compose is not installed. Please install docker-compose first."
    exit 1
fi

echo "✅ Docker and docker-compose are installed"
echo ""

# Check for GEMINI_API_KEY
if [ -z "$GEMINI_API_KEY" ]; then
    echo "⚠️  GEMINI_API_KEY environment variable is not set"
    echo ""
    read -p "Enter your Gemini API key: " api_key
    export GEMINI_API_KEY=$api_key
    echo ""
fi

echo "✅ API key configured"
echo ""

# Ask deployment type
echo "Select deployment option:"
echo "1) Development (with logs visible)"
echo "2) Production (detached mode)"
echo ""
read -p "Enter your choice (1 or 2): " choice

case $choice in
    1)
        echo ""
        echo "🔨 Building and starting in development mode..."
        docker-compose up --build
        ;;
    2)
        echo ""
        echo "🔨 Building and starting in production mode..."
        docker-compose up --build -d
        echo ""
        echo "✅ Application deployed successfully!"
        echo ""
        echo "📍 Access points:"
        echo "   Frontend: http://localhost"
        echo "   Backend API: http://localhost:8080"
        echo ""
        echo "📊 View logs:"
        echo "   docker-compose logs -f"
        echo ""
        echo "🛑 Stop application:"
        echo "   docker-compose down"
        ;;
    *)
        echo "❌ Invalid choice. Exiting."
        exit 1
        ;;
esac
