#!/bin/sh
ollama serve &
echo "Waiting for Ollama to start..."
until ollama list > /dev/null 2>&1; do
  sleep 1
done
echo "Pulling llama3.2:1b..."
ollama pull llama3.2:1b
wait