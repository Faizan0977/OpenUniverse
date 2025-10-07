# Syntax for Placeholder Injection

OpenUniverse provides a powerful and flexible mechanism for injecting dynamic
values into configuration files using placeholder expressions. These expressions
allow you to externalize configuration and integrate with various runtime
contexts such as environment variables, system properties, CLI arguments, secret
vaults, or predefined OpenUniverse properties.

### Placeholder Format

A placeholder follows the format:

```
${<prefix>name}
```

- `prefix` (optional): Indicates the source of the value.
- `name`: The key or identifier to look up in the specified source.

If no prefix is provided, OpenUniverse assumes the value is defined within its
own configuration scope.

### Supported Prefixes

| Prefix    | Source Description                              | Example                          |
|-----------|-------------------------------------------------|----------------------------------|
| `env:`    | Environment variable – resolved from the system or process environment. | `${env:MY_VAR}`              |
| `sys:`    | JVM system property – passed via `-D` or set in code. | `${sys:my.sys.prop}`         |
| `arg:`    | Command-line argument – provided at application startup. | `${arg:repo}`                |
| `sec:`    | Secret key. | `${sec:my.vault.key}`      |
| *(none)*  | OpenUniverse property – defined within the configuration JSON or inherited. | `${my.prop}`                 |


### Resolution Order

When resolving placeholders, OpenUniverse applies the following rules:

1. If a prefix is specified, only that source is queried.
2. If no prefix is provided, OpenUniverse checks its internal property context.
3. Unresolved placeholders are either left as-is or cause validation errors,
   depending on the strictness mode.

### Escaping Placeholders

To use a literal `${...}` string without triggering placeholder resolution,
prefix the dollar sign with a backslash:

```json
"This is a literal string: \\${not.a.placeholder}"
```

### Use Cases

- **Environment configuration:** Useful for injecting host-specific values such
  as credentials, ports, or file paths.

- **Multi-environment deployments:** Combine system properties and CLI arguments
  to toggle behavior between development, staging, and production.

- **Secrets management:** Use `encrypted:` placeholders to keep secrets out of plain
  configuration files.

- **Dynamic routing or job logic:** Adjust behavior at runtime based on
  arguments passed to the orchestrator.

### Example Configuration Snippet

```json
{
    "repository": "${arg:repo}",
    "logLevel": "${sys:openuniverse.log.level}",
    "apiKey": "${sec:services.payment.apiKey}",
    "outputDir": "${env:OUTPUT_PATH}",
    "heartbeatInterval": "${heartbeat.interval}"
}
```
