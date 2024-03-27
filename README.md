# resources

![CI](https://github.com/enr/resources/workflows/CI/badge.svg)

[![](https://jitpack.io/v/enr/resources.svg)](https://jitpack.io/#enr/resources)

Library to access resources in agnostic way: classpath, filesystem, url...


## Usage

Add the JitPack repository to your build file

```xml
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Add the dependency

```xml
<dependency>
    <groupId>com.github.enr</groupId>
    <artifactId>resources</artifactId>
    <version>${resources.version}</version>
</dependency>
```

```java
// create the loader
ResourceLoader resourceLoader = ResourceLoader.defaultInstance();
// load the resource from classpath
Resource resource = resourceLoader.get("classpath:foo.properties");
// or environment variable
resource = resourceLoader.get("env:FOO");
// or url
resource = resourceLoader.get("http://localhost:9990/foo");
// or filesystem
resource = resourceLoader.get("file:/tmp/foo.json");
resource = resourceLoader.get("/tmp/foo.json");
// use resource
boolean exists = resource.exists();
String str = resource.getAsString();
byte[] bytes = resource.getAsBytes();
InputStream is = resource.getAsInputStream();
```



## Development

Build:

```
mvn install
```

Full check (test and formatting):

```
mvn -Pci
```

Repair formatting:

```
mvn -Pfmt
```

Fast build (skip any check and file generation):

```
mvn -Pfast
```
