#!/bin/bash

# Cleanup script - Remove AI-generated looking files before pushing to GitHub
# Run this before your first git push

echo "🧹 Cleaning up repository for GitHub..."
echo ""

# Remove Kiro AI files
echo "Removing .kiro/ directory..."
rm -rf .kiro/

# Remove internal documentation that looks AI-generated
echo "Removing internal documentation files..."
rm -f GITHUB_PUSH_CHECKLIST.md
rm -f PROJECT_SUMMARY.md
rm -f CONTRIBUTING.md
rm -f QUICK_START.md
rm -f WHAT_TO_PUSH.md
rm -f cleanup-for-github.sh  # Remove this script itself

# Remove extra deployment guides (keep only 2)
echo "Removing extra deployment guides..."
rm -f AWS_DEPLOYMENT_GUIDE.md  # Keep only DEPLOYMENT.md and RENDER_DEPLOYMENT.md

# Remove any .env files (just in case)
echo "Checking for .env files..."
find . -name ".env" -type f -delete
find . -name "*.env" -type f -delete

# Remove DS_Store files
echo "Removing .DS_Store files..."
find . -name ".DS_Store" -type f -delete
find . -name "DS_Store" -type f -delete

# Remove any log files
echo "Removing log files..."
find . -name "*.log" -type f -delete

echo ""
echo "✅ Cleanup complete!"
echo ""
echo "📁 Your repository now contains only:"
echo "   - Source code (backend & frontend)"
echo "   - README.md"
echo "   - DEPLOYMENT.md"
echo "   - RENDER_DEPLOYMENT.md"
echo "   - Docker files"
echo "   - .gitignore"
echo ""
echo "🚀 Ready to push to GitHub!"
echo ""
echo "Next steps:"
echo "1. git init"
echo "2. git add ."
echo "3. git commit -m 'Initial commit: Pipeline execution system'"
echo "4. git remote add origin <your-github-url>"
echo "5. git push -u origin main"
