<!-- Generator: Widdershins v4.0.1 -->

<h1 id="makker-api-server">Makker API Server v1.0.0</h1>

> Scroll down for code samples, example requests and responses. Select a language for code samples from the tabs above or the mobile navigation menu.

Make (formerly Integromat) connector suite. Choose how do you want to manage your low-code and no-code scenarios from your code!

Base URLs:

* <a href="/">/</a>

<h1 id="makker-api-server-scenarios">Scenarios</h1>

A scenario lets you create and run automated tasks in Make (formerly Integromat).

<a href="https://www.make.com/en/api-documentation/scenarios">Find out more</a>

## createScenarioV1

<a id="opIdcreateScenarioV1"></a>

`POST /v1/scenarios`

Create a new scenario from the provided blueprint.

The `blueprint` can be passed as either a correctly escaped JSON string, or you can also provide a Base64-encoded value.

> Body parameter

```json
{
  "teamId": 1,
  "folderId": 1,
  "blueprint": "{ \"name\": \"Empty integration\", \"flow\": [ { \"id\": 2, \"module\": \"json:ParseJSON\", \"version\": 1, \"metadata\": { \"designer\": { \"x\": -46, \"y\": 47, \"messages\": [ { \"category\": \"last\", \"severity\": \"warning\", \"message\": \"A transformer should not be the last module in the route.\" } ] } } } ], \"metadata\": { \"version\": 1, \"scenario\": { \"roundtrips\": 1, \"maxErrors\": 3, \"autoCommit\": true, \"autoCommitTriggerLast\": true, \"sequential\": false, \"confidential\": false, \"dataloss\": false, \"dlq\": false }, \"designer\": { \"orphans\": [ ] } } }"
}
```

<h3 id="createscenariov1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|body|body|[CreateScenarioRequest](#schemacreatescenariorequest)|true|Scenario definition.|

> Example responses

> 201 Response

```json
{
  "id": 925,
  "teamId": 1,
  "folderId": 1,
  "name": "New scenario"
}
```

<h3 id="createscenariov1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|The created scenario.|[Scenario](#schemascenario)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad request.|[ErrorResponse](#schemaerrorresponse)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Bad request.|[ErrorResponse](#schemaerrorresponse)|

<aside class="success">
This operation does not require authentication
</aside>

## updateScenarioV1

<a id="opIdupdateScenarioV1"></a>

`PATCH /v1/scenarios/{scenarioId}`

*Update a scenario.*

Updates a scenario by uploading of a new or updated blueprint

> Body parameter

```json
{
  "blueprint": "{ \"name\": \"Empty integration\", \"flow\": [ { \"id\": 2, \"module\": \"json:ParseJSON\", \"version\": 1, \"metadata\": { \"designer\": { \"x\": -46, \"y\": 47, \"messages\": [ { \"category\": \"last\", \"severity\": \"warning\", \"message\": \"A transformer should not be the last module in the route.\" } ] } } } ], \"metadata\": { \"version\": 1, \"scenario\": { \"roundtrips\": 1, \"maxErrors\": 3, \"autoCommit\": true, \"autoCommitTriggerLast\": true, \"sequential\": false, \"confidential\": false, \"dataloss\": false, \"dlq\": false }, \"designer\": { \"orphans\": [ ] } } }"
}
```

