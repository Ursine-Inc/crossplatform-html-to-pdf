# Use Ubuntu as base image for Wine support
FROM ubuntu:22.04

# Avoid interactive prompts during package installation
ENV DEBIAN_FRONTEND=noninteractive

# Install Wine and dependencies (64-bit only, no i386 for ARM compatibility)
RUN apt-get update && \
    apt-get install -y \
    software-properties-common \
    wget \
    gnupg2 \
    xvfb \
    x11vnc \
    fluxbox \
    openjdk-21-jre \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Set up display environment
ENV DISPLAY=:99

# Create working directory
WORKDIR /app

# Copy the Windows/Mac app-image (will be built before running docker)
# Note: On macOS the jpackage output is .app, we'll handle both cases
COPY build/jpackage/ /app/jpackage/

# Create a startup script that detects what was built
RUN echo '#!/bin/bash\n\
# Start virtual display\n\
Xvfb :99 -screen 0 1024x768x16 &\n\
sleep 2\n\
\n\
# Start window manager\n\
fluxbox &\n\
sleep 2\n\
\n\
# Find the app directory\n\
if [ -d "/app/jpackage/HTMLtoPDF" ]; then\n\
    APP_DIR="/app/jpackage/HTMLtoPDF"\n\
elif [ -d "/app/jpackage/HTMLtoPDF.app" ]; then\n\
    APP_DIR="/app/jpackage/HTMLtoPDF.app"\n\
else\n\
    echo "Error: Cannot find HTMLtoPDF app"\n\
    ls -la /app/jpackage/\n\
    exit 1\n\
fi\n\
\n\
echo "Found app at: $APP_DIR"\n\
echo "App contents:"\n\
ls -la "$APP_DIR"\n\
\n\
# Try to find and run the launcher\n\
if [ -f "$APP_DIR/bin/HTMLtoPDF" ]; then\n\
    echo "Running Java launcher..."\n\
    cd "$APP_DIR/bin"\n\
    ./HTMLtoPDF\n\
elif [ -f "$APP_DIR/Contents/MacOS/HTMLtoPDF" ]; then\n\
    echo "Running Mac launcher..."\n\
    "$APP_DIR/Contents/MacOS/HTMLtoPDF"\n\
else\n\
    echo "Error: Cannot find launcher script"\n\
    find "$APP_DIR" -name "HTMLtoPDF*"\n\
    exit 1\n\
fi\n\
' > /app/start.sh && chmod +x /app/start.sh

# Expose VNC port if you want to view the GUI
EXPOSE 5900

CMD ["/app/start.sh"]
