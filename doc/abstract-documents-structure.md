# Generic document structure

All OupenUniverse documents extends `AbstractDocument`.

## **AbstractDocument**

|JSON Key       |Data&nbsp;Type    |Required|Description
|---------------|------------------|--------|----------------------------------------------------------------------------------------------------------
|`"head"`       |`Object`          |Yes     |Document metadata and constraints.
|`"properties"` |`Object`          |No      |Custom key-value map.

### **AbstractDocument / HeadObject**

|JSON Key               |Data&nbsp;Type          |Required|Description
|-----------------------|------------------------|--------|----------------------------------------------------------------------------------------------------------
|`"name"`               |`String`                |Yes     |The name of the document.
|`"doc_spec"`           |`String`                |Yes     |The specification version of the document. E.g.: `"OpenUniverseSpecVer-1.01"`.
|`"doc_type"`           |`String`                |Yes     |Defines the nature of the JSON configuration document. <br> Possible values: <br> **- "root"**: Root-level JSON configuration document. <br> **- "event"**: Event-related JSON configuration document. <br> **- "timesheet"**: Timesheet-related JSON configuration document. <br> **- "system"**: System-related JSON configuration document. <br> **- "event_processor"**: Processor-specific JSON configuration document. <br> **- "job"**: Job-related JSON configuration document. <br> **- "triggers/event_calendar"**: Calendar-related JSON configuration document. <br> **- "triggers/event_scheduler"**: Scheduler-related JSON configuration document. <br> **- "triggers/event_provider"**: Trigger-related JSON configuration document. |
|`"disabled"`           |`Boolean`               |No      |Indicates whether the document is disabled (`true`) or active (`false`). Default: `false`.
|`"description"`        |`String`                |No      |A brief description of the document. Default: No description.
|`"validator_сommands"` |`Array of CommandObject`|No      |Constraints such as time intervals and user access restrictions. Default: No constraints.
|`"tags"`               |`Array of Strings`      |No      |A collection of tags to categorize or label the document. Default: No tags.
|`"attr"`               |`Object`                |No      |A map of attributes where keys are attribute names, and values can be of any type. Default: No attributes.


**Example of `HeadObject`**

```json
{
  "doc_spec": "OpenUniverseSpecVer-1.01",
  "doc_type": "event",
  "disabled": false,
  "name": "Sample Event",
  "description": "This is an event configuration document.",
  "tags": ["event", "configuration"],
  "attr": {
    "customField": "value"
  },
  "validator_сommands": [
            {
               "cmd": "my-scripts/validator.py",
               "error_log_file": "/tmp/err.txt",
               "instances_count": 1
            }
         ]
} 
```
