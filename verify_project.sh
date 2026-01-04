#!/bin/bash

# Quiz Answerer - Project Verification Script
# This script verifies all necessary files are in place

echo "╔════════════════════════════════════════════════════════╗"
echo "║   Quiz Answerer - Project Verification                ║"
echo "╚════════════════════════════════════════════════════════╝"
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1"
        return 0
    else
        echo -e "${RED}✗${NC} $1 ${RED}(MISSING)${NC}"
        return 1
    fi
}

check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $1/"
        return 0
    else
        echo -e "${RED}✗${NC} $1/ ${RED}(MISSING)${NC}"
        return 1
    fi
}

ERRORS=0

echo "📁 Checking Project Structure..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Core Gradle files
echo ""
echo "Build Configuration:"
check_file "build.gradle.kts" || ((ERRORS++))
check_file "settings.gradle.kts" || ((ERRORS++))
check_file "gradle.properties" || ((ERRORS++))
check_file "app/build.gradle.kts" || ((ERRORS++))
check_file "gradlew" || ((ERRORS++))
check_file "gradlew.bat" || ((ERRORS++))

# Source files
echo ""
echo "Kotlin Source Files:"
check_file "app/src/main/java/com/quizanswerer/app/MainActivity.kt" || ((ERRORS++))
check_file "app/src/main/java/com/quizanswerer/app/ScreenshotService.kt" || ((ERRORS++))
check_file "app/src/main/java/com/quizanswerer/app/GeminiHelper.kt" || ((ERRORS++))
check_file "app/src/main/java/com/quizanswerer/app/QuestionParser.kt" || ((ERRORS++))
check_file "app/src/main/java/com/quizanswerer/app/Question.kt" || ((ERRORS++))

# Android resources
echo ""
echo "Android Resources:"
check_file "app/src/main/AndroidManifest.xml" || ((ERRORS++))
check_file "app/src/main/res/layout/activity_main.xml" || ((ERRORS++))
check_file "app/src/main/res/values/strings.xml" || ((ERRORS++))
check_file "app/src/main/res/values/colors.xml" || ((ERRORS++))
check_file "app/src/main/res/values/themes.xml" || ((ERRORS++))

# Documentation
echo ""
echo "Documentation Files:"
check_file "README.md" || ((ERRORS++))
check_file "SETUP_GUIDE.md" || ((ERRORS++))
check_file "EXAMPLES.md" || ((ERRORS++))
check_file "API_KEY_INFO.md" || ((ERRORS++))
check_file "PROJECT_SUMMARY.md" || ((ERRORS++))
check_file "QUICK_START.txt" || ((ERRORS++))

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Check API key
echo ""
echo "🔑 Verifying API Key Configuration..."
if grep -q "AIzaSyDLZz0FwL2TayC-ocr9c_AOsNq6Tkqf8hQ" app/src/main/java/com/quizanswerer/app/MainActivity.kt; then
    echo -e "${GREEN}✓${NC} API Key is configured in MainActivity.kt"
else
    echo -e "${RED}✗${NC} API Key not found in MainActivity.kt"
    ((ERRORS++))
fi

# Count lines of code
echo ""
echo "📊 Code Statistics..."
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
KOTLIN_LINES=$(find app/src/main/java -name "*.kt" 2>/dev/null | xargs wc -l 2>/dev/null | tail -1 | awk '{print $1}')
XML_LINES=$(find app/src/main/res -name "*.xml" 2>/dev/null | xargs wc -l 2>/dev/null | tail -1 | awk '{print $1}')
echo "Kotlin code: ${KOTLIN_LINES:-0} lines"
echo "XML resources: ${XML_LINES:-0} lines"

# Check permissions
echo ""
echo "🔐 Checking Gradlew Permissions..."
if [ -x "gradlew" ]; then
    echo -e "${GREEN}✓${NC} gradlew is executable"
else
    echo -e "${YELLOW}⚠${NC} gradlew is not executable (run: chmod +x gradlew)"
fi

# Summary
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ PROJECT VERIFICATION SUCCESSFUL!${NC}"
    echo ""
    echo "All required files are present and properly configured."
    echo "Your Quiz Answerer app is ready to build and run!"
    echo ""
    echo "Next steps:"
    echo "  1. Open project in Android Studio"
    echo "  2. Wait for Gradle sync"
    echo "  3. Click Run button"
    echo ""
else
    echo -e "${RED}❌ PROJECT VERIFICATION FAILED${NC}"
    echo ""
    echo "Found $ERRORS error(s). Please check missing files above."
    echo ""
fi

echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "For more information, see:"
echo "  • README.md          (Complete documentation)"
echo "  • SETUP_GUIDE.md     (Quick setup guide)"
echo "  • QUICK_START.txt    (Command reference)"
echo ""
