<head>
    <meta name='doc-spec' content="OpenUniverseSpecVer-1.01">
    <meta name='doc-disabled' content="false">
    <meta name="title" content="Hello, Universe!">
    <meta name="author" content="k2Xzny">
    <meta name="keywords" content="OpenUniverse, example, markdown">
</head>

# Hello Universe (Root + Trigger + Plugins) Example

## Root Document

`root.json`

```json
   {
      "head": {
         "doc_spec": "OpenUniverseSpecVer-1.01",
         "doc_type": "root",
         "name": "Root",
         "disabled": false,
         "description": "Controls execution of OpenUniverse and JVM health triggers using tag and priority filters.",
         "tags": [
            "example",
            "check_file"
         ]
      },
      "events": "tags:(example AND check_file) AND attr.check:size",
      "jobs": "tags:(example AND check_file)  AND my*",
      "triggers": "tags:(example AND check_file AND monitoring) AND attr.priority:[03 TO *]"
   },
```

`system.json`

```json
{
      "head": {
         "doc_spec": "OpenUniverseSpecVer-1.01",
         "doc_type": "system",
         "name": "my sys",
         "description": "My system definition",
         "tags": [
            "example",
            "check_file",
            "os",
            "file"
         ],
         "attr": {
            "firmware": "005_072_138_beta",
            "mission_zone": "warehouse_A7"
         }
      },
      "system_def": {
         "file": "${sys:java.io.tmpdir}/my.txt"
      }
   }
```

`event_processor.json`

```json
{
      "head": {
         "doc_spec": "OpenUniverseSpecVer-1.01",
         "doc_type": "event_processor",
         "name": "my processor",
         "description": "Config file validator",
         "tags": [
            "example",
            "check_file",
            "old"
         ],
         "attr": {
            "range_detection": "none",
            "location_priority": "high"
         }
      },
      "command": {
         "cmd": "#/my-scripts/fileValidator.py",
         "error_log_file": "/tmp/err.txt",
         "instances_count": 1
      }
   }
```

`my-scripts/fileValidator.py` `+x`

```python
#!/usr/bin/env python3

import sys
import json
import os
from pathlib import Path

def read_json_from_stdin():
    """Reads JSON from stdin by balancing braces."""
    buffer = ""
    brace_count = 0

    while True:
        char = sys.stdin.read(1)  # Read one character at a time
        if not char:  # End of input
            break

        buffer += char

        # Count opening and closing braces
        if char == '{':
            brace_count += 1
        elif char == '}':
            brace_count -= 1

        # If braces are balanced and buffer is not empty, we assume a full JSON object is read
        if brace_count == 0 and buffer.strip():
            return buffer.strip()
    return None

def check_file_size(json_data):
    """Checks the file size against the maxFileSize field."""
    try:
        root = json.loads(json_data)

        # Extract required fields
        max_file_size = root.get("maxFileSize")
        system = root.get("system", {})
        file_path = system.get("file")
        file_path = str(Path(file_path).expanduser())
        if not isinstance(max_file_size, int) or not isinstance(file_path, str):
            print(json.dumps({}))  # Return empty JSON object
            sys.stdout.flush()
            return

        # Check file size
        if os.path.exists(file_path):
            file_size = os.path.getsize(file_path)
            if file_size > max_file_size:
                # Print JSON to stdout
                output = {
                    "eventType": "InvalidFileSize",
                    "file": file_path,
                    "fileSize": file_size,
                    "maxFileSize": max_file_size,
                    "message": "Invalid file size detected!"
                }
                print(json.dumps(output))
                sys.stdout.flush()
                return
        # Return empty JSON object if conditions are not met
        print(json.dumps({}))
        sys.stdout.flush()

    except json.JSONDecodeError:
        print(json.dumps({}))  # Handle invalid JSON
        sys.stdout.flush()

if __name__ == "__main__":
    while True:
        json_data = read_json_from_stdin()
        if json_data:
            check_file_size(json_data)
        else:
            print(json.dumps({}))  # Output empty JSON object in case of no input or incomplete data
            sys.stdout.flush()
            break
```

`job.json`

```json
   {
      "head": {
         "doc_spec": "OpenUniverseSpecVer-1.01",
         "doc_type": "job",
         "name": "my Job",
         "description": "Ny job definition",
         "tags": [
            "example",
            "check_file"
         ]
      },
      "events": "tags:(example AND check_file) AND attr.check:size",
      "processors": "tags:(example AND check_file) AND attr.location_priority:high",
      "systems": "tags:(example AND check_file) AND attr.mission_zone:warehouse_A7"
   }
```

`event.json`

```json
{
      "head": {
         "doc_spec": "OpenUniverseSpecVer-1.01",
         "doc_type": "event",
         "name": "checkFileSize",
         "description": "яяяяяя ddddddd",
         "tags": [
            "example",
            "check_file",
            "validate"
         ],
         "attr": {
            "config": "true",
            "check": "size"
         }
      },
      "processable": true
   }
```

`trigger.json`

```json
   {
      "head": {
         "doc_spec": "OpenUniverseSpecVer-1.01",
         "doc_type": "triggers/event_scheduler",
         "name": "validator",
         "description": "Schedules recurring health events for OpenUniverse and JVM monitoring.",
         "tags": [
            "example",
            "check_file",
            "health",
            "system",
            "monitoring"
         ],
         "attr": {
            "priority": "07"
         }
      },
      "schedulers_list": [
         {
            "event_type": "checkFileSize",
            "cron_expression": "0/15 * * * * ?",
            "maxFileSize": 16
         }
      ]
   }
```