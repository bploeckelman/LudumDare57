#!/bin/bash

# Set the transparency color (e.g., RGBA 0,0,0,0 for full transparency)
TRANSPARENCY="rgba(0,0,0,0)"

# Loop through all PNG images in subdirectories
find . -type f -name "*.png" -print0 | while IFS= read -r -d $'\0' file; do
  # Get the directory of the current file
  dir=$(dirname "$file")
  # Get the filename without the directory
  filename=$(basename "$file")

  # Create a temporary file to store the modified image
  temp_file=$(mktemp)

  # Use ImageMagick's convert to add transparency to the border
  convert "$file" -alpha set -bordercolor "$TRANSPARENCY" -border 1x1 "$temp_file"

  # Replace the original file with the modified one
  mv "$temp_file" "$file"

  echo "Processed: $file"
done

echo "Image processing complete."
