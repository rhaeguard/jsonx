package io.rhaeguard;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonXTest {

    @Test
    void parse() {
        Object result = JsonX.parse("null");
        assertNull(result);
    }

    @Test
    void parse_1() {
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

        assertEquals(
                """
                        {glossary={title=example glossary, GlossDiv={GlossList={GlossEntry={GlossTerm=Standard Generalized Markup Language, GlossSee=markup, SortAs=SGML, GlossDef={para=A meta-markup language, used to create markup languages such as DocBook., GlossSeeAlso=[GML, XML]}, ID=SGML, Acronym=SGML, Abbrev=ISO 8879:1986}}, title=S}}}
                        """.trim(),
                parse.toString()
        );
    }
}