<h3 id="updatescenariov1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|scenarioId|path|integer(int64)|true|The unique ID of the scenario.|
|body|body|[UpdateScenarioRequest](#schemaupdatescenariorequest)|true|Scenario definition.|

> Example responses

> 201 Response

```json
{
  "id": 925,
  "teamId": 1,
  "folderId": 1,
  "name": "New scenario"
}
```

<h3 id="updatescenariov1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|201|[Created](https://tools.ietf.org/html/rfc7231#section-6.3.2)|The updated scenario.|[Scenario](#schemascenario)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad request.|[ErrorResponse](#schemaerrorresponse)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Bad request.|[ErrorResponse](#schemaerrorresponse)|

<aside class="success">
This operation does not require authentication
</aside>

## getScenarioBlueprintV1

<a id="opIdgetScenarioBlueprintV1"></a>

`GET /v1/scenarios/{scenarioId}/blueprint`

*Get scenario's blueprint.*

Download the blueprint (scenario definition) as a JSON string.

<h3 id="getscenarioblueprintv1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|scenarioId|path|integer(int64)|true|The unique ID of the scenario.|

> Example responses

> 200 Response

```json
{
  "name": "Empty integration",
  "modules": [
    {
      "id": 2,
      "name": "json:ParseJSON"
    }
  ],
  "json": "{ \"name\": \"Empty integration\", \"flow\": [ { \"id\": 2, \"module\": \"json:ParseJSON\", \"version\": 1, \"metadata\": { \"designer\": { \"x\": -46, \"y\": 47, \"messages\": [ { \"category\": \"last\", \"severity\": \"warning\", \"message\": \"A transformer should not be the last module in the route.\" } ] } } } ], \"metadata\": { \"version\": 1, \"scenario\": { \"roundtrips\": 1, \"maxErrors\": 3, \"autoCommit\": true, \"autoCommitTriggerLast\": true, \"sequential\": false, \"confidential\": false, \"dataloss\": false, \"dlq\": false }, \"designer\": { \"orphans\": [ ] } } }"
}
```

<h3 id="getscenarioblueprintv1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|The scenario's blueprint.|[Blueprint](#schemablueprint)|

<aside class="success">
This operation does not require authentication
</aside>

## setModuleDataV1

<a id="opIdsetModuleDataV1"></a>

`PUT /v1/scenarios/{scenarioId}/data`

*Set module data.*

Updates a specific module in a scenario

> Body parameter

```json
{
  "scenarioId": 1,
  "modules": [
    {
      "moduleId": 1,
      "key": "json",
      "value": "{\"greeting\":\"hello world\"}"
    },
    {
      "moduleId": 2,
      "key": "value",
      "value": "hello world!"
    }
  ]
}
```

<h3 id="setmoduledatav1-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|scenarioId|path|integer(int64)|true|The unique ID of the scenario.|
|body|body|[SetModuleDataRequest](#schemasetmoduledatarequest)|true|Module update definition.|

> Example responses

> 200 Response

```json
{
  "result": true
}
```

<h3 id="setmoduledatav1-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|The update result.|[UpdateResult](#schemaupdateresult)|
|400|[Bad Request](https://tools.ietf.org/html/rfc7231#section-6.5.1)|Bad request.|[ErrorResponse](#schemaerrorresponse)|
|500|[Internal Server Error](https://tools.ietf.org/html/rfc7231#section-6.6.1)|Bad request.|[ErrorResponse](#schemaerrorresponse)|

<aside class="success">
This operation does not require authentication
</aside>

# Schemas

<h2 id="tocS_CreateScenarioRequest">CreateScenarioRequest</h2>
<!-- backwards compatibility -->
<a id="schemacreatescenariorequest"></a>
<a id="schema_CreateScenarioRequest"></a>
<a id="tocScreatescenariorequest"></a>
<a id="tocscreatescenariorequest"></a>

```json
{
  "teamId": 1,
  "folderId": 1,
  "blueprint": "{ \"name\": \"Empty integration\", \"flow\": [ { \"id\": 2, \"module\": \"json:ParseJSON\", \"version\": 1, \"metadata\": { \"designer\": { \"x\": -46, \"y\": 47, \"messages\": [ { \"category\": \"last\", \"severity\": \"warning\", \"message\": \"A transformer should not be the last module in the route.\" } ] } } } ], \"metadata\": { \"version\": 1, \"scenario\": { \"roundtrips\": 1, \"maxErrors\": 3, \"autoCommit\": true, \"autoCommitTriggerLast\": true, \"sequential\": false, \"confidential\": false, \"dataloss\": false, \"dlq\": false }, \"designer\": { \"orphans\": [ ] } } }"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|teamId|integer(int64)|true|none|The unique ID of the team in which the scenario will be created.|
|folderId|integer(int64)|true|none|The unique ID of the folder in which you want to store created scenario.|
|blueprint|string|true|none|The scenario blueprint. To save resources, the blueprint is sent as a string, not as an object.|
|encoded|boolean|false|none|Enable this option in order to pass the scenario blueprint as a Base64-encoded string. This option is disabled by default.|

<h2 id="tocS_UpdateScenarioRequest">UpdateScenarioRequest</h2>
<!-- backwards compatibility -->
<a id="schemaupdatescenariorequest"></a>
<a id="schema_UpdateScenarioRequest"></a>
<a id="tocSupdatescenariorequest"></a>
<a id="tocsupdatescenariorequest"></a>

```json
{
  "blueprint": "{ \"name\": \"Empty integration\", \"flow\": [ { \"id\": 2, \"module\": \"json:ParseJSON\", \"version\": 1, \"metadata\": { \"designer\": { \"x\": -46, \"y\": 47, \"messages\": [ { \"category\": \"last\", \"severity\": \"warning\", \"message\": \"A transformer should not be the last module in the route.\" } ] } } } ], \"metadata\": { \"version\": 1, \"scenario\": { \"roundtrips\": 1, \"maxErrors\": 3, \"autoCommit\": true, \"autoCommitTriggerLast\": true, \"sequential\": false, \"confidential\": false, \"dataloss\": false, \"dlq\": false }, \"designer\": { \"orphans\": [ ] } } }"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|blueprint|string|true|none|The scenario blueprint. To save resources, the blueprint is sent as a string, not as an object.|
|encoded|boolean|false|none|Enable this option in order to pass the scenario blueprint as a Base64-encoded string. This option is disabled by default.|

<h2 id="tocS_SetModuleDataRequest">SetModuleDataRequest</h2>
<!-- backwards compatibility -->
<a id="schemasetmoduledatarequest"></a>
<a id="schema_SetModuleDataRequest"></a>
<a id="tocSsetmoduledatarequest"></a>
<a id="tocssetmoduledatarequest"></a>

```json
{
  "scenarioId": 1,
  "modules": [
    {
      "moduleId": 1,
      "key": "json",
      "value": "{\"greeting\":\"hello world\"}"
    },
    {
      "moduleId": 2,
      "key": "value",
      "value": "hello world!"
    }
  ]
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|scenarioId|integer(int64)|true|none|The unique ID of the scenario.|
|modules|[[ModuleUpdate](#schemamoduleupdate)]|true|none|none|

<h2 id="tocS_ModuleUpdate">ModuleUpdate</h2>
<!-- backwards compatibility -->
<a id="schemamoduleupdate"></a>
<a id="schema_ModuleUpdate"></a>
<a id="tocSmoduleupdate"></a>
<a id="tocsmoduleupdate"></a>

```json
{
  "moduleId": 0,
  "key": "string",
  "value": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|moduleId|integer(int64)|false|none|The unique ID of the module.|
|key|string|false|none|The name of the updated field in the module.|
|value|string|false|none|The new field value. No validation is performed, the value can be set to anything you want. Use with care.|

<h2 id="tocS_Scenario">Scenario</h2>
<!-- backwards compatibility -->
<a id="schemascenario"></a>
<a id="schema_Scenario"></a>
<a id="tocSscenario"></a>
<a id="tocsscenario"></a>

```json
{
  "id": 925,
  "teamId": 1,
  "folderId": 1,
  "name": "New scenario"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|true|none|none|
|teamId|integer(int64)|true|none|none|
|folderId|integer(int64)|true|none|none|
|name|string|true|none|none|

<h2 id="tocS_Blueprint">Blueprint</h2>
<!-- backwards compatibility -->
<a id="schemablueprint"></a>
<a id="schema_Blueprint"></a>
<a id="tocSblueprint"></a>
<a id="tocsblueprint"></a>

```json
{
  "name": "Empty integration",
  "modules": [
    {
      "id": 2,
      "name": "json:ParseJSON"
    }
  ],
  "json": "{ \"name\": \"Empty integration\", \"flow\": [ { \"id\": 2, \"module\": \"json:ParseJSON\", \"version\": 1, \"metadata\": { \"designer\": { \"x\": -46, \"y\": 47, \"messages\": [ { \"category\": \"last\", \"severity\": \"warning\", \"message\": \"A transformer should not be the last module in the route.\" } ] } } } ], \"metadata\": { \"version\": 1, \"scenario\": { \"roundtrips\": 1, \"maxErrors\": 3, \"autoCommit\": true, \"autoCommitTriggerLast\": true, \"sequential\": false, \"confidential\": false, \"dataloss\": false, \"dlq\": false }, \"designer\": { \"orphans\": [ ] } } }"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|name|string|true|none|Scenario name.|
|json|string|true|none|The raw blueprint JSON.|
|modules|[[Module](#schemamodule)]|false|none|none|

<h2 id="tocS_Module">Module</h2>
<!-- backwards compatibility -->
<a id="schemamodule"></a>
<a id="schema_Module"></a>
<a id="tocSmodule"></a>
<a id="tocsmodule"></a>

```json
{
  "id": 0,
  "name": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|integer(int64)|true|none|The unique ID of the module within the scenario.|
|name|string|true|none|The module name.|

<h2 id="tocS_UpdateResult">UpdateResult</h2>
<!-- backwards compatibility -->
<a id="schemaupdateresult"></a>
<a id="schema_UpdateResult"></a>
<a id="tocSupdateresult"></a>
<a id="tocsupdateresult"></a>

```json
{
  "result": true
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|result|boolean|true|none|Returns `true` if the operation was successful, `false` otherwise.|

<h2 id="tocS_ErrorResponse">ErrorResponse</h2>
<!-- backwards compatibility -->
<a id="schemaerrorresponse"></a>
<a id="schema_ErrorResponse"></a>
<a id="tocSerrorresponse"></a>
<a id="tocserrorresponse"></a>

```json
{
  "errorCode": "string",
  "errorMessage": "string"
}

```

The server encountered an error while processing this request.

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|errorCode|string|false|none|Error code.|
|errorMessage|string|false|none|A text message, presented primarily for troubleshooting purposes.|

