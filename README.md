# SUT Mock
Mocks a system under test (SUT) to be used for load testing.

The mock will respond to each request with a status code 200 and log the request except for the cases listed below.
Furthermore, it maintains a user session. All requests are written to files and can be retrieved via a REST endpoint.

## Special Endpoints

Requests to the following endpoints return special responses and will **not** be logged:

* GET /_status: returns wil a 200 if the SUT Mock is up and running, without logging the request.
* POST /_restart: resets the Mock and restarts it. Formerly recorded requests will be deleted.
* GET /_logs: returns all request logs collected so far in the following format (corresponding to Apache Logs): ```sessionId - - [timestamp] "method path protocol" responseCode -```

## Logging & Limits

The SUT Mock buffers each request and writes it to a file asynchronously.
**In case the internal buffer is full, the requests will be dropped** and the Mock will respond with a 500 status.
The number of threads removing requests from the buffer and writing them to files can be configured.

## Running SUT Mock

### Using Docker

The best option to run the SUT Mock is to use the Docker container available:

```
docker run -d --rm --name sutmock -p 8080:80 continuityproject/sutmock
```

### Using a Local Build

Alternatively, it can be built locally (see below) and started directly:

```
java -jar build/libs/sutmock.jar --port=8080
```

### Run Parameters

Both options to run the SUT Mock accept the following parameters.
Simply append them to the run commands shown above, e.g., ```... --sutmock.writers=4```

* --port: the port number to be used. Defaults to ```80```.
* --sutmock.buffersize: The maximum number of requests to be buffered. Defaults to ```1024```.
* --sutmock.writers: The number of writers removing the requests from the buffer. Defaults to half the number of available processors.
* --sutmock.file_prefix: The prefix for the load files. Defaults to ```logs/log_```.

## Build the SUT Mock

The SUT Mock is build using Gradle with the following command:

```
./gradlew build
```

Build a Docker container locally using the following command:

```
docker build . -t continuityproject/sutmock
```
