## How it works

OpenUniverse operates in several stages to transform static documents definitions into a dynamic, event-driven infrastructure.

First, the repository working directory is scanned for documents. Each document is represented as a JSON object. A single JSON file may contain a single document or an array of documents. During this scan, OpenUniverse automatically skips any documents with unsupported specification versions or those explicitly marked as disabled.

Next, each discovered document is passed through an optional chain of pre-run constraints, if declared. Constraints ensure that documents meet structural, logical, and environmental requirements before execution. Processing does not starts if any constraint reports invalid requirements.

Once validation is complete, OpenUniverse enters the discovery phase. Here, the system analyzes declared search queries, resolves cross-references between documents, and maps their relationships.

After discovery, OpenUniverse loads the resolved document instances and begins execution. At this point, the runtime becomes active:
- **Triggers** fire events based on conditions, calendars, or schedules.  
- **Processors** consume event messages and execute the appropriate activities across systems defined in **jobs**.  
- **Export targets** deliver data to external backends for storage or further processing.  
- **DMQ** intercepts undeliverable or failed messages, routing them into a dedicated dead-message queue for later inspection, retries, or manual handling.  

Through these stages, OpenUniverse transforms static JSON definitions into a dynamic, event-driven infrastructure.
