<h2>Core components</h2>
<p>
  The architecture is based on documents as the primary abstraction. Each component—events, jobs, triggers,
  systems, and processors—is described as a structured document, with the RootDocument serving as the central
  blueprint. This model makes the environment self-describing, auditable, and easy to evolve.
  Execution is fully event-driven. Events from calendars, schedulers, or publishers activate jobs, which
  process
  them through defined processors and then act on target systems. Instead of static configuration,
  relationships
  between jobs, events, and systems are discovered dynamically at runtime, creating adaptive orchestration
  flows.
  <br><br>
<h3>Documents:</h3>
<div class="flowchart">
  
  ---
  
  <div class="boxed">
    <b>AbstractDocument</b> (Abstract)
    <br><br>
    ┌ <b>Header (HeaderObject)</b><br>
    └ <b>Properties (Map)</b>
  </div><br>

  ---
  
  <div class="boxed">
    <b>RootDocument</b><br>
    extends <b>AbstractDocument</b>
    <br><br>
    ┌ <b>Header (HeaderObject)</b><br>
    ├ <b>Properties (Map)</b><br>
    ├ Triggers (List&lt;TriggerDocument&gt;)<br>
    ├ Jobs (List&lt;JobDocument&gt;)<br>
    ├ SignSettings (SignSettingsObject)<br>
    ├ HashAlgorithm (String)<br>
    ├ UnprotectPlugin (PluginObject)<br>
    ├ ExportTargets (List&lt;ExportTargetObject&gt;)<br>
    └ <abbr title="Dead Message Queue">DMQ</abbr>Plugin (PluginObject)
  </div><br>
  
  ---
  
  <div class="boxed">
    <b>EventDocument</b><br>
    extends <b>AbstractDocument</b>
    <br><br>
    ┌ <b>Header (HeaderObject)</b><br>
    ├ <b>Properties (Map)</b><br>
    └ Processable (Boolean)
  </div><br>
  
  ---
  
  <div class="boxed">
    <b>Triggers/CalendarDocument</b><br>
    extends <b>AbstractDocument</b>
    <br><br>
    ┌ <b>Header (HeaderObject)</b><br>
    ├ <b>Properties (Map)</b><br>
    └ Calendars (List&lt;CalendarObject&gt;)
  </div><br>

  ---
  
  <div class="boxed">
    <b>Triggers/SchedulerDocument</b><br>
    extends <b>AbstractDocument</b>
    <br><br>
    ┌ <b>Header (HeaderObject)</b><br>
    ├ <b>Properties (Map)</b><br>
    └ Schedulers (List&lt;SchedulerObject&gt;)
  </div><br>

  ---
  
  <div class="boxed">
    <b>Triggers/EventPublisherDocument</b><br>
    extends <b>AbstractDocument</b>
    <br><br>
    ┌ <b>Header (HeaderObject)</b><br>
    ├ <b>Properties (Map)</b><br>
    └ EventPublisherPlugin (PluginObject)
  </div><br>

  ---
  
  <div class="boxed">
    <b>JobDocument</b><br>
    extends <b>AbstractDocument</b>
    <br><br>
    ┌ <b>Header (HeaderObject)</b><br>
    ├ <b>Properties (Map)</b><br>
    ├ Systems (List&lt;SystemDocument&gt;)<br>
    └ EventProcessors (List&lt;ProcessorDocument&gt;)
  </div><br>

  ---
  
  <div class="boxed">
    <b>SystemDocument</b><br>
    extends <b>AbstractDocument</b>
    <br><br>
    ┌ <b>Header (HeaderObject)</b><br>
    ├ <b>Properties (Map)</b><br>
    └ AbstractSystemDefinition (Object)
  </div><br>

  ---
  
  <div class="boxed">
    <b>EventProcessorDocument</b><br>
    extends <b>AbstractDocument</b>
    <br><br>
    ┌ <b>Header (HeaderObject)</b><br>
    ├ <b>Properties (Map)</b><br>
    └ EventProcessorPlugin (PluginObject)
  </div>

  ---
  
</div>
<br><br>
<h3>Objects:</h3>

  ---
  
