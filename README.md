# resources

![CI](https://github.com/enr/resources/workflows/CI/badge.svg)

[![](https://jitpack.io/v/enr/resources.svg)](https://jitpack.io/#enr/resources)

Java library template

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

## Usage

To get this project into your build:

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
