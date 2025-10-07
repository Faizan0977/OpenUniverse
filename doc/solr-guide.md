# Apache Solr Guide

This guide explains how to configure and run an Apache Solr server using the
**slim release**. You'll start Solr in cloud mode, create a collection, and
access the admin interface.

## Download Apache Solr

Visit the official download page:

[https://solr.apache.org/downloads.html](https://solr.apache.org/downloads.html)

Download the slim binary release:

- `solr-9.8.1-slim.tgz` (You may verify using PGP or SHA512 files)

To download and extract:

```bash
wget https://downloads.apache.org/solr/solr/9.8.1/solr-9.8.1-slim.tgz
tar -xzf solr-9.8.1-slim.tgz
```

## Start Solr in Cloud Mode

Run the following commands:

```bash
cd solr-9.8.1/bin
./solr start --cloud
```

This will start Solr on port `8983` by default.

## Delete a Collection (if exists)

Use the Collections API to delete a collection named `my_collection`:

```bash
curl "http://localhost:8983/solr/admin/collections?action=DELETE&name=my_collection"
```

## Create a Collection

Use the Collections API to create a collection named `my_collection`:

```bash
curl --request POST \
  --url http://localhost:8983/api/collections \
  --header 'Content-Type: application/json' \
  --data '{
    "name": "my_collection",
    "numShards": 1,
    "replicationFactor": 1
  }'
```

## Access the Admin Panel

Open your browser and go to:

[http://localhost:8983](http://localhost:8983)


This is the Solr Admin UI, where you can view collections and query documents.

## References

- [Five Minutes to Searching](https://solr.apache.org/guide/solr/latest/getting-started/tutorial-five-minutes.html)
- [Solr Documentation](https://solr.apache.org/guide/)
