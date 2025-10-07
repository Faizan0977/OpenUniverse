# Java Runtime Environment (JRE) Requirement

OpenUniverse requires a Java Runtime Environment (JRE) to operate, with **minimum version 24** or higher. This ensures compatibility with the latest language features, performance optimizations, and security enhancements necessary for reliable and efficient execution.

We recommend using the latest stable JRE version available that meets or exceeds this minimum to benefit from ongoing improvements in the Java ecosystem. OpenUniverse is designed to run seamlessly on JRE versions 24+, leveraging modern JVM capabilities for scalability and robustness across Linux environments.

## Obtaining Java

If you do not have a compatible JRE installed, you can obtain it from several trusted sources:

- **Official Oracle JDK/JRE:** Download the latest version from the [Oracle Java downloads page](https://www.oracle.com/java/technologies/downloads/).
- **OpenJDK:** A free and open-source alternative, available from [Adoptium](https://adoptium.net/), [Amazon Corretto](https://aws.amazon.com/corretto/), or your Linux distribution’s package manager.
- **Linux Package Managers:** Most Linux distributions provide OpenJDK packages that can be installed via the system package manager, e.g.,  
```bash
  sudo apt install openjdk-24-jre  # Debian/Ubuntu  
  sudo dnf install java-24-openjdk  # Fedora  
```

Ensure that your installed JRE version meets the minimum version 24 requirement by running:
```bash
java -version
```

## See Also:

- [Running Guide](running-guide.md)
- [Java Search Order](java-search-order.md)

