<h1>
OpenUniverse
</h1>

<a title="Category UaC (Universe as Code)" href="https://github.com/ou-org/OpenUniverse"><img src="https://img.shields.io/badge/category-UaC_(Universe_as_Code)-blueviolet?style=flat-square"></a>
<a title="License MIT" href="https://github.com/ou-org/OpenUniverse/blob/main/LICENSE"><img src="https://img.shields.io/badge/license-MIT-blue?style=flat-square"></a>
<a title="Latest release" href="https://github.com/ou-org/OpenUniverse/releases"><img src="https://img.shields.io/github/v/release/ou-org/OpenUniverse?style=flat-square&color=28A745"></a>

<h2>What is Universe?</h2>
<p>
  A <b>Universe</b> is a complete digital environment that connects data, information, and knowledge across
  diverse domains. It extends beyond IT infrastructure to include ALL data sources like physical assets,
  business processes, regulatory constraints, data lifecycles, and knowledge flows. A Universe can be
  understood through different perspectives:
<ul>
  <li>Structural (composition, ownership, hierarchy).</li>
  <li>Functional (usage, control, data flow).</li>
  <li>Historical (lineage, evolution, journals and immutable traces of changes).</li>
  <li>Regulatory (compliance obligations, contractual bindings, access constraints).</li>
  <li>Protocols (standards, interoperability, communication rules).</li>
</ul>
It also encompasses events, transactions, state changes, and lifecycle transitions, thereby enabling reasoning
about system evolution. In this sense, a Universe is a living ecosystem where raw data flows into information,
and information evolves into knowledge, shaping how people, machines, and structures interact securely and
intelligently.
</p>

<h2>Universe as Code</h2>
<p>
  <b>UaC (Universe as Code)</b> is the forward-thinking extension of the well-known IaC (Infrastructure as
  Code)
  paradigm. While IaC focuses on describing and automating IT infrastructure such as servers, networks, and
  software stacks, UaC embraces a much broader and more complex scope.
  In UaC, the principle of declarative,
  code-driven definition extends not only to infrastructure but also to physical assets, business processes,
  regulatory constraints, data lifecycles, and knowledge flows.<br><br>
  Just as IaC transformed system
  administration into programmable workflows, UaC envisions an environment where entire digital-physical
  ecosystems can be described, evolved, and reasoned about through code. This approach anticipates future
  needs
  where interoperability, compliance, intelligence, and evolution of systems are orchestrated holistically,
  making UaC a natural evolution beyond IaC.
</p>

<h2>Why <span class="product-name">Open<wbr>Universe</span>?</h2>
<p>
  <span class="product-name">Open<wbr>Universe</span> is an ultralight, security-first open source UaC (Universe as Code) platform that unifies your environment.
</p>
<p>
  Traditional Infrastructure as Code tools such as Terraform are effective for provisioning and
  configuration but are not designed for real-time, event-driven orchestration across diverse systems.
  OpenUniverse fills this gap by enabling triggers, workflows, and responses to be
  defined as code and executed dynamically across cloud services, on-premise systems, IoT devices, and legacy applications.
</p>
<p>
  The platform ensures that orchestration logic is versioned, signed, and timestamped for compliance and traceability,
  while maintaining security through cryptographic chaining of records. Because it is system-neutral, it integrates
  across heterogeneous technologies without vendor lock-in.
</p>
<p>
  By combining real-time coordination with auditability and long-term verifiability,
  OpenUniverse extends the benefits of IaC into the operational domain,
  offering a structured and predictable approach to automation that aligns with regulatory and governance needs.
</p>

<h2>Decentralized Orchestration</h2>
<p>OpenUniverse operates in a world without a single point of control. Unlike traditional systems that rely on centralized servers or orchestration engines, erse treats every component — events, jobs, triggers, processors, and systems — as part of a decentralized, self-organizing network.</p>
<p>In OpenUniverse, workflows are not dictated by a central engine. Instead, each component communicates through cross-referenced event streams, allowing  triggers to react dynamically to changes anywhere in the system. This model ensures:</p>
<ul>
  <li><strong>Resilience:</strong> There is no single point of failure. Nodes can join or leave the system without disrupting processing.</li>
  <li><strong>Scalability:</strong> Workloads distribute naturally across available resources, allowing the system to expand organically.</li>
  <li><strong>Autonomy:</strong> Each component decides its actions based on local state and incoming events, while still contributing to global ation.</li>
</ul>

