# Cars API

Java Spring boot app that provides a Rest API for adding, retrieving and deleting Cars

## API

### Add

Add a Car

#### Request

```http
POST /api/v1/cars
Content-Type: application/json

{
    "make": "Ford",
    "model": "Focus",
    "colour": "Blue",
    "year": 2010
}
```

#### Response

```http
HTTP 201 Created
Location: /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
```

### Update

Update an existing car

#### Request

##### Full update

```http
PUT /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
Content-Type: application/json

{
    "make": "Ford",
    "model": "Focus",
    "colour": "Blue",
    "year": 2010
}
```

##### Partial update

```http
PUT /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
Content-Type: application/json

{
    "make": "Ford",
}
```

#### Response

##### Successful

```http
HTTP 204 No Content
```

##### Car not found

```http
 HTTP 404 Not Found
```

### Retrieve

Retrieve an existing car

#### Request

```http
GET /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
Accept: application/json
```

#### Response

##### Successful

```http
HTTP 200 OK
Content-Type: application/json

{
    "make": "Ford",
    "model": {
        "name": "Focus",
        "homophones": "focus, fokus, phocus, ficus, focas"
    },
    "colour": "Blue",
    "year": 2010
}
```

##### Car not found

```http
 HTTP 404 Not Found
```

### Delete

Delete an existing car

#### Request

```http
DELETE /api/v1/cars/8a5fea9c-43ff-44d3-a334-e1eca5f209fb
```

#### Response

##### Successful

```http
HTTP 200 OK
```

##### Car not found

```http
 HTTP 404 Not Found
```

### Find by make

Search cars by make

#### Request

```http
GET /api/v1/cars/make/Ford
Accept: application/json
```

#### Response

##### Successful

```http
HTTP 200 OK
Content-Type: application/json

[
    {
        "make": "Ford",
        "model": {
            "name": "Focus",
            "homophones": "focus, fokus, phocus, ficus, focas"
        },
        "colour": "Blue",
        "year": 2010
    }
]
```

##### No matches

```http
HTTP 200 OK
Content-Type: application/json

[]
```

### Find by make & model

Search cars by make & model

#### Request

```http
GET /api/v1/cars/make/Ford/model/Focus
Accept: application/json
```

#### Response

##### Successful

```http
HTTP 200 OK
Content-Type: application/json

[
    {
        "make": "Ford",
        "model": {
            "name": "Focus",
            "homophones": "focus, fokus, phocus, ficus, focas"
        },
        "colour": "Blue",
        "year": 2010
    }
]
```

##### No matches

```http
HTTP 200 OK
Content-Type: application/json

[]
```

## Run App

### Jar

To build an executable jar we just need to run the gradle task `bootJar`

```shell
./gradlew :server:bootJar
```

this will create a jar in `server/build/libs/server.jar` this can then be executed with

```shell
java -jar server.jar
```

### Docker

This project uses [GoogleContainerTools/jib](https://github.com/GoogleContainerTools/jib) which is a project to create
OCI images without the need of a docker daemon. Although it can build images without a docker daemon it also allows
images to be build with the daemon.

To build the image run the gradle task `jibDockerBuild`

```shell
./gradlew :server:jibDockerBuild
```

The project also supplies a `Dockerfile` in case there are issues with the `jib` gradle plugin on your system. To build
the image we can do this:

```shell
docker build -t server .
```

The above task with build the jar inside the container, so it might take a while.

`jib` will tag the image as `server:0.0.1` and the manual docker build will create an image called `server`. To then run
the image we simply do:

```shell
docker run -p 8080:8080 -it <image name>
```

