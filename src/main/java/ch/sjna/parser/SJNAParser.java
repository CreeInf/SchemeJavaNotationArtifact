package ch.sjna.parser;

import ch.sjna.model.*;
import java.util.*;

public class SJNAParser {
    private String input;
    private int pos;
    private int line;
    private int col;

    public Document parse(String content) throws ParseException {
        this.input = content;
        this.pos = 0;
        this.line = 1;
        this.col = 1;

        Document doc = new Document();

        while (!isAtEnd()) {
            skipWhitespaceAndComments();
            if (isAtEnd()) break;

            if (peek() == '/' && peekNext() == ':') {
                parseSchema(doc);
            } else {
                parseRootProperty(doc);
            }
        }

        return doc;
    }

    private void parseSchema(Document doc) throws ParseException {
        consume('/');
        consume(':');
        expect("schema");
        consume(':');
        skipWhitespace();

        String name = parseIdentifier();
        SchemaDefinition schema = new SchemaDefinition(name);

        skipWhitespace();
        consume('{');

        while (!isAtEnd() && peek() != '}') {
            skipWhitespaceAndComments();
            if (peek() == '}') break;

            String key = parseKey();
            EnumDefinition enumDef = null;

            if (peek() == '(') {
                enumDef = parseEnumOptions();
            }

            consume(':');
            skipWhitespace();

            String description = null;
            if (peek() == '/' && peekNext() == '/') {
                description = parseComment();
            }

            consume(';');

            schema.addProperty(new SchemaProperty(key, enumDef, description));
            skipWhitespaceAndComments();
        }

        consume('}');
        doc.addSchema(name, schema);
    }

    private void parseRootProperty(Document doc) throws ParseException {
        String key = parseKey();
        EnumDefinition enumDef = null;

        if (peek() == '(') {
            enumDef = parseEnumOptions();
        }

        consume(':');
        skipWhitespace();

        ValueNode value = parseValue();
        PropertyNode prop = new PropertyNode(key, value, enumDef);

        consume(';');
        doc.addProperty(key, prop);
    }

    private String parseKey() throws ParseException {
        skipWhitespace();
        if (peek() == '"') {
            return parseString();
        } else {
            return parseIdentifier();
        }
    }

    private EnumDefinition parseEnumOptions() throws ParseException {
        consume('(');
        List<String> options = new ArrayList<>();

        while (peek() != ')') {
            skipWhitespace();
            if (peek() == '"') {
                options.add(parseString());
            } else {
                options.add(parseIdentifier());
            }
            skipWhitespace();
            if (peek() == ',') {
                consume(',');
            }
        }

        consume(')');
        return new EnumDefinition(options);
    }

    private ValueNode parseValue() throws ParseException {
        skipWhitespace();
        char c = peek();

        if (c == '"') {
            return new ValueNode(parseString(), ValueNode.ValueType.STRING);
        } else if (c == '{') {
            return new ValueNode(parseObject(), ValueNode.ValueType.OBJECT);
        } else if (c == 't' || c == 'f') {
            return new ValueNode(parseBoolean(), ValueNode.ValueType.BOOLEAN);
        } else if (Character.isDigit(c) || c == '-') {
            return new ValueNode(parseNumber(), ValueNode.ValueType.NUMBER);
        } else {
            return new ValueNode(parseIdentifier(), ValueNode.ValueType.IDENTIFIER);
        }
    }

    private ObjectNode parseObject() throws ParseException {
        consume('{');
        ObjectNode obj = new ObjectNode();

        while (!isAtEnd() && peek() != '}') {
            skipWhitespaceAndComments();
            if (peek() == '}') break;

            String key = parseKey();
            EnumDefinition enumDef = null;

            if (peek() == '(') {
                enumDef = parseEnumOptions();
            }

            consume(':');
            skipWhitespace();

            ValueNode value = parseValue();
            PropertyNode prop = new PropertyNode(key, value, enumDef);

            consume(';');
            obj.addProperty(prop);
            skipWhitespaceAndComments();
        }

        consume('}');
        return obj;
    }

    private String parseString() throws ParseException {
        consume('"');
        StringBuilder sb = new StringBuilder();

        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\\') {
                advance();
                if (isAtEnd()) throw error("Unterminated string");
                char escaped = advance();
                switch (escaped) {
                    case 'n': sb.append('\n'); break;
                    case 't': sb.append('\t'); break;
                    case 'r': sb.append('\r'); break;
                    case '"': sb.append('"'); break;
                    case '\\': sb.append('\\'); break;
                    default: sb.append(escaped);
                }
            } else {
                sb.append(advance());
            }
        }

        consume('"');
        return sb.toString();
    }

    private String parseIdentifier() throws ParseException {
        StringBuilder sb = new StringBuilder();

        if (!Character.isJavaIdentifierStart(peek())) {
            throw error("Expected identifier");
        }

        while (!isAtEnd() && (Character.isJavaIdentifierPart(peek()) || peek() == '_')) {
            sb.append(advance());
        }

        return sb.toString();
    }

    private Number parseNumber() throws ParseException {
        StringBuilder sb = new StringBuilder();
        boolean isDouble = false;

        if (peek() == '-') {
            sb.append(advance());
        }

        while (!isAtEnd() && (Character.isDigit(peek()) || peek() == '.')) {
            if (peek() == '.') {
                isDouble = true;
            }
            sb.append(advance());
        }

        try {
            return isDouble ? Double.parseDouble(sb.toString()) : Long.parseLong(sb.toString());
        } catch (NumberFormatException e) {
            throw error("Invalid number format");
        }
    }

    private Boolean parseBoolean() throws ParseException {
        if (peek() == 't') {
            expect("true");
            return true;
        } else {
            expect("false");
            return false;
        }
    }

    private String parseComment() {
        advance(); // '/'
        advance(); // '/'
        StringBuilder sb = new StringBuilder();

        while (!isAtEnd() && peek() != '\n') {
            sb.append(advance());
        }

        return sb.toString().trim();
    }

    private void skipWhitespaceAndComments() {
        while (!isAtEnd()) {
            skipWhitespace();
            if (peek() == '/' && peekNext() == '/') {
                while (!isAtEnd() && peek() != '\n') {
                    advance();
                }
            } else {
                break;
            }
        }
    }

    private void skipWhitespace() {
        while (!isAtEnd() && Character.isWhitespace(peek())) {
            advance();
        }
    }

    private void expect(String expected) throws ParseException {
        for (char c : expected.toCharArray()) {
            if (peek() != c) {
                throw error("Expected '" + expected + "'");
            }
            advance();
        }
    }

    private void consume(char expected) throws ParseException {
        if (peek() != expected) {
            throw error("Expected '" + expected + "' but got '" + peek() + "'");
        }
        advance();
    }

    private char peek() {
        return isAtEnd() ? '\0' : input.charAt(pos);
    }

    private char peekNext() {
        return pos + 1 >= input.length() ? '\0' : input.charAt(pos + 1);
    }

    private char advance() {
        char c = input.charAt(pos++);
        if (c == '\n') {
            line++;
            col = 1;
        } else {
            col++;
        }
        return c;
    }

    private boolean isAtEnd() {
        return pos >= input.length();
    }

    private ParseException error(String message) {
        return new ParseException(message + " at line " + line + ", column " + col);
    }
}