<h2>How it works</h2>
<p>
  OpenUniverse operates in several stages to transform static document definitions into a dynamic,
  event-driven infrastructure.
  <br><br>
  Before any processing begins, OpenUniverse optionally performs a self-check to verify its own integrity:
  <ul>
    <li>The distribution JAR is validated against its expected SHA-256 checksum.</li>
    <li>The JAR’s digital signature is verified to ensure it originates from a trusted source and has not been tampered with.</li>
    <li>A detailed self-check report is generated and stored in the repository, providing a permanent audit trail of verification results.</li>
  </ul>
  <br>
  Next, the repository working directory is scanned for documents. Each document is represented as a JSON
  object. A single JSON file may contain a single document or an array of documents. During this scan,
  OpenUniverse automatically skips any documents with unsupported specification versions or those explicitly
  marked as disabled.
  <br><br>
  In addition to JSON, documents and plugins may also be authored directly in Markdown files.
  This allows developers to provide human-readable documentation alongside executable definitions, blending
  source, commentary, and infrastructure logic in a single artifact.
  <br><br>
  If the repository is marked to enforce signed commits (via repository configuration), OpenUniverse performs
  commit signature verification before any constraint checks:
  <ul>
    <li>The system extracts the HEAD commit signature and verifies its cryptographic validity.</li>
    <li>It evaluates the signing key:
      <ul>
        <li>Only keys marked as ultimately trusted (u) or fully trusted (f) in the GnuPG keyring are accepted.</li>
        <li>Keys that are revoked, expired, disabled, marginal, or unknown are rejected.</li>
      </ul>
    </li>
    <li>If the signature verification fails or the key does not meet the strict trust criteria, processing halts to prevent execution of untrusted or tampered documents.</li>
  </ul>
  If the repository is not marked for signed commits, this step is skipped and documents are processed normally.
  <br><br>
  Next, each discovered document is passed through an optional chain of pre-run constraints, if declared.
  Constraints ensure that documents meet structural, logical, and environmental requirements before execution.
  Processing does not start if any constraint reports invalid requirements.
  <br><br>
  Once validation is complete, OpenUniverse enters the discovery phase. Here, the system analyzes declared
  search queries, resolves cross-references between documents, and maps their relationships.
  <br><br>
  After discovery, OpenUniverse loads the resolved document instances and begins execution. At this point, the
  runtime becomes active:
<ul>
  <li><b>Triggers</b> fire events based on conditions, calendars, or schedules</li>
  <li><b>Processors</b> consume event messages and execute the appropriate activities across <b>systems</b> defined
      in <b>jobs</b></li>
  <li><b>Export targets</b> deliver data to external backends for storage or further processing</li>
  <li><b>DMQ</b> intercepts undeliverable or failed messages, routing them into a dedicated dead-message queue
      for later inspection, retries, or manual handling</li>
</ul>
  <br>
<p>Every record produced by OpenUniverse is secured and traceable through multiple layers of protection:</p>
<ul>
  <li><b>Identity & Ordering</b> – each record carries a globally unique identifier (GUID) and a serial number within its node’s stream, ensuring uniqueness and ordered traceability.</li>
  <li><b>Integrity & Authenticity</b> – contents are hashed with SHA-256 and digitally signed to prevent tampering and prove origin.</li>
  <li><b>Time Assurance</b> – records receive real-time NTP timestamps, are additionally sealed by a trusted Certificate Authority (CA) for long-term non-repudiation, and timestamped by a Time Stamping Authority (TSA, RFC 3161) for independent verification.</li>
  <li><b>Immutability</b> – every record references the hash of its predecessor, forming a blockchain-style ledger that makes alterations immediately evident.</li>
</ul></p>

---

## The "Hello, Universe!" Example

### 1. Start the Universe:

Linux 64-bit Intel/AMD (x64):

```bash
curl -fsSL https://raw.githubusercontent.com/ou-org/OpenUniverse/v1.0.22/src/main/scripts/hello.sh | sh -s -- 1.0.22 x64
```

<br>

Linux 64-bit ARM (aarch64):

```bash
curl -fsSL https://raw.githubusercontent.com/ou-org/OpenUniverse/v1.0.22/src/main/scripts/hello.sh | sh -s -- 1.0.22 aarch64
```

### 2. Hear the Universe!

Open your browser and go to:

http://localhost:8080/events

