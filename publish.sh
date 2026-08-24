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

# Parse arguments
DRY_RUN=false
for arg in "$@"; do
    case "$arg" in
        --dry-run|-n)
            DRY_RUN=true
            ;;
        -h|--help)
            echo "Usage: $0 [--dry-run|-n]"
            echo "  --dry-run, -n   Run every step (checks, clean, tests, coverage gate)"
            echo "                  EXCEPT the actual Maven Central publish."
            exit 0
            ;;
        *)
            echo_error "Unknown argument: $arg"
            echo "Usage: $0 [--dry-run|-n]"
            exit 1
            ;;
    esac
done

# Get version from libs.versions.toml
VERSION=$(grep 'flexiLoggerVersion' gradle/libs.versions.toml | sed 's/.*= *"\(.*\)"/\1/')
echo_info "FlexiLogger version: $VERSION"

if [[ "$DRY_RUN" == true ]]; then
    echo_warn "DRY RUN — running all checks, tests and the coverage gate, but NOT publishing."
fi

# Check for uncommitted changes
if [[ -n $(git status --porcelain) ]]; then
    echo_error "You have uncommitted changes. Please commit or stash them before publishing."
    git status --short
    exit 1
fi

# Check we're on master branch (skipped in dry-run)
BRANCH=$(git branch --show-current)
if [[ "$BRANCH" != "master" ]]; then
    if [[ "$DRY_RUN" == true ]]; then
        echo_warn "On branch '$BRANCH', not 'master' — branch check skipped in dry-run."
    else
        echo_warn "You are on branch '$BRANCH', not 'master'."
        read -p "Continue anyway? (y/N) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
fi

# Check for required credentials (environment variables or gradle.properties)
GRADLE_PROPS="$HOME/.gradle/gradle.properties"
HAS_USERNAME=false
HAS_PASSWORD=false
HAS_SIGNING=false
FROM_ENV=false
FROM_PROPS=false

# Check environment variables
# Only the names Gradle itself reads count here. Aliases such as
# MAVEN_CENTRAL_USERNAME or SIGNING_KEY are never read by Gradle, so accepting
# them would pass this check and then fail later at signing or upload.
if [[ -n "$ORG_GRADLE_PROJECT_mavenCentralUsername" ]]; then
    HAS_USERNAME=true
    FROM_ENV=true
fi
if [[ -n "$ORG_GRADLE_PROJECT_mavenCentralPassword" ]]; then
    HAS_PASSWORD=true
    FROM_ENV=true
fi
if [[ -n "$ORG_GRADLE_PROJECT_signingInMemoryKey" ]]; then
    HAS_SIGNING=true
    FROM_ENV=true
fi

# Check gradle.properties
if [[ -f "$GRADLE_PROPS" ]]; then
    if grep -q "^mavenCentralUsername=" "$GRADLE_PROPS" 2>/dev/null; then
        HAS_USERNAME=true
        FROM_PROPS=true
    fi
    if grep -q "^mavenCentralPassword=" "$GRADLE_PROPS" 2>/dev/null; then
        HAS_PASSWORD=true
        FROM_PROPS=true
    fi
    if grep -q "^signing\." "$GRADLE_PROPS" 2>/dev/null; then
        HAS_SIGNING=true
        FROM_PROPS=true
    fi
fi

# Report missing credentials
MISSING_CREDS=false

if [[ "$HAS_USERNAME" == false ]]; then
    echo_error "Missing Maven Central username."
    MISSING_CREDS=true
fi

if [[ "$HAS_PASSWORD" == false ]]; then
    echo_error "Missing Maven Central password/token."
    MISSING_CREDS=true
fi

if [[ "$HAS_SIGNING" == false ]]; then
    if [[ "$DRY_RUN" == true ]]; then
        echo_warn "No signing configuration found — ignored in dry-run (a real publish would fail here)."
    else
        echo_error "No signing configuration found. Maven Central rejects unsigned artifacts."
        MISSING_CREDS=true
    fi
fi

if [[ "$MISSING_CREDS" == true ]]; then
    if [[ "$DRY_RUN" == true ]]; then
        echo_warn "Missing publish credentials — ignored in dry-run (a real publish would fail here)."
    else
        echo ""
        echo "Credentials live in 1Password, not on disk. Publish with:"
        echo ""
        echo "  flexipublish            # defined at the end of ~/.zshrc"
        echo ""
        echo "It reads the signing key and Sonatype token from 1Password, exports"
        echo "the five variables below, runs this script, then unsets them again:"
        echo "  ORG_GRADLE_PROJECT_mavenCentralUsername"
        echo "  ORG_GRADLE_PROJECT_mavenCentralPassword"
        echo "  ORG_GRADLE_PROJECT_signingInMemoryKey"
        echo "  ORG_GRADLE_PROJECT_signingInMemoryKeyId"
        echo "  ORG_GRADLE_PROJECT_signingInMemoryKeyPassword"
        echo ""
        echo "Those five are the only names Gradle reads. On a machine without the"
        echo "1Password CLI they can go in ~/.gradle/gradle.properties instead, but"
        echo "that puts the signing key back on disk — which this project"
        echo "deliberately moved away from."
        exit 1
    fi
fi

if [[ "$FROM_ENV" == true && "$FROM_PROPS" == true ]]; then
    echo_info "Credentials found in the environment and ~/.gradle/gradle.properties (environment wins)."
elif [[ "$FROM_ENV" == true ]]; then
    echo_info "Credentials found in the environment (exported from 1Password)."
elif [[ "$FROM_PROPS" == true ]]; then
    echo_info "Credentials found in ~/.gradle/gradle.properties."
else
    echo_warn "No credentials found in the environment or ~/.gradle/gradle.properties."
fi

# Clean, test and verify coverage BEFORE prompting — fail fast so a broken
# build never waits on (or wastes) the publish confirmation.
echo_info "Cleaning previous build..."
./gradlew clean

echo_info "Running tests and verifying coverage..."
if ! ./gradlew allTests koverVerify; then
    echo_error "Tests or coverage verification failed. Fix before publishing."
    exit 1
fi
echo_info "All tests passed and the coverage gate is satisfied."

# In dry-run, stop here — everything except the actual publish has run.
if [[ "$DRY_RUN" == true ]]; then
    echo ""
    echo_info "================================================"
    echo_info "  DRY RUN complete for FlexiLogger v$VERSION"
    echo_info "================================================"
    echo ""
    echo_info "All pre-publish checks, tests and the coverage gate passed."
    echo_info "Skipped: ./gradlew publishAndReleaseToMavenCentral --no-configuration-cache"
    echo_info "Re-run without --dry-run to publish for real."
    exit 0
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

# Publish
echo_info "Publishing to Maven Central..."
./gradlew publishAndReleaseToMavenCentral --no-configuration-cache

# Tag the released commit and push the tag (only after a successful publish).
TAG="v$VERSION"
if git rev-parse -q --verify "refs/tags/$TAG" >/dev/null; then
    echo_warn "Tag $TAG already exists — skipping tag creation."
else
    echo_info "Tagging release as $TAG and pushing..."
    git tag -a "$TAG" -m "Release $TAG"
    git push origin "$TAG"
    echo_info "Pushed tag $TAG."
fi

echo ""
echo_info "================================================"
echo_info "  Successfully published FlexiLogger v$VERSION!"
echo_info "================================================"
echo ""
echo_info "The artifacts should be available on Maven Central shortly."
echo_info "Check: https://central.sonatype.com/artifact/io.github.projectdelta6/flexilogger"