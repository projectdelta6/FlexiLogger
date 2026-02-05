#!/bin/bash

# FlexiLogger Publishing Script
# Publishes all modules to Maven Central using the Vanniktech Maven Publish plugin

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

echo_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

echo_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Get version from libs.versions.toml
VERSION=$(grep 'flexiLoggerVersion' gradle/libs.versions.toml | sed 's/.*= *"\(.*\)"/\1/')
echo_info "FlexiLogger version: $VERSION"

# Check for uncommitted changes
if [[ -n $(git status --porcelain) ]]; then
    echo_error "You have uncommitted changes. Please commit or stash them before publishing."
    git status --short
    exit 1
fi

# Check we're on master branch
BRANCH=$(git branch --show-current)
if [[ "$BRANCH" != "master" ]]; then
    echo_warn "You are on branch '$BRANCH', not 'master'."
    read -p "Continue anyway? (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# Check for required environment variables
MISSING_ENV=false

if [[ -z "$ORG_GRADLE_PROJECT_mavenCentralUsername" ]] && [[ -z "$MAVEN_CENTRAL_USERNAME" ]]; then
    echo_error "Missing Maven Central username. Set ORG_GRADLE_PROJECT_mavenCentralUsername or MAVEN_CENTRAL_USERNAME"
    MISSING_ENV=true
fi

if [[ -z "$ORG_GRADLE_PROJECT_mavenCentralPassword" ]] && [[ -z "$MAVEN_CENTRAL_PASSWORD" ]]; then
    echo_error "Missing Maven Central password/token. Set ORG_GRADLE_PROJECT_mavenCentralPassword or MAVEN_CENTRAL_PASSWORD"
    MISSING_ENV=true
fi

if [[ -z "$ORG_GRADLE_PROJECT_signingInMemoryKey" ]] && [[ -z "$SIGNING_KEY" ]]; then
    if [[ ! -f ~/.gradle/gradle.properties ]] || ! grep -q "signing.gnupg" ~/.gradle/gradle.properties 2>/dev/null; then
        echo_warn "No signing key found. Ensure GPG signing is configured in ~/.gradle/gradle.properties or via environment variables."
    fi
fi

if [[ "$MISSING_ENV" == true ]]; then
    echo ""
    echo "Required environment variables:"
    echo "  ORG_GRADLE_PROJECT_mavenCentralUsername - Maven Central username"
    echo "  ORG_GRADLE_PROJECT_mavenCentralPassword - Maven Central password/token"
    echo ""
    echo "For signing (one of):"
    echo "  ORG_GRADLE_PROJECT_signingInMemoryKey - ASCII-armored GPG key"
    echo "  ORG_GRADLE_PROJECT_signingInMemoryKeyPassword - Key password"
    echo "  Or configure signing.gnupg.* in ~/.gradle/gradle.properties"
    exit 1
fi

# Confirm publish
echo ""
echo "================================================"
echo "  Publishing FlexiLogger v$VERSION to Maven Central"
echo "================================================"
echo ""
echo "Modules to publish:"
echo "  - io.github.projectdelta6:flexilogger:$VERSION"
echo "  - io.github.projectdelta6:flexilogger-okhttp:$VERSION"
echo "  - io.github.projectdelta6:flexilogger-ktor:$VERSION"
echo ""
read -p "Proceed with publish? (y/N) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo_info "Publish cancelled."
    exit 0
fi

# Clean build
echo_info "Cleaning previous build..."
./gradlew clean

# Run tests
echo_info "Running tests..."
if ! ./gradlew allTests; then
    echo_error "Tests failed. Fix tests before publishing."
    exit 1
fi

# Publish
echo_info "Publishing to Maven Central..."
./gradlew publishAndReleaseToMavenCentral --no-configuration-cache

echo ""
echo_info "================================================"
echo_info "  Successfully published FlexiLogger v$VERSION!"
echo_info "================================================"
echo ""
echo_info "The artifacts should be available on Maven Central shortly."
echo_info "Check: https://central.sonatype.com/artifact/io.github.projectdelta6/flexilogger"