> [!NOTE]
> Read more about **Hello, Universe!** example
> [here](https://github.com/ou-org/OpenUniverse/blob/master/doc/examples/HelloUniverse.md).

## Why OpenUniverse Releases Are Source-Only

OpenUniverse is distributed as **source code plus build tooling**, never as
precompiled binaries, to ensure maximum security, compliance, and trust. By
building locally, you control exactly what runs in your environment: you can
audit the code, compile it yourself, and keep sensitive parameters, such as your
JAR signing keystore, private and never exposed in public binaries. This
approach meets regulatory and compliance requirements, allowing organizations to
verify builds, enforce reproducibility, and maintain deterministic outputs. The
provided `build.sh` script and `build.properties` file offer a flexible,
reproducible, and customizable build pipeline for different Linux distributions,
runtime integrations, and organization-specific security policies, without
needing multiple prebuilt binaries. Source-only releases remain lightweight,
depend only on standard tools like JDK and Maven, and empower developers,
operators, and infrastructure teams to inspect, contribute to, and fully
understand the platform, rather than relying on opaque black-box software.

## Build Official Production-ready Binaries

#### 1. Prepare Your Properties File:

`build.properties` (Example)

```properties
##########################################################
# ⚠️ WARNING! CHANGE PARAMETERS BEFORE PRODUCTION USAGE! #
##########################################################

# OpenUnvierse configuration properties file (Example)

# Copy this file to build.properties and adjust the values as needed.
# This file is used by build.sh

# -----------------------------
# JAR SIGNING CONFIG
# -----------------------------

# Keystore related parameters
#
# For simplicity, we use the same values here as in
# create-keystore.sh (the quick-and-dirty keystore
# generation script).
# Adjust these as needed.

# You can also use a different keystore
# type (e.g., JKS) if you prefer.

# Tip: Create the QaD (quick-and-dirty) keystore with:
# https://raw.githubusercontent.com/ou-org/OpenUniverse/refs/heads/master/src/main/scripts/create-qad-keystore.sh

# ⚠️ Make sure to keep your keystore and passwords secure!
# ⚠️ Never commit your keystore or passwords to version control!

# In this example, we use an absolute path in the user's home directory.
# Make sure the path is correct for your environment.

# Also, ensure that the keystore file is accessible during the build process.
# You can create the keystore in a secure location and reference it here.

# Example values (replace with your actual values):

SIGN_JAR_KEYSTORE="$HOME/MyQuickAndDirtyKeystore/keystore.p12"
SIGN_JAR_STORETYPE="PKCS12"
SIGN_JAR_STOREPASS="your_password"
SIGN_JAR_ALIAS="signing_alias"
SIGN_JAR_KEYPASS="your_password"

# Timestamping Authority URL
# https://www.ietf.org/rfc/rfc3161.txt - RFC 3161 Time-Stamp Protocol (TSP)
SIGN_JAR_TSA="http://timestamp.digicert.com"
```

#### 2. Build Binaries:

```bash
curl -fsSL https://raw.githubusercontent.com/ou-org/OpenUniverse/v${OPEN_UNIVERSE_VERSION}/src/main/scripts/build.sh | sh -s -- ${OPEN_UNIVERSE_VERSION} ${YOUR_BUILD_PROPERTIES_FILE} ${OUTPUT_DIR}
```

## Documentation

- [Core components](doc/core-components.md)
- [Ontology Overview](doc/ontology.md)
- [Query Syntax](doc/query-syntax.md)
- [Abstract Document Structure](doc/abstract-documents-structure.md)
- [Syntax for Placeholder Injection](doc/placeholder-injection.md)
- [Java Requirement](doc/java-requirement.md)
- [Java Search Order](doc/java-search-order.md)
- [Build Guide](doc/build-guide.md)
- [Command-line Interface](doc/command-line-interface.md)
- [Running Guide](doc/running-guide.md)

## Tutorials

- [Signing Output Records](doc/signing-guide.md)
- [Send data to Apache Solr](doc/solr-exporter.md)

## Examples

- [Hello, Universe!](doc/examples/HelloUniverse.md)
- [Check File Size](doc/examples/CheckFileSize.md)

<!--
- [Document Validator](doc/examples/document-validator.json)
- [Hello Solr](doc/examples/hello-solr.json)
- [Hello SSE (JSON)](doc/examples/hello-sse.json)
- [Hello SSE (YAML)](doc/examples/hello-sse.yaml)
- [Hello Universe](doc/examples/HelloUniverse.json)
- [Inject Properties](doc/examples/inject-properties.json)
- [Signing](doc/examples/Signing.json)
- [Trigger](doc/examples/Trigger.json)

## Plugin Examples

- [Decrypt](scripts/decrypt.py)
- [DMQ](scripts/dmq.py)
- [File Size Monitor](scripts/file_size_monitor.py)
- [File Validator (Python)](scripts/fileValidator.py)
- [File Validator (Shell)](scripts/fileValidator.sh)
- [OS](scripts/os.py)
- [Send to Solr](scripts/send-to-solr.py)
- [SSE](scripts/sse.py)
- [Validator](scripts/validator.py)
-->

## Useful Guides

- [Minimal Git Repository Guide](doc/git-repo.md)
- [Apache Solr Guide](doc/solr-guide.md)
- [Working with Encrypted Values](doc/work-with-encrypted-values.md)

## Related Works

1. [A Scalable, event-driven architecture for designing interactions across heterogeneous devices in smart environments](https://doi.org/10.1016/j.infsof.2019.01.006)<br>
   Ovidiu-Andrei Schipor, Radu-Daniel Vatavu, Jean Vanderdonckt. _Information
   and Software Technology_, Vol. 109 (2019), pp. 43–59 ISSN: 0950-5849<br>

2. [The Power of Event-Driven Architecture: Enabling RealTime Systems and Scalable Solutions](https://doi.org/10.61841/turcomat.v11i1.14928)<br>
   Adisheshu Reddy Kommera. _Turkish Journal of Computer and Mathematics
   Education_, Vol. 11 (2020), pp. 1740–1751 ISSN: 3048-4855<br>

3. [Exploring event-driven architecture in microservices — patterns, pitfalls and best practices](https://doi.org/10.30574/ijsra.2021.4.1.0166)<br>
   Ashwin Chavan. _International Journal of Science and Research Archive_, Vol.
   4 (2021), pp. 229–249 ISSN: 2582-8185<br>

## LICENSE

The OpenUniverse is released under the
<a href="https://github.com/ou-org/OpenUniverse/blob/main/LICENSE">MIT</a>
license.
