# spring-cloud-config-bug

Reproduction of Spring Cloud Config yaml and properties endpoints not working the same way as Spring Boot `@ConfigurationProperties`.

## Running the reproduction

### Working example (local .properties and .yaml configuration)

```sh
> ./mvnw -Dspring-boot.run.profiles=local -pl client clean spring-boot:run
```

You should see the following output in the console. It's the `@ConfigurationProperties` annotated class `ClientTestConfigurationProperties` values being output as JSON in the console by the application.

```
=============
{
  "mapFromProperties" : {
    "from" : "application-local.properties",
    "zoq.fot.pik" : "1235"
  },
  "mapFromYaml" : {
    "from" : "application-local.yaml",
    "zoq.fot.pik" : "1235"
  }
}
=============
```

### Working example (remote config server)

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
    "zoq.fot.pik" : "123567890"
  }
}
=============
```
