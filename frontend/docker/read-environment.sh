#!/bin/sh

# Define the output JSON file
output_file="/bemeal-frontend/assets/env.json"

# Check if the environment variable ABC is set
if [ -z "$BACKEND_URL" ]; then
    echo "Environment variable BACKEND_URL is not set."
    exit 1
fi

# Create a JSON object with the environment variable ABC
echo "{ \"BACKEND_URL\": \"$BACKEND_URL\" }" > "$output_file"

echo "BACKEND_URL has been copied to $output_file."
