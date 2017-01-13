# Sbire

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
$ curl "https://[your-sbire-host]/v1/service_brokers/p-redis/service_bindings" -X GET -u user:changeit
```

### Response
#### Status
```
200 OK
```
#### Body
```shell
[

{

    "id": "6b9b2c7f-3aa9-4699-8535-80268b861c03",
    "serviceInstanceId": "bc07a4a3-2ec6-47ef-8973-d79a56884b6d",
    "serviceInstanceName": "my-redis-111",
    "applicationId": "b1524276-9a5c-4d9a-9467-0f6e7aa2981e",
    "applicationName": "app111",
    "servicePlan": "shared-vm",
    "service": "p-redis",
    "space": "space11",
    "organization": "org1"

},
{

    "id": "69a822a7-55cf-4d94-9994-b93d3d9e08ef",
    "serviceInstanceId": "8856f1b6-091c-4d24-b9be-40df853e6be8",
    "serviceInstanceName": "my-redis-211",
    "applicationId": "139ca9b0-ad9b-437f-8db2-3ca2cb1f8566",
    "applicationName": "app211",
    "servicePlan": "dedicated-vm",
    "service": "p-redis",
    "space": "space11",
    "organization": "org1"

},
{

    "id": "eef72ea9-b01a-4b6d-b328-30a6e92eddd3",
    "serviceInstanceId": "ce6bde82-c77a-4e1d-b04b-dd39d17e5cf8",
    "serviceInstanceName": "my-redis-112",
    "applicationId": "2f4bf8e3-968f-4350-8c01-39e0ea31a4e5",
    "applicationName": "app112",
    "servicePlan": "shared-vm",
    "service": "p-redis",
    "space": "space12",
    "organization": "org2"

},
{

    "id": "eef72ea9-b01a-4b6d-b328-30a6e92eddd3",
    "serviceInstanceId": "6f184a79-28bd-435d-9b01-65e2be8e16d7",
    "serviceInstanceName": "my-redis-122",
    "applicationId": "d7609d5c-5ec0-40e8-a15b-24d83ea3ff28"
    "applicationName": "app122",
    "servicePlan": "shared-vm",
    "service": "p-redis",
    "space": "space22",
    "organization": "org2"

}
]
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
$ curl "https://[your-sbire-host]/v1/service_brokers/p-redis/service_bindings" -X POST -u user:changeit
```

### Response
#### Status
```
201 CREATED
```
#### Body
```shell
[

{

    "id": "8d4b7ad0-00b6-4bab-b76a-c1cbc7388c95",
    "serviceInstanceId": "bc07a4a3-2ec6-47ef-8973-d79a56884b6d",
    "serviceInstanceName": "my-redis-111",
    "applicationId": "b1524276-9a5c-4d9a-9467-0f6e7aa2981e",
    "applicationName": "app111",
    "servicePlan": "shared-vm",
    "service": "p-redis",
    "space": "space11",
    "organization": "org1"

},
{

    "id": "12672d87-317b-492a-84e5-dd299653d5f6",
    "serviceInstanceId": "8856f1b6-091c-4d24-b9be-40df853e6be8",
    "serviceInstanceName": "my-redis-211",
    "applicationId": "139ca9b0-ad9b-437f-8db2-3ca2cb1f8566",
    "applicationName": "app211",
    "servicePlan": "dedicated-vm",
    "service": "p-redis",
    "space": "space11",
    "organization": "org1"

},
{

    "id": "86dfb2e1-dd9d-435b-a22a-594a3c488c94",
    "serviceInstanceId": "ce6bde82-c77a-4e1d-b04b-dd39d17e5cf8",
    "serviceInstanceName": "my-redis-112",
    "applicationId": "2f4bf8e3-968f-4350-8c01-39e0ea31a4e5",
    "applicationName": "app112",
    "servicePlan": "shared-vm",
    "service": "p-redis",
    "space": "space12",
    "organization": "org2"

},
{

    "id": "932faf5a-fe25-4158-b6b6-32d16cecb7a1",
    "serviceInstanceId": "6f184a79-28bd-435d-9b01-65e2be8e16d7",
    "serviceInstanceName": "my-redis-122",
    "applicationId": "d7609d5c-5ec0-40e8-a15b-24d83ea3ff28"
    "applicationName": "app122",
    "servicePlan": "shared-vm",
    "service": "p-redis",
    "space": "space22",
    "organization": "org2"

}
]
```
