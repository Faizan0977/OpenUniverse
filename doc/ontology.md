# Ontology Overview

- **root**
  - **scope** (events)
  - **triggers** (event publishers and/or calendars and/or schedulers)
  - **jobs** (systems and processors)

At the **root** level, the ontology organizes three key domains: **scope,
triggers, and jobs**. Each domain represents a crucial aspect of the system's
operation.

## **1. Scopes (Contexts of Operation)**

- Defines the domain or category of the events being handled.
- Includes:
  - **Events** – Represents event types.

## **2. Triggers (Event Sources)**

- Specifies what initiates an event-driven process.
- Can be:
  - **Event Publishers** – Components that actively generate and send out
    events.
  - **Calendars** – Time-based schedules that dictate when certain actions
    should occur.
  - **Schedulers** – Fier events at predefined intervals or conditions.

## **3. Jobs (Execution Entities)**

- Represents the processing units that perform tasks on systems based on
  triggers.
- Comprises:
  - **Systems** – Endpoint platforms.
  - **Processors** – Event handlers thst connects to systems and perform tasks
    based on event data.
