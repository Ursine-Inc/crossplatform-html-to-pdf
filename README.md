# HTML to PDF Converter

A JavaFX-based desktop application that converts HTML files to PDF format, preserving directory structure and styling.

## Features

- Convert individual HTML files or entire directories to PDF
- Preserves original directory structure in output
- Supports CSS styling and embedded resources
- Cross-platform: macOS, Windows, Linux
- Progress tracking for batch conversions

## Prerequisites

- **JDK 21** (required for building and running)
- **Gradle** (included via wrapper)
- **Docker** (optional, for cross-platform builds and testing)

## Development

### Setup

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd html-to-pdf
   ```

2. **Build the project:**
   ```bash
   ./gradlew build
   ```

3. **Run the application:**
   ```bash
   ./gradlew run
   ```

### Key Technologies

- **JavaFX 21**: UI framework
- **OpenHTMLtoPDF**: HTML to PDF conversion engine
- **JSoup**: HTML parsing and cleaning
- **Apache PDFBox**: PDF generation backend

## Building Native Installers

### Local Build (Native Platform)

Build a native installer for your current operating system:

```bash
# macOS: Creates .dmg
# Windows: Creates .exe
# Linux: Creates .deb
./gradlew jpackage
```

Output location: `build/jpackage/`

### Cross-Platform Builds

The project supports building app-images (without installers) for different platforms:

#### macOS (from macOS)
```bash
./gradlew jpackage
```
Creates: `build/jpackage/HTMLtoPDF.app/`

#### Windows (from any OS)
```bash
./gradlew jpackage -PtargetOs=windows
```
Creates: `build/jpackage/HTMLtoPDF/` (app-image only, no .exe)

**Note:** Creating Windows `.exe` installers requires running on Windows due to WiX Toolset dependencies.

#### Linux (from any OS)
```bash
./gradlew jpackage -PtargetOs=linux
```
Creates: `build/jpackage/HTMLtoPDF/` (app-image only on non-Linux, .deb on Linux)

## Testing with Docker

The included Dockerfile allows you to test the built application in a containerized Linux environment with a virtual display.

### Build and Test

1. **Build the jpackage app-image:**
   ```bash
   ./gradlew jpackage
   ```

2. **Build the Docker image:**
   ```bash
   docker build -t htmltopdf-test .
   ```

3. **Run the application in Docker:**
   ```bash
   docker run --rm htmltopdf-test
   ```

### View GUI in Docker (Optional)

To view the application GUI running in Docker:

1. **Run with VNC port exposed:**
   ```bash
   docker run -p 5900:5900 htmltopdf-test
   ```

2. **Connect with a VNC client:**
   - Host: `localhost:5900`
   - You'll see the JavaFX application running

## Usage

1. **Launch the application**
2. **Browse for HTML file or directory:**
   - Click "Browse File" to select a single HTML file
   - Click "Browse Directory" to select a folder containing HTML files
3. **Browse for output directory:**
   - Choose where to save the generated PDFs
4. **Click "Convert":**
   - Progress bar shows conversion status
   - PDFs are created with `_pdf` suffix in the output directory
   - Directory structure is preserved for batch conversions

## Troubleshooting

### Build Issues

**"Unsupported class file major version 67"**
- Ensure you're using JDK 21. Check with: `java -version`
- Set `JAVA_HOME` to JDK 21 location

**jpackage fails with platform-specific options**
- The build script automatically adjusts options based on the target OS
- Cross-platform builds create app-images only (no installers)

### Runtime Issues

**Empty PDFs generated**
- Check that HTML files are well-formed (valid markup)
- Ensure all `<link>` and `<meta>` tags are properly closed
- CSS files should be accessible relative to the HTML file

**Missing fonts or styling**
- External CSS files must be in the correct relative path
- Font files should be embedded or available on the system

### Docker Issues

**Application won't start in container**
- Verify the jpackage build completed successfully
- Check Docker logs: `docker logs <container-id>`
- Ensure the virtual display is running (Xvfb)

## Development Notes

### Adding Dependencies

1. Add to `build.gradle.kts` dependencies section
2. Rebuild: `./gradlew build --refresh-dependencies`

### Modifying the UI

- FXML files are in `src/main/resources/com/ursineenterprises/utilities/htmltopdf/`
- Controllers are in `src/main/java/com/ursineenterprises/utilities/htmltopdf/`
- After changes, rebuild and run: `./gradlew run`

### Debugging

Run with debug logging:
```bash
./gradlew run --debug
```

Or attach a debugger from your IDE to the `run` task.

## License

See [LICENSE](LICENSE) file for details.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

---

**Built with:** JavaFX 21 | Gradle | OpenHTMLtoPDF | PDFBox

