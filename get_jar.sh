#!/bin/bash

echo "Downloading EtherealMind v3.0.0 JAR..."

# Check if release has assets
ASSETS=$(gh release view v3.0.0 --json assets -q '.assets[].name' 2>/dev/null)

if [ -z "$ASSETS" ]; then
    echo "No JAR found in release yet. The build workflow may still be running."
    echo "Checking workflow status..."
    gh run list --workflow=upload-release-jar.yml --limit=1
    echo ""
    echo "Alternative: Download the JAR from one of these previous releases:"
    echo ""
    # List previous release JARs
    gh release list --limit=5
    echo ""
    echo "Or wait a few minutes and run this script again."
else
    echo "Found JAR: $ASSETS"
    gh release download v3.0.0 -D build/libs/ --clobber
    echo "Downloaded to: build/libs/"
    ls -la build/libs/*.jar
fi