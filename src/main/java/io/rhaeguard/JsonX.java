package io.rhaeguard;

import java.util.*;
import java.util.regex.Pattern;

public class JsonX {

    private final String json;
    private int pos;

    private final Pattern numberChars = Pattern.compile("[0-9]");

    private static final char OPEN_CURLY = '{';
    private static final char CLOSE_CURLY = '}';
    private static final char OPEN_BRACKET = '[';
    private static final char CLOSE_BRACKET = ']';
    private static final char COMMA = ',';
    private static final char DOUBLE_QUOTE = '"';
    private static final char COLON = ':';
    private static final char BACKSLASH = '\\';

    private JsonX(String json) {
        this.json = json;
        this.pos = 0;

        skipWhitespace();
    }

    public static Object parse(String jsonPayload) {
        if (jsonPayload == null || jsonPayload.isBlank() || jsonPayload.equalsIgnoreCase("null")) return null;

        JsonX object = new JsonX(jsonPayload);

        char first = getFirstChar(jsonPayload);

        if (first == OPEN_CURLY) {
            return object.parseObject();
        } else if (first == OPEN_BRACKET) {
            return object.parseArray();
        } else {
            throw new IllegalArgumentException("invalid json provided");
        }
    }

    private static char getFirstChar(String jsonPayload) {
        int pos = 0;
        while (pos < jsonPayload.length() && isWhitespace(jsonPayload.charAt(pos))) {
            pos++;
        }

        if (pos >= jsonPayload.length()) {
            throw new IllegalArgumentException("json is blank");
        }

        return jsonPayload.charAt(pos);
    }

    private Map<String, Object> parseObject() {
        expectChar(OPEN_CURLY);
        skipWhitespace();

        Map<String, Object> map = new HashMap<>();

        while (getChar() != CLOSE_CURLY) {
            var kvPair = readProperty();
            skipWhitespace();
            if (getChar() != CLOSE_CURLY) {
                expectChar(COMMA);
            }
            map.put(kvPair.key, kvPair.value);
        }
        expectChar(CLOSE_CURLY);
        return map;
    }

    private JsonKeyValue readProperty() {
        var key = readKey();
        skipWhitespace();
        expectChar(COLON);
        skipWhitespace();
        Object value = readValue();
        return new JsonKeyValue(key, value);
    }

    private Object readValue() {
        if (getChar() == OPEN_CURLY) {
            return parseObject();
        } else if (getChar() == OPEN_BRACKET) {
            return parseArray();
        }
        return parseSingleValue();
    }

    private String readKey() {
        skipWhitespace();
        expectChar(DOUBLE_QUOTE);
        var string = readString();
        skipWhitespace();
        return string;
    }

    private String readString() {
       return readUntil(Set.of(DOUBLE_QUOTE));
    }

    private Object parseSingleValue() {
        char aChar = getChar();

        if (aChar == DOUBLE_QUOTE) {
            expectChar(DOUBLE_QUOTE);
            var string = readString();
            expectChar(DOUBLE_QUOTE);
            return string;
        } else if (peekWord("true") || peekWord("false")) {
            return parseBoolean();
        } else if (numberChars.matcher(String.valueOf(aChar)).matches()) {
            return parseNumber();
        }
        return null;
    }

    private Object parseNumber() {
        String numberAsString = readUntil(Set.of(COMMA, CLOSE_BRACKET, CLOSE_CURLY));
        return Integer.parseInt(numberAsString);
    }

    private boolean peekWord(String expected) {
        try {
            String substring = this.json.substring(this.pos, expected.length());
            return substring.equals(expected);
        } catch (Exception ex) {
            return false;
        }
    }

    private String readUntil(Set<Character> terminationChars) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            boolean shouldTerminate = terminationChars.contains(getChar());
            if (shouldTerminate && getPrevChar() != BACKSLASH) {
                return sb.toString();
            }
            sb.append(getChar());
        }
    }

    private Object parseBoolean() {
        if (getChar() == 't') {
            this.pos += 4;
            return Boolean.TRUE;
        } else {
            this.pos += 5;
            return Boolean.FALSE;
        }
    }

    public List<Object> parseArray() {
        expectChar(OPEN_BRACKET);
        List<Object> objects = new ArrayList<>();
        while (getChar() != CLOSE_BRACKET) {
            skipWhitespace();
            Object value = readValue();
            objects.add(value);
            skipWhitespace();
            if (getChar() != CLOSE_BRACKET) {
                expectChar(COMMA);
            }
        }
        expectChar(CLOSE_BRACKET);
        return objects;
    }

    public void expectChar(char ch) {
        if (getChar() != ch) {
            throw new RuntimeException("expected %c at pos: %d".formatted(ch, pos));
        }
        pos++;
    }

    public void skipWhitespace() {
        while (isWhitespace(getChar())) {
            pos++;
        }
    }

    private static boolean isWhitespace(char ch) {
        return ch == ' '
                || ch == '\t'
                || ch == '\n';
    }

    private char getChar() {
        return json.charAt(pos);
    }

    private char getPrevChar() {
        return json.charAt(pos - 1);
    }

    record JsonKeyValue(String key, Object value) {
    }

}
