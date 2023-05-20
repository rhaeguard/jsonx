# JsonX

A tiny Json parsing library written in Java for no reason whatsoever. Use it at your own risk. Does not support many things.

```xml
<dependency>
  <groupId>io.rhaeguard</groupId>
  <artifactId>jsonx</artifactId>
  <version>VERSION</version>
</dependency>
```

Example:

```java
Object parse = JsonX.parse("""
{
    "glossary": {
        "title": "example glossary",
        "GlossDiv": {
            "title": "S",
            "GlossList": {
                "GlossEntry": {
                    "ID": "SGML",
                    "SortAs": "SGML",
                    "GlossTerm": "Standard Generalized Markup Language",
                    "Acronym": "SGML",
                    "Abbrev": "ISO 8879:1986",
                    "GlossDef": {
                        "para": "A meta-markup language, used to create markup languages such as DocBook.",
                        "GlossSeeAlso": ["GML", "XML"]
                    },
                    "GlossSee": "markup"
                }
            }
        }
    }
}
""");
```
