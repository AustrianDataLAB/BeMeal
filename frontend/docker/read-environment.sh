#!/bin/sh


# Check if the environment variable ABC is set
if [ -z "$BACKEND_URL" ]; then
    echo "Environment variable BACKEND_URL is not set."
    exit 1
fi

# Create a JSON object with the environment variable ABC
envsubst < "$FRONTEND_ENV_TEMPLATE" > "$FRONTEND_ENV"

echo "BACKEND_URL has been copied to $FRONTEND_ENV."
