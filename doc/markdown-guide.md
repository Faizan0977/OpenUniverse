# OpenUniverse Markdown Guide

## Overview

OpenUniverse transforms Markdown documents into structured JSON/YAML and extracts embedded plugins, scripts, or binary content for execution or storage.
Markdown documents are the primary source for defining jobs, triggers, events, and orchestrated components.

## Metadata

Metadata is declared in the Markdown header using HTML meta tags:

```html
<head>
    <!-- Required for OpenUniverse to process the document -->
    <meta name='doc-spec' content="OpenUniverseSpecVer-1.01">
    
    <!-- Optional: if true, the document will be ignored during scanning phase -->
    <meta name='doc-disabled' content="false">
    
    <!-- Optional user-defined metadata -->
    <meta name='title' content="Hello Universe!">
    <meta name='author' content="John">
    <meta name='keywords' content="openuniverse, docs, metadata">
    ...
</head>
```

* `doc-spec` must be exactly `OpenUniverseSpecVer-1.01`.
* If `doc-disabled` is `true`, the document is ignored during scanning phase.

## Filename Line Before Code Blocks

Files to be extracted are specified with a file name immediately preceding a code block:

```text

`file.py` `+x`

```bash
#!/bin/bash
echo "Executable script"
...

```

* `file.ext` → name of the file to create.
* `+x` → optional; if present, the file is made executable (for owner).
* If no filename line is provided, the code block is treated as non-extractable and not saved.
* Supports all languages, including `bytes:*` (bin, oct, dec, hex, base64, base64url).

## Code Block Languages

OpenUniverse supports:

* Scripts like bash, python etc. (saved as UTF-8 text).
* Binary files: `bytes:*` (converted to raw bytes before writing).

Supported `bytes:*` encodings:

* `bytes:hex` → hexadecimal
* `bytes:bin` → binary (0/1)
* `bytes:oct` → octal
* `bytes:dec` → decimal
* `bytes:base64` → Base64
* `bytes:base64url` → Base64 URL-encoded

Whitespace characters are allowed.


## Executable Files (`+x`)

Example:

`run.sh` `+x`
```bash
#!/bin/bash
echo "Executable script"
```

* Owner-only: read + execute.
* Non-executable files (JSON/YAML/TXT) remain read-only.
* Executable files cannot be written by the owner (read+execute only).

## File Permissions

| File Type                  | Permission | Description                                |
| -------------------------- | ---------- | ------------------------------------------ |
| Executable (`+x`)          | --x------  | Owner can execute                          |
| Document files (JSON/YAML) |            |                                            |

## Recursive Directory Processing

* OpenUniverse can traverse directories recursively to find `.md` files.
* Extract all files according to rules above.
* Maintains proper permissions for executable vs. data files.
* Non-supported file types can be skipped or logged.

## Examples

### JSON File

`task.json`
```json
{
  "task": "backup",
  "time": "02:00"
}
```

### Python Script

```
hello.py +x
```python
#!/usr/bin/env python3
print("Hello from Python!")
```

### Binary Files

`mysecret.bin`
```bytes:base64
SGVsbG8gQmluYXJ5IQ==
```

`myfile.hex`
```bytes:hex
48 65 6c 6c 6f
```

`myfile.bin`
```bytes:bin
01001000 01101001
```

`myfile.oct`
```bytes:oct
110 145 154
```

`myfile.dec`
```bytes:dec
072 101 108
```

## Best Practices

1. Always specify `doc-spec`.
2. Use explicit language for code blocks for correct extraction.
3. Use `+x` only for scripts that must be executed.
4. Keep metadata consistent across documents for automated orchestration.
5. Avoid mixed encodings in binary files.
6. Test `bytes:*` extraction before production use.
