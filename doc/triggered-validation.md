# Check File Size: Triggered Validation Example.

This tutorial demonstrates how to use OpenUniverse to periodically validate a file's size using a scheduler, system, job, and event processor. This is a practical example of integrating custom Python logic into the OpenUniverse orchestration pipeline.

## Step 1: Create `CheckFileSize.json`

Save the following configuration as `CheckFileSize.json` in your repository:

```json
[
  {
    "head": {
      "doc_type": "root",
      "name": "Root",
      "disabled": false,
      "description": "Controls execution of OpenUniverse and JVM health triggers using tag and priority filters.",
      "tags": ["example", "check_file"]
    },
    "events": "tags:(example AND check_file) AND attr.check:size",
    "jobs": "tags:(example AND check_file) AND my*",
    "triggers": "tags:(example AND check_file AND monitoring) AND attr.priority:[03 TO *]"
  },
  {
    "head": {
      "doc_type": "system",
      "name": "my sys",
      "description": "My system definition",
      "tags": ["example", "check_file", "os", "file"],
      "attr": {
        "firmware": "005_072_138_beta",
        "mission_zone": "warehouse_A7"
      }
    },
    "system_def": {
      "file": "~/ou/my-repo/my-data/my.txt"
    }
  },
  {
    "head": {
      "doc_type": "event_processor",
      "name": "my processor",
      "description": "Config file validator",
      "tags": ["example", "check_file", "old"],
      "attr": {
        "range_detection": "none",
        "location_priority": "high"
      }
    },
    "command": {
      "cmd": "~/ou/my-repo/my-scripts/fileValidator.py",
      "error_log_file": "/tmp/err.txt",
      "instances_count": 1
    }
  },
  {
    "head": {
      "doc_type": "job",
      "name": "my Job",
      "description": "My job definition",
      "tags": ["example", "check_file"]
    },
    "events": "tags:(example AND check_file) AND attr.check:size",
    "processors": "tags:(example AND check_file) AND attr.location_priority:high",
    "systems": "tags:(example AND check_file) AND attr.mission_zone:warehouse_A7"
  },
  {
    "head": {
      "doc_type": "event",
      "name": "checkFileSize",
      "description": "Triggers validation of file size.",
      "tags": ["example", "check_file", "validate"],
      "attr": {
        "config": "true",
        "check": "size"
      }
    },
    "processable": true
  },
  {
    "head": {
      "doc_type": "triggers/event_scheduler",
      "name": "validator",
      "description": "Schedules recurring file size validation events.",
      "tags": ["example", "check_file", "health", "system", "monitoring"],
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
]
```

### What it does:

* Declares a **root document** that enables the validation scenario.
* Defines a **system** pointing to the target file.
* Sets up a **Python-based event processor** to check file size.
* Configures a **scheduler** that emits `checkFileSize` events every 15 seconds.



## Step 2: Create the Python Validator

Save the following file as `fileValidator.py` in your repo's `my-scripts` folder:

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
        char = sys.stdin.read(1)
        if not char:
            break

        buffer += char

        if char == '{':
            brace_count += 1
        elif char == '}':
            brace_count -= 1

        if brace_count == 0 and buffer.strip():
            return buffer.strip()
    return None

def check_file_size(json_data):
    try:
        root = json.loads(json_data)
        max_file_size = root.get("maxFileSize")
        system = root.get("system", {})
        file_path = str(Path(system.get("file", "")).expanduser())

        if not isinstance(max_file_size, int) or not os.path.exists(file_path):
            print(json.dumps({}))
            sys.stdout.flush()
            return

        file_size = os.path.getsize(file_path)
        if file_size > max_file_size:
            print(json.dumps({
                "eventType": "InvalidFileSize",
                "file": file_path,
                "fileSize": file_size,
                "maxFileSize": max_file_size,
                "message": "Invalid file size detected!"
            }))
        else:
            print(json.dumps({}))
        sys.stdout.flush()
    except json.JSONDecodeError:
        print(json.dumps({}))
        sys.stdout.flush()

if __name__ == "__main__":
    while True:
        json_data = read_json_from_stdin()
        if json_data:
            check_file_size(json_data)
        else:
            print(json.dumps({}))
            sys.stdout.flush()
            break
```

Make the script executable:

```bash
chmod +x my-repo/my-scripts/fileValidator.py
```



## Step 3: Start OpenUniverse

Run OpenUniverse as usual:

```bash
./ou my-repo start --stdout | jq
```

This will trigger the validator every 15 seconds.



## Step 4: Observe Output

Once running:

* Watch for `"InvalidFileSize"` events in the output.
* The event will contain full file path, current size, and max limit.



## Step 5: Extend It

* Change `maxFileSize` or file path in the system definition.
* Add more system attributes (e.g., checksum, type).
* Support multiple file validations with tag filters.
* Log violations to a central system.



## Summary

| Component         | Purpose                                                |
| ----------------- | ------------------------------------------------------ |
| `root`            | Central controller for triggers, jobs, and events      |
| `system`          | Declares the file to monitor                           |
| `event_processor` | Python validator for checking file size                |
| `job`             | Binds event to system and processor via tags           |
| `event`           | Declares `checkFileSize` for recurring validation      |
| `scheduler`       | Runs the job every 15 seconds with `maxFileSize` limit |