<div class="flowchart">
  <div class="boxed">
    <b>HeaderObject</b>
    <br><br>
    ┌ SpecVer (String)<br>
    ├ Type (String)<br>
    ├ Name (String)<br>
    ├ Description (String)<br>
    ├ Disabled (Boolean)<br>
    ├ Tags (List)<br>
    ├ Attributes (Map)<br>
    └ PreRunConstraints (List&lt;PluginObject&gt;)
  </div><br>

  ---
  
  <div class="boxed">
    <b>PluginObject</b>
    <br><br>
    ┌ Command (String)<br>
    ├ Arguments (List)<br>
    ├ EnvironmentVariables (Map)<br>
    ├ WorkingDirectory (String)<br>
    ├ ErrorLogFile (String)<br>
    └ InstancesCount (Integer)
   </div><br>

  ---
  
  <div class="boxed">
    <b>CalendarObject</b>
    <br><br>
    ┌ EventType (String)<br>
    ├ ScheduledFor (Time)<br>
    └ user-defined fields...
   </div><br>

  ---
  
  <div class="boxed">
    <b>SchedulerObject</b>
    <br><br>
    ┌ EventType (String)<br>
    ├ CronExpression (String)<br>
    └ user-defined fields...
   </div><br>

  ---
  
  <div class="boxed">
    <b>SignSettingsObject</b>
    <br><br>
    ┌ KeyStoreType (String)<br>
    ├ KeyStoreFile (String)<br>
    ├ KeyStorePassword (String)<br>
    ├ KeyAlias (String)<br>
    ├ KeyPassword (String)<br>
    └ SignatureAlgorithm (String)
   </div><br>

  ---
  
  <div class="boxed">
    <b>ExportTargetObject</b>
    <br><br>
    ┌ Id (String)<br>
    ├ StoreAsArray (Boolean)<br>
    ├ EnableCompression (Boolean)<br>
    ├ СompresionLevel (Integer)<br>
    └ ExportTargetPlugin (PluginObject)<br>
  </div>
</div>
<br><br>
<h3>Plugins:</h3>

  ---
  
<div class="flowchart">
  <div class="boxed">
    <b>Event Publisher Plugin</b>
    <br><br>
    Generates and publishes event messages
    <br><br>
    Required in:
    <ul>
      <li>Triggers/EventPublisherDocument</li>
    </ul>
   </div><br>

  ---
  
  <div class="boxed">
    <b>Event Processor Plugin</b>
    <br><br>
    Consumes events, applies processing logic, and emits the result
    <br><br>
    Required in:
    <ul>
      <li>EventProcessorDocument</li>
    </ul>
   </div><br>

  ---
  
  <div class="boxed">
    <b>Pre-Run Validator Plugin</b>
    <br><br>
    Validates OpenUniverse documents before execution starts
    <br><br>
    Optional in:
    <ul>
      <li>RootDocument</li>
      <li>EventDocument</li>
      <li>JobDocument</li>
      <li>Triggers/EventPublisherDocument</li>
      <li>Triggers/CalendarDocument</li>
      <li>Triggers/SchedulerDocument</li>
      <li>EventProcessorDocument</li>
      <li>SystemDocument</li>
    </ul>
   </div><br>

  ---
  
  <div class="boxed">
    <b>Secret Extractor Plugin</b>
    <br><br>
    Decrypts or extracts protected values for runtime usage.
    <br><br>
    Optional in:
    <ul>
      <li>RootDocument</li>
    </ul>
   </div><br>

  ---
  
  <div class="boxed">
    <b>Export Target Plugin</b>
    <br><br>
    Delivers JSON messages to external backend(s)
    <br><br>
    Optional in:
    <ul>
      <li>RootDocument</li>
    </ul>
   </div><br>

  ---
  
  <div class="boxed">
    <b>Dead Message Queue (<abbr title="Dead Message Queue">DMQ</abbr>) Plugin</b>
    <br><br>
    Safely stores undeliverable messages in the Dead Message Queue (<abbr title="Dead Message Queue">DMQ</abbr>)
    for later inspection or reprocessing
    <br><br>
    Optional in:
    <ul>
      <li>RootDocument</li>
    </ul>
  </div><br>

  ---
  
</div>
</p>
