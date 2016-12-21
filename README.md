# Sbire

Automate Cloud Foundry PaaS Ops tasks on a service broker.

# Sample usage

## List all service bindings for service broker

In order to have a comprehensive status of a service broker catalog update impacts over existing related service instance bindings, PaaS Ops may need to list service broker service bindings.

GET /v1/service_brokers/{name}/service_bindings

### Request
#### Route
```
GET /v1/service_brokers/p-redis/service_bindings
```
#### Parameters

| Name | Description | Example Values | Valid |
| - | - | - | - |
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
    "serviceInstance": "my-redis-111",
    "application": "app111",
    "servicePlan": "shared-vm",
    "service": "p-redis",
    "space": "space11",
    "organization": "org1"

},
{

    "id": "69a822a7-55cf-4d94-9994-b93d3d9e08ef",
    "serviceInstance": "my-redis-211",
    "application": "app211",
    "servicePlan": "dedicated-vm",
    "service": "p-redis",
    "space": "space11",
    "organization": "org1"

},
{

    "id": "eef72ea9-b01a-4b6d-b328-30a6e92eddd3",
    "serviceInstance": "my-redis-112",
    "application": "app112",
    "servicePlan": "shared-vm",
    "service": "p-redis",
    "space": "space12",
    "organization": "org2"

},
{

    "id": "eef72ea9-b01a-4b6d-b328-30a6e92eddd3",
    "serviceInstance": "my-redis-122",
    "application": "app122",
    "servicePlan": "shared-vm",
    "service": "p-redis",
    "space": "space22",
    "organization": "org2"

}
]
```