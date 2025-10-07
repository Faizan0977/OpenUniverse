# Plugin Specifications

This document describes the standard plugins and their behavior.
- Plugins are regular operating system CLI programs that communicate with the OpenUniverse kernel.  
- Each plugin may use **stdin**, **stdout**, or both for this communication depending on its purpose, and must follow the defined conventions.
- Plugins must implement an endless loop to continuously read and write data.
- Plugin processes are started by the kernel and terminate when it stops.
- Executable requirement. All plugins must be executable.  
Ensure execute permission is set before use:

```bash
chmod +x YOUR-PLUGIN-EXECUTABLE
```

---
## Event Publisher Plugin

### Role
Generates and publishes event messages.  

### Required in:
- Triggers/EventPublisherDocument

### Execution Flow
1. Writes an event message to **stdout**.  

Notes:  
*The message must be properly formatted JSON, emitted as a single line.*

<br><br>
***Example (Python):***

This plugin monitors a specified log file and emits JSON events when issues are detected. It accepts three arguments:

1. Path to the log file
2. Maximum allowed size in bytes
3. Check interval in seconds

At each interval, the plugin inspects the file and prints a JSON event to **stdout** only if a problem is found:

- `event_type = log_file_size_exceeded` — when the log file grows beyond the limit  
- `event_type = log_file_not_found` — when the file is missing  
- `event_type = log_file_error` — when any other file access error occurs  

When the log file is within the defined limits, the plugin stays silent.

```python
#!/usr/bin/env python3
import json, time, sys, os

# Help for testing purposes (optional)
if len(sys.argv) != 4:
    print(f"Usage: {sys.argv[0]} <log_file_path> <max_log_file_size_bytes> <check_interval_seconds>", file=sys.stderr)
    sys.exit(1)

LOG_FILE_PATH = sys.argv[1]
MAX_LOG_FILE_SIZE = int(sys.argv[2])
CHECK_INTERVAL = int(sys.argv[3])

while True:
    try:
        log_file_size = os.path.getsize(LOG_FILE_PATH)
        if log_file_size > MAX_LOG_FILE_SIZE:
            event = {
                "event_type": "log_file_size_exceeded",
                "log_file_path": LOG_FILE_PATH,
                "log_file_size": log_file_size,
                "max_log_file_size": MAX_LOG_FILE_SIZE
            }
            print(json.dumps(event), flush=True)
    except FileNotFoundError:
        event = {
            "event_type": "log_file_not_found",
            "log_file_path": LOG_FILE_PATH
        }
        print(json.dumps(event), flush=True)
    except Exception as e:
        event = {
            "event_type": "log_file_error",
            "log_file_path": LOG_FILE_PATH,
            "error": str(e)
        }
        print(json.dumps(event), flush=True)

    time.sleep(CHECK_INTERVAL)
```

---

## Event Processor Plugin

### Role
Consumes events, applies processing logic, and emits the result.  

### Required in:
- EventProcessorDocument

### Execution Flow
1. Reads an event JSON message from **stdin**.  
2. Processes the message against the specified system.  
3. Writes the resulting JSON message to **stdout**.  

Notes:  
- *Each input message includes `event_type` and `system` keys.*
- *Input and output messages must both be properly formatted JSON, emitted as single lines.*
- *The output message may be either an event (including the event_type key) or just a processing report.*

<br><br>
***Example (Python):***

This plugin checks a specified log file and emits JSON result.
When issues are detected it emits event.
It got two parameters from `system` object:

1. `log_file_path` — Path to the log file
2. `max_log_file_size` — Maximum allowed size in bytes

The plugin inspects the file and prints a JSON event to **stdout** only if a problem is found:

- `event_type = log_file_size_exceeded` — when the log file grows beyond the limit  
- `event_type = log_file_not_found` — when the file is missing  
- `event_type = log_file_error` — when any other file access error occurs  

When the log file is within the defined limits, the plugin prints report message.

