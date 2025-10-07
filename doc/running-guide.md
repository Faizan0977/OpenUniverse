# Running OpenUniverse

## OpenUniverse is available in multiple formats
All formats support the same command-line interface.

| Format                             | File                     | Platform          | Requirements   | Notes                                                                   |
| ---------------------------------- | ------------------------ | ----------------- | -------------- | ----------------------------------------------------------------------- |
| **Linux x86\_64**                  | `ou-linux-x86_64`        | Linux (64-bit)    | None           | Best default choice, works out-of-the-box on most modern Linux systems. |
| **Linux arch-agnostic executable** | `ou`                     | Linux (any arch)  | Java installed | Runs via system Java.                               |
| **Executable JAR**                 | `ou.jar`                 | Cross-platform    | Java installed | Portable, can be run on Linux, macOS, Windows.                          |
| **Java Library**                   | `ou.jar` (as dependency) | Java environments | Java installed | Can be invoked programmatically from your own Java projects.            |

The best choice depends on whether you already have Java installed and how you like to start programs.

> [!NOTE]
> If you are not sure which release type to choose, you will most likely need the **Linux x86_64** release.<br>
> It works on most modern Linux systems without extra setup.

## 1. Using the Linux x86_64 Executable

Make the file executable if necessary:

```bash
chmod +x ou-x86_64
./ou-linux-x86_64 '/path/to/your/repo' start --stdout
```

This format includes all dependencies and does not require Java to be installed separately.

> [!NOTE]
Releases for other CPU architectures available here: [Releases](https://github.com/ou-org/OpenUniverse/releases).

## 2. Using the Linux Arch Agnostic Executable – *Needs Java*

Requirements:
- Java 17 or newer

> **Note:** See [Java Search Order](java-search-order.md) document.

Make the binary executable if necessary:

```bash
chmod +x ou
./ou '/path/to/your/repo' start --stdout
```

## 3. Using the Executable JAR (Crossplatform) – *Needs Java*

Requirements:
- Java 17 or newer

> **Note:** See [Java Search Order](java-search-order.md) document.

To run:

```bash
java -jar ou.jar '/path/to/your/repo' start --stdout
```

## 4. Running from Java Code (Crossplatform Developers) – *Needs Java*

Requirements:
- Java 17 or newer

You can invoke OpenUniverse directly from your Java application:

```java
import org.ou.main.Main;

public class MyApp {
    public static void main(String[] args) {
        Main.main(new String[] { "/path/to/your/repo", "start", "--stdout" });
    }
}
```

Compile and run with `ou.jar` on the classpath:

```bash
javac -cp ou.jar MyApp.java
java -cp .:ou.jar MyApp
```

On Windows, use `;` instead of `:` in the classpath.

## See Also:

- [Java Search Order](java-search-order.md)
- [Java Requirement](doc/java-requirement.md)
- [Command-line Interface](command-line-interface.md)
- [Build Guide](build-guide.md)
