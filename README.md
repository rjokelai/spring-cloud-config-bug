# spring-cloud-config-bug

Reproduction of Spring Cloud Config `.yaml`, `.json`, and `.properties` endpoints not working the same way as Spring Boot `@ConfigurationProperties`. This causes problems when trying to use the config server in other than spring boot applications.

In a Spring Boot world the following are equally valid and are bound properly to instances of `java.util.Map` as seen in both local and remote examples using client and server applications

```properties
client.mapFromProperties[foo.bar]=baz
client.mapFromProperties.'zoq.fot.pik'=sc2
```

```yaml
client:
  mapFromYaml:
    foo.bar: baz
    'zoq.fot.pik': sc2
```

This will end up with a regular Java map instance with dots in the keys as expected, see [ClientTestConfigurationProperties.java](./client/src/main/java/com/example/rjokelai/configserverbug/ClientTestConfigurationProperties.java). I would expect the .yaml, .properties, and .json endpoints to behave in a similar fashion. See also [this issue](https://github.com/spring-projects/spring-boot/issues/8326).

## Working example (local .properties and .yaml configuration)

This example will use the following configuration files:

- [client/application.yaml](./client/src/main/resources/application.yaml)
- [client/application-local.yaml](./client/src/main/resources/application-local.yaml)
- [client/application-local.properties](./client/src/main/resources/application-local.properties)

Run the following command

```sh
> ./mvnw -Dspring-boot.run.profiles=local -pl client clean spring-boot:run
```

You should see the following output in the console. It's the `@ConfigurationProperties` annotated class `ClientTestConfigurationProperties` values being output as JSON in the console by the application.

```
=============
{
  "mapFromProperties" : {
    "from" : "application-local.properties",
    "zoq.fot.pik" : "sc2",
    "foo.bar" : "baz"
  },
  "mapFromYaml" : {
    "from" : "application-local.yaml",
    "zoq.fot.pik" : "sc2",
    "foo.bar" : "baz"
  }
}
=============
```

## Working example (remote config server)

This example will use the following configuration files:

- [client/application.yaml](./client/src/main/resources/application.yaml)
- [client/application-remote.yaml](./client/src/main/resources/application-remote.yaml)
- [server/bugclient-remote.yaml](./server/src/main/resources/config/bugclient-remote.yaml)
- [server/bugclient-remote.properties](./server/src/main/resources/config/bugclient-remote.properties)

First launch the server

```sh
> ./mvnw -pl server clean spring-boot:run
```

Then launch the client

```sh
> ./mvnw -Dspring-boot.run.profiles=remote -pl client clean spring-boot:run
```

You should see the following output in the console. It's the `@ConfigurationProperties` annotated class `ClientTestConfigurationProperties` values being output as JSON in the console by the application. This time, the configuration was laoded over the network by the Spring Cloud Config Client.

```
=============
{
  "mapFromProperties" : {
    "from" : "bugclient-remote.properties",
    "zoq.fot.pik" : "1235"
  },
  "mapFromYaml" : {
    "from" : "bugclient-remote.yaml",
    "zoq.fot.pik" : "Make no hostile actions"
  }
}
=============
```

## Not working: .yaml, .json endpoints

In the above examples you can see maps can easily be instantiated with dots in the keys for maps assigned by Spring Boot `@ConfigurationProperties`. The expected outcome of the .yaml and .json files served by the config server would be a combination of the .yaml and .properties files used by the server. In this case the example is using the plain and simple native driver but, e.g., a JDBC driver should be working the same way.

However, when configuring the keys like this, the .yaml and .json endpoints just fail with a 500 server error. .properties endpoint on the other hand works as expected for properties defined in .properties, but not so much for .yaml. The following is the output from http://localhost:8182/bugclient-remote.properties after running the config server

```properties
client.mapFromYaml.from: bugclient-remote.yaml
client.mapFromYaml.foo.bar: baz-quux
client.mapFromYaml.zoq.fot.pik: Make no hostile actions
client.mapFromProperties.from: bugclient-remote.properties
client.mapFromProperties.'zoq.fot.pik': We come in peace
client.mapFromProperties[foo.bar]: baz-quux
```

## Expected output

The expected results for the endpoints would be as follows:

### Remote JSON

Open http://localhost:8182/bugclient-remote.json

```json
{
  "client": {
    "mapFromYaml": {
      "from": "bugclient-remote.yaml",
      "foo.bar": "baz-quux",
      "zoq.fot.pik": "Make no hostile actions"
    },
    "mapFromProperties": {
      "from": "bugclient-remote.properties",
      "foo.bar": "baz-quux",
      "zoq.fot.pik": "We come in peace"
    }
  }
}
```

### Remote YAML

Open http://localhost:8182/bugclient-remote.yaml

Quoting the keys of YAML maps is allowed to make them be treated as strings:

```yaml
client:
  mapFromYaml:
    from: bugclient-remote.yaml
    'foo.bar': baz-quux
    'zoq.fot.pik': Make no hostile actions
  mapFromProperties:
    from: bugclient-remote.properties
    'foo.bar': baz-quux
    'zoq.fot.pik': We come in peace
```

### Remote .properties

Open http://localhost:8182/bugclient-remote.properties

Note that in the example I have used the single quotes after the dot notation. It doesn't matter which of the formats is selected as long as it's consistent. Using `[foo.bar]` style might be more readable, but might make it harder to figure out the correct type (object vs. array) for the bound properties.

```properties
client.mapFromYaml.from: bugclient-remote.yaml
client.mapFromYaml.'foo.bar': baz-quux
client.mapFromYaml.'zoq.fot.pik': Make no hostile actions
client.mapFromProperties.from: bugclient-remote.properties
client.mapFromProperties.'foo.bar': baz-quux
client.mapFromProperties.'zoq.fot.pik': We come in peace
```