```python
#!/usr/bin/env python3
import sys, json, os

for line in sys.stdin:
    event_in = json.loads(line)
    system = event_in.get("system", {})
    log_file_path = system.get("log_file_path")
    max_log_file_size = system.get("max_log_file_size")

    result = {"log_file_path": log_file_path}

    try:
        log_file_size = os.path.getsize(log_file_path)
        result["log_file_size"] = log_file_size
        result["max_log_file_size"] = max_log_file_size

        if log_file_size > max_log_file_size:
            result["event_type"] = "log_file_size_exceeded"
            result["status"] = "size_exceeded"
        else:
            result["status"] = "ok" # no event_type, it's not event, it's just report message

    except FileNotFoundError:
        result["event_type"] = "log_file_not_found"
        result["status"] = "not_found"
    except Exception as e:
        result["event_type"] = "log_file_error"
        result["status"] = "error"
        result["error"] = str(e)

    print(json.dumps(result), flush=True)
```

---

## Pre-Run Validator Plugin

### Role
Validates OpenUniverse documents before execution starts.  

### Optional in:
- RootDocument
- EventDocument
- JobDocument
- Triggers/EventPublisherDocument
- Triggers/CalendarDocument
- Triggers/SchedulerDocument
- EventProcessorDocument
- SystemDocument

### Execution Flow
1. Reads a single JSON document from **stdin** and validates it.  
2. Outputs a validation result as a single character to **stdout**:  

    - `0` — valid  
    - `1` — not valid  

Notes:  
*The message is JSON, emitted as a single line.*

<br><br>
***Example (Python):***

```python
#!/usr/bin/env python3
import sys, json

MIN_MEMORY_SIZE = 200 * 1024 * 1024  # 200 MB, adjust as needed

def enough_memory():
    with open("/proc/meminfo") as f:
        for line in f:
            if line.startswith("MemAvailable:"):
                available_kb = int(line.split()[1])
                return available_kb * 1024 >= MIN_MEMORY_SIZE
    return False

try:
    # just skip info
    for line in sys.stdin:
        doc = json.loads(line)

    if enough_memory():
        print("0", end="", flush=True)  # valid
    else
        print("1", end="", flush=True)  # invalid

except Exception as e:
    print("1", end="", flush=True) # invalid
    print(str(e), flush=True) # error

```

---

## Secret Extractor Plugin

### Role
Decrypts or extracts protected values for runtime usage.  

### Optional in:
- RootDocument

### Execution Flow
1. Reads a protected value (one line) from **stdin**.  
2. Reads a secret (one line) from **stdin**.  
3. Produces the original value.  
4. Outputs the original value as a line to **stdout**.  

<br><br>
***Example (Python):***

```python
#!/usr/bin/env python3
import sys

while True:
    protected = sys.stdin.readline().strip()
    secret = sys.stdin.readline().strip()

    if protected == "ABCDEF12345" and secret == "mysecret":
        original = "John Doe"
    else:
        original = ""  # return empty string for any other input

    print(original, flush=True)
```

---

## Export Target Plugin

### Role
Delivers JSON messages to external backend(s).  

### Optional in:
- RootDocument

### Execution Flow
1. Reads a JSON message from **stdin**.  
2. Attempts to deliver the message.  
3. Outputs a delivery status report line to **stdout**:  

    - `0` — delivery succeeded  
    - `<error message>` — delivery failed  

Notes:  
*The message is JSON, emitted as a single line.*

<br><br>
***Example (Python):***

```python
#!/usr/bin/env python3
import sys, json

for line in sys.stdin:
    try:
        msg = json.loads(line)
        # some message delivery logic here
        print("0", flush=True) # success
    except Exception as e:
        print(str(e).replace("\n", " "), flush=True) # error
```

---

## Dead Message Queue (<abbr title="Dead Message Queue">DMQ</abbr>) Plugin

### Role
Safely stores undeliverable messages in the **Dead Message Queue (<abbr title="Dead Message Queue">DMQ</abbr>)** for later inspection or reprocessing.  

### Optional in:
- RootDocument

### Execution Flow
1. Reads JSON messages from **stdin**.  
2. Attempts to deliver the message to the <abbr title="Dead Message Queue">DMQ</abbr>.  
3. Outputs a delivery status report line to **stdout**:  

    - `0` — delivery to <abbr title="Dead Message Queue">DMQ</abbr> succeeded  
    - `<error message>` — delivery to <abbr title="Dead Message Queue">DMQ</abbr> failed  

Notes:  
*The message is JSON, emitted as a single line.*

<br><br>
***Example (Python):***

```python
#!/usr/bin/env python3
import sys, json

for line in sys.stdin:
    try:
        msg = json.loads(line)
        # some DMQ message delivery logic here
        print("0", flush=True) # success
    except Exception as e:
        print(str(e), file=sys.stderr, flush=True) # error

```

---