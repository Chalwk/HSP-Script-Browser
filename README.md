# HSP Script Browser

A desktop application for browsing and downloading SAPP Lua scripts from the HALO SCRIPT PROJECTS GitHub repository.

![HSP Script Browser](https://img.shields.io/badge/Java-11+-blue?style=for-the-badge&logo=java)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

## Overview

HSP Script Browser provides an intuitive graphical interface to explore, search, and download scripts from
the [HALO SCRIPT PROJECTS](https://github.com/Chalwk/HALO-SCRIPT-PROJECTS) repository. It eliminates the need to
manually browse GitHub and download individual script files.

## Features

- **Organized Categories**: Browse scripts by category (Attractive, Custom Games, Utility)
- **Advanced Search**: Fuzzy search across script titles, descriptions, and categories
- **Modern UI**: Clean, responsive interface with dark theme
- **Quick Downloads**: One-click script downloading with progress tracking
- **GitHub Integration**: Direct links to view scripts on GitHub
- **Keyboard Shortcuts**: Efficient navigation with keyboard support
- **Statistics**: Real-time script counts and filtering statistics

## Quick Start

### Download Pre-built Executable

1. Go to the [Releases](https://github.com/Chalwk/HSP-Script-Browser/releases) page
2. Download the latest `HSPScriptBrowser.exe`
3. Double-click to run (requires Java 11 or later)

### System Requirements

- **Windows** 7 or later (64-bit recommended)
- **Java** 11 or later ([Download Java](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html))
- Internet connection for fetching script metadata

## Building from Source

### Prerequisites

- **Java Development Kit (JDK)** 11 or later
- **Apache Maven** 3.6 or later
- **Git** (optional, for cloning repository)

### Compilation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/Chalwk/HSP-Script-Browser.git
   cd HSP-Script-Browser
   ```

2. **Build with Maven**
   ```bash
   mvn clean package
   ```

3. **Locate the output files**
   After successful build, check the `target/` directory for:
    - `hsp-script-browser-1.0.0.jar` (Executable JAR)
    - `hsp-script-browser-1.0.0.exe` (Windows executable)

### Running the Application

**Option 1: Using the EXE (Windows)**

```bash
# Double-click the EXE file or run from command line:
target/hsp-script-browser-1.0.0.exe
```

**Option 2: Using the JAR file**

```bash
java -jar target/hsp-script-browser-1.0.0.jar
```

## Usage Guide

### Browsing Scripts

1. **Select a Category**: Use the dropdown to filter by script type
2. **Search**: Type in the search box to find specific scripts
3. **View Details**: Click on any script to see its full description

### Downloading Scripts

1. Select a script from the list
2. Click "Download Script"
3. Choose where to save the file
4. Wait for download completion

### Keyboard Shortcuts

- `Ctrl + F` - Focus search field
- `Enter` - Download selected script
- `Escape` - Clear selection and search

## Project Structure

```
src/main/java/com/chalwk/
├── HSPScriptBrowser.java      # Main application window
├── model/
│   ├── ScriptMetadata.java    # Script data model
│   └── ScriptCategory.java    # Category enumeration
├── service/
│   └── ScriptService.java     # GitHub API and download logic
└── ui/
    ├── Buttons.java           # Custom button component
    └── ScriptListRenderer.java # Script list cell renderer
```

## Technical Details

- **Framework**: Java Swing
- **Build Tool**: Apache Maven
- **Packaging**: Maven Assembly Plugin + Launch4j
- **Dependencies**:
    - `org.json:json` - JSON parsing
- **Minimum Java Version**: 11

## Troubleshooting

### Common Issues

**"Java not found" error when running EXE**

- Install Java 11 or later from [Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)

**Application fails to start**

- Ensure you have internet connection for initial script loading
- Check if your firewall is blocking the application

**Scripts not loading**

- Verify GitHub is accessible from your network
- Check the status page: [GitHub Status](https://www.githubstatus.com/)

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [HALO SCRIPT PROJECTS](https://github.com/Chalwk/HALO-SCRIPT-PROJECTS) - The script repository
- [SAPP](https://github.com/SapphireSAPP/SAPP) - Halo Custom Edition server modification
- Icons and UI inspiration from modern design systems

---