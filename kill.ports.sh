#!/bin/bash

# This script takes one or more port numbers as arguments and attempts to stop
# Usage: ./stop port1 [port2 ... portN]

# Function to print usage instructions
usage() {
    printf "Usage: %s port1 [port2 ... portN]\n" "$(basename "$0")" >&2
    return 1
}

# Validate that at least one port is provided as an argument
if [[ "$#" -lt 1 ]]; then
    usage
    exit 1
fi

# Function to stop processes on a specific port
# Iterate over all provided ports and attempt to stop processes
stop_process_on_port() {
    local port=$1
    if lsof_output=$(lsof -ti :"$port" 2>/dev/null); then
        printf "Killing process(es) on port %s...\n" "$port"
        if kill -9 $lsof_output; then
            printf "Successfully killed process(es) on port %s.\n" "$port"
        else
            printf "Failed to kill process(es) on port %s.\n" "$port" >&2
        fi
    else
        printf "No process running on port %s.\n" "$port"
    fi
}

for port in "$@"; do
    if [[ ! "$port" =~ ^[0-9]+$ ]]; then
        printf "Invalid port: %s. Ports must be numeric.\n" "$port" >&2
        continue
    fi
    stop_process_on_port "$port"
done
