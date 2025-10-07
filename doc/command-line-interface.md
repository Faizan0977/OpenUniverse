# Command-line Interface

## Usage

```text
$ ou help

Usage:

ou [repo dir] <command> [options...] [flags...]

Commands:

 help              Print help message.
 schema            Print schema.
 targets           Print export targets.
 status            Print repository status.

 start             Start process.
 stop              Stop process.

Options:

 --secret,     -s  Secret for encrypted values.

Flags:

 --assume-yes, -y  Start without asking for confirmation.
 --prompt-sec, -p  Prompt for the secret (see: --secret).
 --gui-sec,    -g  Use GUI dialog to prompt for the secret (see: --prompt-sec).

 --stdout,     -o  Print all records (as newline-delimited JSON) to console.
 --no-verify,  -nv Disable pre-run platform self-verification phase.
 --no-color,   -nc Disable ANSI colors (same effect as NO_COLOR env var).

Optional environment variables:

 OU_JAVA_HOME      Path to the JRE directory (first priority).
 JAVA_HOME         Path to the JRE directory (second priority).
 NO_COLOR          Disable ANSI colors (same effect as --no-color, -n flags).

Optional directories:

 jre               Local JRE directory (has common parent with 'ou' executable).

Auto-generated repository files:

 node-id.txt       Contains node ID.
 commit.txt        Contains latest commit hash.
 schema.json       Contains discovered data structure for the repository.
 log.ndjson        System log in newline-delimited JSON format.
 error-log.txt     Error log.

Optional repository files:

 *.json            JSON container (holds documents).
 *.yaml, *.yml     YAML container (holds documents).
 *.md              Markdown container (holds documents and resources).
 .ouignore         Ignore rules (uses the same syntax as .gitignore).

Temporary repository files:

 write-lock.json   Write lock file.

Exit Codes:

   0               The command was successfully executed without errors.
   1               An error occurred during the execution of the command.
 255               An unknown or invalid command, option or flag was provided.

```

## See Also:

- [Running Guide](running-guide.md)
- [Java Search Order](java-search-order.md)

