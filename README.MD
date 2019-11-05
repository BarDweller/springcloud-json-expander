## Spring Cloud Environment JSON Expansion.

A simple project to expand JSON objects within Spring Environment Properties to become new hierarchically named properties. 

Eg. 

```
myproperty = { "first": 50, "second":"fish", "obj": { "bar": "foo" } }
```

would be expanded to 
```
myproperty.first=50
myproperty.second=fish
myproperty.obj.bar=foo
```

The project processes any existing enumerable property sources, and any property sources retrievable via property source locators.

The resulting properties can then be used in placeholders, allowing for simple mapping of nested json values to configuration for other Spring Starters.

This projects works well in conjuction with Spring Cloud Kubernetes, where configmaps/secrets are injected containing JSON.

### Usage

Add
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
to your pom.xml (as a child of the project element.

Then add the dependency to the dependencies section
```
<dependency>
    <groupId>com.github.BarDweller</groupId>
    <artifactId>springcloud-json-expander</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```

Other versions may be available depending on which releases are present for this project in GitHub. 

Currently this project is still being developed, so there's only the master-SNAPSHOT. 

### Future

Mapping is currently limited to key/value objects, arrays are not explicitly supported (yet).

Arrays should probaly work as if the property was declared as a list in yaml. YMMV if using arrays today =)

