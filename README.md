# Sbire [![Build Status](https://travis-ci.org/orange-cloudfoundry/sbire.svg?branch=master)](https://travis-ci.org/orange-cloudfoundry/sbire)

Automate Cloud Foundry PaaS Ops tasks on a service broker.

# Sample usage

## List service bindings for service broker

In order to have a comprehensive status of a service broker catalog update impacts over existing related service instance bindings, PaaS Ops may need to list service broker service bindings.

GET /v1/service_brokers/{name}/service_bindings

### Request
#### Route
```
GET /v1/service_brokers/p-redis/service_bindings
```
#### Parameters

| Name | Description | Example Values | Valid |
| :--- | :---------- | :------------- | :---- |
| name | service broker name | p-redis | |
| org_name | organization name | org_name=org1 | |
| space_name | space name | org_name=org1&space_name=space11 | if org_name and space_name specified |
| service_label | service label | service_label=p-redis | |
| plan_name | service plan name | plan_name=shared-vm | |
| instance_name | service instance name | instance_name=my-redis-111 | |

#### cURL

```shell
$ curl "https://[your-sbire-host]/v1/service_brokers/p-redis/service_bindings?org_name=org2&space_name=space12" -X GET -u user:changeit
```

### Response
#### Status
```
200 OK
```
#### Body
```shell
{
    "total_results": 1
    "total_pages": 1,
    "next_url": null,
    "prev_url": null,
    "resources": [
      {
        "entity": {
            "id": "6b9b2c7f-3aa9-4699-8535-80268b861c03",
            "serviceInstanceId": "ce6bde82-c77a-4e1d-b04b-dd39d17e5cf8",
            "serviceInstanceName": "my-redis-112",
            "applicationId": "2f4bf8e3-968f-4350-8c01-39e0ea31a4e5",
            "applicationName": "app112",
            "servicePlan": "shared-vm",
            "service": "p-redis",
            "space": "space12",
            "organization": "org2"
        },
        "metadata": {
            "guid": "6b9b2c7f-3aa9-4699-8535-80268b861c03",
            "created_at": "2017-01-13T20:22:35Z",
            "updated_at": null,
            "url": "/v2/service_bindings/6b9b2c7f-3aa9-4699-8535-80268b861c03"
        }
      }
    ]
}
```

## Rebind service bindings for service broker

In order to broadcast a service broker update to existing service instance bindings, PaaS Ops may need to rebind (i.e. unbind then bind) existing service broker service bindings.

POST /v1/service_brokers/{name}/service_bindings

### Request
#### Route
```
POST /v1/service_brokers/p-redis/service_bindings
```
#### Parameters

| Name | Description | Example Values | Valid |
| :--- | :---------- | :------------- | :---- |
| name | service broker name | p-redis | |
| org_name | organization name | org_name=org1 | |
| space_name | space name | org_name=org1&space_name=space11 | if org_name and space_name specified |
| service_label | service label | service_label=p-redis | |
| plan_name | service plan name | plan_name=shared-vm | |
| instance_name | service instance name | instance_name=my-redis-111 | |


#### cURL

```shell
$ curl "https://[your-sbire-host]/v1/service_brokers/p-redis/service_bindings?org_name=org2&space_name=space12" -X POST -u user:changeit
```

### Response
#### Status
```
201 CREATED
```
#### Body
```shell
{
    "total_results": 1
    "total_pages": 1,
    "next_url": null,
    "prev_url": null,
    "resources": [
      {
        "entity": {
            "id": "68d4b7ad0-00b6-4bab-b76a-c1cbc7388c95",
            "serviceInstanceId": "ce6bde82-c77a-4e1d-b04b-dd39d17e5cf8",
            "serviceInstanceName": "my-redis-112",
            "applicationId": "2f4bf8e3-968f-4350-8c01-39e0ea31a4e5",
            "applicationName": "app112",
            "servicePlan": "shared-vm",
            "service": "p-redis",
            "space": "space12",
            "organization": "org2"
        },
        "metadata": {
            "guid": "8d4b7ad0-00b6-4bab-b76a-c1cbc7388c95",
            "created_at": "2017-01-13T20:22:35Z",
            "updated_at": null,
            "url": "/v2/service_bindings/8d4b7ad0-00b6-4bab-b76a-c1cbc7388c95"
        }
      }
    ]
}
```
