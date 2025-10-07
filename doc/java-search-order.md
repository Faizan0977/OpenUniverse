# Java Search Order

## 1. **Embedded JRE Directory (`./jre/bin/java`)**

   - The script first checks if there's a `jre` directory in the same location
     as the script (`./jre/bin/java`).
   - If this Java executable exists and is executable, it takes top priority.
   - This allows bundling a dedicated Java runtime with your application.
   - **Example:**
     ```
     ou-parent-dir/
     ├── ou (OpenUniverse executable here)
     └── jre/
         └── bin/
             └── java (java executable here)
     ```

## 2. **Custom Java via `OU_JAVA_HOME` Environment Variable**

   - If no embedded JRE is found, the script checks the `OU_JAVA_HOME`
     environment variable.
   - If set, the script uses `$OU_JAVA_HOME/bin/java`.
   - **Example:**
     ```bash
     export OU_JAVA_HOME=/path/to/specific/java
     ```

## 3. **Java from `JAVA_HOME` Environment Variable**

   - If `OU_JAVA_HOME` is not set, the script checks for `JAVA_HOME`.
   - If set, the script uses `$JAVA_HOME/bin/java`.
   - **Example:**
     ```bash
     export JAVA_HOME=/path/to/specific/java
     ```

## 4. **System Java (`java` in `PATH`)**

   - If neither environment variable is set, the script tries the system `java`
     via `java -version`.
   - If valid, `java` is used as-is.
   - **Example:**
     ```bash
     java -version
     ```

## 5. **Failure to Find Java**

   - If no valid Java executable is found by any method, the script outputs:
     ```
     Java not found.
     ```
   - Then exits with status code `1`.

## See Also:

- [Running Guide](running-guide.md)
- [Java Requirement](doc/java-requirement.md)

