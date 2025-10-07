## Basic Query Syntax

### Match All Documents

To retrieve all documents, use:

```plaintext
*:*
```

### Term Search

Search for an exact term in a specific field:

```plaintext
name:check_config
```

### Multiple Words (AND/OR)

Use logical operators to refine searches:

- Use `AND` when both terms must be present in the results.
- Use `OR` when at least one of the terms should be present.

```plaintext
name:check_config AND description:"Check MySQL config"
```

Similarly, `OR` can be used:

```plaintext
name:check_config OR description:"Check config"
```

### Phrase Search

Search for an exact phrase in `name` field using double quotes:

```plaintext
"Check config"
```

### Wildcards

- `?` matches a single character.
- `*` matches multiple characters.

Example:

```plaintext
name:che?k  # Matches 'check' and 'chexk' but not 'checck'
name:ch*k   # Matches 'check', 'checck', 'chk'
```

### Fuzzy Search

Use `~` to find similar words:

```plaintext
description:Conf~
```

### Proximity Search

Find words near each other:

The number after `~` specifies the maximum word distance allowed between terms.
A lower number means the terms must be closer together.

```plaintext
"Check config"~5
```

### Field Grouping

Use parentheses for complex expressions:

```plaintext
(name:check_config OR name:"check_configuration") AND description:"Production"
```

### Range Queries

Find values within a range (inclusive by default):

```plaintext
price:[10 TO 100]
```

Date Range Example:

```plaintext
created_at:[2024-01-01T00:00:00Z TO NOW]
```

### Exclusion

Use `-` to exclude terms:

```plaintext
name:check -description:deprecated
```
