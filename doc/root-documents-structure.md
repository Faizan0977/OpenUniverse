# Root document structure

All OupenUniverse documents extends `AbstractDocument`.

## **RootDocument**

|JSON Key       |Data&nbsp;Type    |Required|Description
|---------------|------------------|--------|----------------------------------------------------------------------------------------------------------
|`"head"`       |`Object`          |Yes     |Document metadata and constraints.
|`"properties"` |`Object`          |No      |Custom key-value map.

### Export Settings (`"export_targets""` Array entries)

- **`id`** _(String)_: Unique identifier for the export target.
- **`disabled`** _(Boolean)_: Marks disabled export target.
- **`store_as_array`** _(Boolean)_: Whether data should be stored as an array
  rather than key-value pairs.
- **`enable_compression`** _(Boolean)_: Enables compression for stored data.
- **`compression_level`** _(Integer)_: Compression level (1-9, higher is more
  compressed).
- **`keys_black_list`** _(List[String])_: List of keys to exclude from
  processing.
- **`keys_white_list`** _(List[String])_: List of keys to include in processing.
- **`js_transformer_func`** _(String)_: JavaScript function for transforming
  records before processing.

**Example of `ExportTargetObject`**

```json
         {
          "id": "export_target_1",
            "disabled": false,
            "command": {
               "cmd": "my-scripts/send-to-solr.py",
               "args": [
                  "http://localhost:8983/api/collections/my_collection"
               ]
            }
         }
```
---

### SignSettingsObject

- **`sign_key_store_file`** _(String)_: Path to the key store file containing
  the signing key.
- **`sign_key_store_type`** _(String)_: Type of the key store (`PKCS12`).
- **`sign_key_store_password`** _(String)_: Password for accessing the signing
  key store.
- **`sign_key_alias`** _(String)_: Alias of the signing key in the key store.
- **`sign_key_password`** _(String)_: Password for the signing key.
- **`sign_signature_algorithm`** _(String)_: Signature algorithm used for
  signing (e.g., `SHA256withRSA`).

**Example of `SignSettingsObject`**

```json
{
    "sign_key_store_file": "/etc/security/signing/keystore.p12",
    "sign_key_store_type": "PKCS12",
    "sign_key_store_password": "strongpassword123",
    "sign_key_alias": "my-signing-key",
    "sign_key_password": "keypassword456",
    "sign_signature_algorithm": "SHA256withRSA"
}
```