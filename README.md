# SJNA – Scheme Java Notation Artifact
## Vollständige Dokumentation v1.1.2

---

## Inhaltsverzeichnis

1. [Überblick](#1-überblick)
2. [Das SJNA-Format](#2-das-sjna-format)
3. [Projektstruktur](#3-projektstruktur)
4. [Installation & Einbindung](#4-installation--einbindung)
5. [Kern-API: Klasse `SJNA`](#5-kern-api-klasse-sjna)
6. [Konfigurationszugriff: Klasse `SJNAConfig`](#6-konfigurationszugriff-klasse-sjnaconfig)
7. [Schemas](#7-schemas)
8. [Validierung](#8-validierung)
9. [Serialisierung](#9-serialisierung)
10. [Datenmodell (intern)](#10-datenmodell-intern)
11. [Vollständige Beispiele](#11-vollständige-beispiele)
12. [Fehlerbehandlung](#12-fehlerbehandlung)
13. [API-Referenz auf einen Blick](#13-api-referenz-auf-einen-blick)

---

## 1. Überblick

**SJNA** (Scheme Java Notation Artifact) ist ein leichtgewichtiges Java-Library zum Parsen, Manipulieren, Validieren und Serialisieren eines eigenen Konfigurationsformats namens **SJNA-Format**. Es kann als Alternative zu JSON, YAML oder TOML für Java-Projekte eingesetzt werden.

### Kernfähigkeiten

- **Parsen** von `.sjna`-Dateien und -Strings
- **Programmatischer Zugriff** auf Werte über Pfadnavigation (z.B. `"server.port"`)
- **Schemas** für wiederverwendbare Objektstrukturen
- **Validierung** von Werten und Enum-Einschränkungen
- **Serialisierung** zurück ins SJNA-Format
- **Builder-Pattern** für einfaches Aufbauen von Dokumenten

### Technische Anforderungen

- Java 21+
- Maven (Build-Tool)
- GroupId: `ch.AlexInf`, ArtifactId: `SJNA`, Version: `1.1.2`

---

## 2. Das SJNA-Format

SJNA ist ein strukturiertes Textformat. Die Grundregel: Jede Property endet mit einem Semikolon `;`.

### 2.1 Grundsyntax

```
schlüssel: wert;
```

### 2.2 Werttypen

| Typ        | Beispiel                        | Beschreibung                            |
|------------|---------------------------------|-----------------------------------------|
| String     | `name: "Alice";`                | In doppelten Anführungszeichen          |
| Zahl (Int) | `port: 8080;`                   | Ganzzahl                                |
| Zahl (Dec) | `ratio: 3.14;`                  | Dezimalzahl                             |
| Boolean    | `active: true;`                 | `true` oder `false`                     |
| Identifier | `mode: production;`             | Bezeichner ohne Anführungszeichen       |
| Objekt     | `server: { host: "localhost"; }` | Verschachteltes Objekt mit `{}`        |
| Liste      | `tags: ["a", "b", "c"];`        | Kommagetrennte Werte in `[]`            |

### 2.3 Kommentare

Kommentare beginnen mit `//` und gelten bis zum Zeilenende:

```
// Das ist ein Kommentar
port: 8080;  // Auch am Zeilenende möglich
```

### 2.4 Enum-Einschränkungen

Properties können auf eine Liste von Werten eingeschränkt werden:

```
// Nur "development", "staging" oder "production" erlaubt
environment(development, staging, production): production;
```

Auch mit String-Optionen:

```
level("INFO", "WARN", "ERROR"): "INFO";
```

### 2.5 Objekte (verschachtelt)

```
database: {
    host: "localhost";
    port: 5432;
    name: "mydb";
    ssl: false;
};
```

### 2.6 Listen

```
// Liste von Strings
tags: ["java", "config", "sjna"];

// Liste von Zahlen
ports: [8080, 8081, 8082];

// Liste von Objekten
servers: [
    { host: "server1"; port: 80; },
    { host: "server2"; port: 80; }
];
```

### 2.7 Schemas

Schemas definieren wiederverwendbare Objektstrukturen und werden am Anfang des Dokuments deklariert:

```
/: schema: Person {
    name:;           // Pflichtfeld, kein Kommentar
    age:;
    role("admin", "user", "guest"):;   // Enum-Feld
}
```

### 2.8 Vollständiges Beispiel einer SJNA-Datei

```
// Applikations-Konfiguration
/: schema: Server {
    host:;
    port:;
    ssl:;
}

appName: "MeineApp";
version: "1.0.0";
environment(development, staging, production): production;
debug: false;

server: {
    host: "localhost";
    port: 8080;
    ssl: true;
};

database: {
    host: "db.example.com";
    port: 5432;
    name: "appdb";
};

allowedRoles: ["admin", "user", "moderator"];
maxConnections: 100;
```

---

## 3. Projektstruktur

```
SchemeJavaNotationArtifact/
├── pom.xml
└── src/main/java/ch/sjna/
    ├── SJNA.java                    ← Haupt-API (Entry Point)
    ├── SJNAConfig.java              ← Konfigurationszugriff via Pfade
    ├── SJNASerializer.java          ← Serialisierung nach SJNA-Format
    ├── model/
    │   ├── Node.java                ← Basisinterface aller Nodes
    │   ├── NodeType.java            ← Enum: DOCUMENT, OBJECT, PROPERTY, VALUE, SCHEMA
    │   ├── Document.java            ← Wurzel eines geparsten Dokuments
    │   ├── ObjectNode.java          ← Verschachteltes Objekt {}
    │   ├── PropertyNode.java        ← Eine key: value; Zeile
    │   ├── ValueNode.java           ← Der Wert einer Property
    │   ├── SchemaDefinition.java    ← /: schema: Name {} Block
    │   ├── SchemaProperty.java      ← Ein Feld innerhalb eines Schemas
    │   └── EnumDefinition.java      ← Erlaubte Werte (a, b, c)
    ├── parser/
    │   ├── SJNAParser.java          ← Rekursiv-absteigender Parser
    │   └── ParseException.java      ← Parse-Fehler mit Zeile/Spalte
    └── validation/
        ├── Validator.java           ← Validierungslogik
        ├── ValidationResult.java    ← Ergebnis der Validierung
        └── ValidationError.java     ← Einzelner Validierungsfehler
```

---

## 4. Installation & Einbindung

### Maven (lokale Installation)

Da das Projekt über Maven gebaut wird, kann es lokal installiert werden:

```bash
mvn install
```

Dann als Dependency einbinden:

```xml
<dependency>
    <groupId>ch.AlexInf</groupId>
    <artifactId>SJNA</artifactId>
    <version>1.1.2</version>
</dependency>
```

### Direkter Import

Alternativ können die Quelldateien direkt in das eigene Projekt kopiert werden. Das Package `ch.sjna` enthält alle notwendigen Klassen.

### Imports

```java
import ch.sjna.SJNA;
import ch.sjna.SJNAConfig;
import ch.sjna.model.*;
import ch.sjna.parser.ParseException;
import ch.sjna.validation.*;
```

---

## 5. Kern-API: Klasse `SJNA`

Die Klasse `SJNA` ist der zentrale Einstiegspunkt. Alle Methoden sind `static`.

### 5.1 Datei laden

```java
// Lädt eine .sjna-Datei vom Dateisystem
Document doc = SJNA.load("config.sjna");
```

**Exceptions:** `IOException` (Datei nicht gefunden), `ParseException` (Syntaxfehler)

### 5.2 String parsen

```java
String content = """
    name: "Alice";
    age: 30;
    """;

Document doc = SJNA.parse(content);
```

### 5.3 Dokument speichern

```java
SJNA.save(doc, "output.sjna");
```

### 5.4 Dokument serialisieren (als String)

```java
String text = SJNA.serialize(doc);
System.out.println(text);
```

### 5.5 Validierung

```java
// Allgemeine Validierung (prüft Enum-Einschränkungen)
ValidationResult result = SJNA.validate(doc);

// Validierung gegen ein Schema
ValidationResult result = SJNA.validateAgainstSchema(doc, "Person");

if (!result.isValid()) {
    for (ValidationError error : result.getErrors()) {
        System.out.println("Fehler: " + error.getMessage());
    }
}
```

### 5.6 Konfigurationszugriff aktivieren

```java
SJNAConfig config = SJNA.asConfig(doc);
```

### 5.7 Instanzen aus Schemas erzeugen

```java
// Leere Instanz (alle Felder mit Default-Werten)
ObjectNode person = SJNA.createFromSchema(doc, "Person");

// Instanz mit Werten befüllen
Map<String, Object> values = new HashMap<>();
values.put("name", "Bob");
values.put("age", 25);
ObjectNode person = SJNA.createFromSchema(doc, "Person", values);
```

### 5.8 Schema-Instanz als Property einfügen

```java
// Schema-Instanz als benannte Property ins Dokument einfügen
SJNA.insertSchema(schemaDoc, "Person", targetDoc, "author", Map.of(
    "name", "Alice",
    "age", 30
));
```

### 5.9 Schema flach einfügen

```java
// Alle Properties der Schema-Instanz direkt ins Dokument-Root einfügen
SJNA.insertSchemaFlat(doc, "Person", targetDoc);

// Mit Werten
SJNA.insertSchemaFlat(doc, "Person", targetDoc, Map.of("name", "Alice"));

// Direkt mit SchemaDefinition-Objekt
SchemaDefinition schema = doc.getSchema("Person");
SJNA.insertSchemaFlat(schema, targetDoc, Map.of("name", "Alice"));
```

---

## 6. Konfigurationszugriff: Klasse `SJNAConfig`

`SJNAConfig` bietet einen bequemen Zugriff auf Werte über **Punkt-Pfade** wie `"server.database.host"`.

```java
SJNAConfig config = SJNA.asConfig(doc);
```

### 6.1 Werte lesen

#### Strings

```java
String name = config.getString("appName");
String host = config.getString("server.host");

// Mit Fallback-Wert
String mode = config.getString("mode", "default");
```

#### Zahlen

```java
// Als Number
Number port = config.getNumber("server.port");

// Als konkrete Typen
int port    = config.getInt("server.port");
long id     = config.getLong("database.id");
double rate = config.getDouble("ratio");

// Mit Fallback
int port = config.getInt("server.port", 80);
```

#### Boolean

```java
boolean debug = config.getBoolean("debug");
boolean ssl   = config.getBoolean("server.ssl", false);
```

#### Objekte

```java
ObjectNode serverNode = config.getObject("server");
```

#### Listen

```java
// String-Liste
List<String> tags = config.getList("tags");

// Zahlen-Liste
List<Number> ports = config.getNumberList("ports");

// Integer-Liste
List<Integer> ids = config.getIntList("ids");

// Objekt-Liste (als Maps)
List<Map<String, Object>> servers = config.getObjectList("servers");
```

#### Prüfen ob ein Pfad existiert

```java
if (config.hasPath("server.ssl")) {
    boolean ssl = config.getBoolean("server.ssl");
}
```

#### Alle Keys abrufen

```java
// Root-Level Keys
List<String> rootKeys = config.getKeys();

// Keys eines Unterobjekts
List<String> serverKeys = config.getKeys("server");
```

#### Gesamtes Dokument als Map

```java
Map<String, Object> all = config.getAllAsMap();

// Unterobjekt als Map
Map<String, Object> dbMap = config.getAsMap("database");
```

#### Alle Objekte sammeln

```java
// Alle Objekte auf Root-Ebene
List<ObjectNode> objects = config.getObjects(null);

// Alle Objekte unter einem Pfad
List<ObjectNode> items = config.getObjects("servers");

// Als Map (mit Keys)
Map<String, ObjectNode> namedObjects = config.getObjectsWithKeys(null);

// Als Liste von Maps
List<Map<String, Object>> maps = config.getObjectsAsMap("servers");
```

### 6.2 Werte setzen

`SJNAConfig` kann auch Werte schreiben. Existierende Werte werden überschrieben, nicht-existierende neu erstellt.

```java
config.setString("appName", "NeuerName");
config.setInt("server.port", 9090);
config.setLong("maxId", 99999L);
config.setDouble("ratio", 2.5);
config.setBoolean("debug", true);

// Enum-Wert setzen (mit Validierung)
config.setEnum("environment", List.of("dev", "prod"), "prod");

// Liste setzen
config.setList("tags", List.of("a", "b", "c"));

// Objekt setzen
config.setObject("database", Map.of(
    "host", "newdb.example.com",
    "port", 5432
));
```

### 6.3 Werte entfernen

```java
config.remove("debug");
config.remove("server.ssl");
```

### 6.4 Objekte hinzufügen

```java
// Auf Root-Ebene
config.addObject("cache", Map.of(
    "host", "redis.local",
    "port", 6379
));

// Unter einem Pfad
config.addObjectAt("server", "ssl", Map.of(
    "cert", "/path/to/cert.pem",
    "key", "/path/to/key.pem"
));

// Aus einem Schema
config.addFromSchema("Person", "author", Map.of(
    "name", "Charlie",
    "age", 28
));
```

### 6.5 Builder-Pattern

`SJNAConfig` stellt zwei Builder-Klassen bereit:

#### `ObjectBuilder`

```java
new SJNAConfig.ObjectBuilder(config, "newServer", null)
    .withString("host", "server3.example.com")
    .withInt("port", 443)
    .withBoolean("ssl", true)
    .withStringList("tags", List.of("web", "https"))
    .build();
```

`ObjectBuilder`-Methoden:

| Methode | Beschreibung |
|---|---|
| `with(key, value)` | Generischer Wert (auto-konvertiert) |
| `withString(key, value)` | String-Wert |
| `withNumber(key, value)` | Number-Wert |
| `withInt(key, value)` | Integer-Wert |
| `withBoolean(key, value)` | Boolean-Wert |
| `withObject(key, map)` | Verschachteltes Objekt |
| `withList(key, list)` | Generische Liste |
| `withStringList(key, list)` | String-Liste |
| `withNumberList(key, list)` | Number-Liste |
| `build()` | Fügt das Objekt ins Dokument ein |

#### `SchemaBuilder`

```java
new SJNAConfig.SchemaBuilder(config, "Person", "developer")
    .withString("name", "Dave")
    .withInt("age", 35)
    .with("role", "admin")
    .build();
```

`SchemaBuilder` funktioniert identisch zu `ObjectBuilder`, aber erstellt die Instanz auf Basis eines Schemas (inkl. Default-Werte und Enum-Validierung).

### 6.6 Zugriff auf das rohe Dokument

```java
Document doc = config.getDocument();
```

---

## 7. Schemas

Schemas ermöglichen die Wiederverwendung von Objektstrukturen.

### 7.1 Schema in SJNA definieren

```java
String content = """
    /: schema: DatabaseConfig {
        host:;           // Hostname der Datenbank
        port:;           // Port-Nummer
        name:;           // Name der Datenbank
        ssl:;            // SSL aktivieren
        mode("read", "readwrite", "admin"):;   // Zugriffstyp
    }
    """;
```

### 7.2 Schema in Java abrufen

```java
Document doc = SJNA.parse(content);
SchemaDefinition schema = doc.getSchema("DatabaseConfig");

System.out.println(schema.getName());  // "DatabaseConfig"

for (Map.Entry<String, SchemaProperty> entry : schema.getProperties().entrySet()) {
    SchemaProperty prop = entry.getValue();
    System.out.println("Feld: " + prop.getKey());
    if (prop.hasEnum()) {
        System.out.println("  Erlaubte Werte: " + prop.getEnumDefinition().getOptions());
    }
    if (prop.getDescription() != null) {
        System.out.println("  Beschreibung: " + prop.getDescription());
    }
}
```

### 7.3 Instanz aus Schema erzeugen

```java
// Ohne Werte → alle Felder mit Defaults (String: "", Enum: erster Wert)
ObjectNode empty = schema.createInstance();

// Mit Werten
ObjectNode db = schema.createInstance(Map.of(
    "host", "prod-db.example.com",
    "port", 5432,
    "name", "productiondb",
    "ssl", true,
    "mode", "readwrite"
));
```

**Default-Werte bei `createInstance()`:**
- Enum-Felder: erster Wert der Options-Liste
- Alle anderen Felder: leerer String `""`

---

## 8. Validierung

### 8.1 Allgemeine Validierung

Prüft alle Properties mit Enum-Einschränkungen im gesamten Dokument (inkl. verschachtelter Objekte):

```java
Document doc = SJNA.parse("""
    environment(development, staging, production): production;
    level("INFO", "WARN", "ERROR"): "DEBUG";
    """);

ValidationResult result = SJNA.validate(doc);

if (result.isValid()) {
    System.out.println("Dokument ist gültig.");
} else {
    for (ValidationError error : result.getErrors()) {
        System.out.println("Fehler: " + error.getMessage());
    }
    // Ausgabe: Fehler: Invalid enum value for 'level': DEBUG. Allowed: [INFO, WARN, ERROR]
}
```

### 8.2 Schema-Validierung

Prüft ob ein Dokument alle Felder eines Schemas enthält und die Enum-Einschränkungen einhält:

```java
Document doc = SJNA.parse("""
    /: schema: User {
        name:;
        role("admin", "user"):;
    }

    name: "Alice";
    role: "superuser";
    """);

ValidationResult result = SJNA.validateAgainstSchema(doc, "User");

// result.isValid() → false
// Fehler: Schema violation for 'role': superuser. Expected one of: [admin, user]
```

Wenn das Schema selbst nicht existiert:

```java
ValidationResult result = SJNA.validateAgainstSchema(doc, "NonExistent");
// result.isValid() → false
// Fehler: Schema not found: NonExistent
```

### 8.3 ValidationResult-API

```java
ValidationResult result = SJNA.validate(doc);

boolean ok = result.isValid();                    // true wenn keine Fehler
List<ValidationError> errors = result.getErrors(); // unmodifizierbare Liste

for (ValidationError e : errors) {
    String msg = e.getMessage();  // Fehlermeldung als String
    System.out.println(e);        // toString() gibt auch getMessage() zurück
}
```

---

## 9. Serialisierung

Ein `Document` kann jederzeit zurück ins SJNA-Format serialisiert werden:

```java
String sjna = SJNA.serialize(doc);
SJNA.save(doc, "output.sjna");
```

### Serialisierungsregeln

- Schemas werden immer zuerst ausgegeben
- Danach folgen alle Properties
- Objekte werden eingerückt (4 Leerzeichen pro Ebene)
- Keys, die nicht dem Identifier-Muster `[a-zA-Z_][a-zA-Z0-9_]*` entsprechen, werden in Anführungszeichen gesetzt
- Strings werden korrekt escaped (`\"`, `\n`, `\t`, `\r`, `\\`)

**Beispiel:**

```java
Document doc = SJNA.parse("""
    name: "Alice";
    server: {
        host: "localhost";
        port: 8080;
    };
    """);

System.out.println(SJNA.serialize(doc));
```

Ausgabe:
```
name: "Alice";

server: {
    host: "localhost";
    port: 8080;
};
```

---

## 10. Datenmodell (intern)

### Klassenübersicht

```
Node (Interface)
├── Document            → Wurzel-Node: enthält Schemas + Root-Properties
├── ObjectNode          → { ... } Block
├── PropertyNode        → key: value;
└── ValueNode           → Der eigentliche Wert
```

#### `Node` Interface

```java
NodeType getType();       // DOCUMENT | OBJECT | PROPERTY | VALUE | SCHEMA
String getComment();      // Zugehöriger Kommentar (// ...)
void setComment(String);
```

#### `Document`

```java
doc.get("key")                      // Node abrufen
doc.addProperty("key", node)        // Node hinzufügen
doc.removeProperty("key")           // Node entfernen
doc.getRoot()                       // Alle Root-Properties (unmodifizierbar)
doc.addSchema("Name", schema)       // Schema registrieren
doc.getSchema("Name")               // Schema abrufen
doc.getSchemas()                    // Alle Schemas (unmodifizierbar)
```

#### `PropertyNode`

```java
prop.getKey()                // z.B. "server"
prop.getValue()              // ValueNode
prop.getEnumDefinition()     // null wenn kein Enum
prop.hasEnum()               // true/false
```

#### `ValueNode`

```java
value.getValueType()         // STRING | NUMBER | BOOLEAN | IDENTIFIER | OBJECT | LIST
value.getValue()             // roher Object-Wert
value.asString()             // als String
value.asNumber()             // als Number (Long oder Double)
value.asBoolean()            // als Boolean
value.asObject()             // als ObjectNode
value.asList()               // als List<ValueNode>
```

#### `ObjectNode`

```java
obj.addProperty(propNode)            // Property hinzufügen
obj.getProperty("key")               // Einzelne Property
obj.getProperties()                  // Alle (unmodifizierbar, geordnet)
obj.removeProperty("key")            // Entfernen
```

#### `SchemaDefinition`

```java
schema.getName()                         // Name des Schemas
schema.addProperty(schemaProp)           // Feld hinzufügen
schema.getProperty("key")               // Feld abrufen
schema.getProperties()                   // Alle Felder
schema.createInstance()                  // ObjectNode mit Defaults
schema.createInstance(Map<String,Object>) // ObjectNode mit Werten
```

#### `EnumDefinition`

```java
enumDef.getOptions()            // List<String> der erlaubten Werte
enumDef.isValid("value")        // true wenn Wert erlaubt
```

---

## 11. Vollständige Beispiele

### Beispiel 1: Konfigurationsdatei lesen

**config.sjna:**
```
appName: "SuperApp";
version: "2.0.0";
environment(dev, staging, prod): prod;
debug: false;

server: {
    host: "api.example.com";
    port: 443;
    ssl: true;
};

database: {
    host: "db.internal";
    port: 5432;
    name: "appdb";
};

allowedIps: ["192.168.1.1", "10.0.0.1"];
```

**Java-Code:**
```java
Document doc = SJNA.load("config.sjna");
SJNAConfig config = SJNA.asConfig(doc);

String appName  = config.getString("appName");           // "SuperApp"
String env      = config.getString("environment");       // "prod"
boolean debug   = config.getBoolean("debug");            // false
String host     = config.getString("server.host");       // "api.example.com"
int port        = config.getInt("server.port");          // 443
boolean ssl     = config.getBoolean("server.ssl");       // true
String dbName   = config.getString("database.name");     // "appdb"
List<String> ips = config.getList("allowedIps");        // ["192.168.1.1", "10.0.0.1"]
```

---

### Beispiel 2: Konfiguration programmatisch erstellen und speichern

```java
Document doc = SJNA.parse("");  // Leeres Dokument
SJNAConfig config = SJNA.asConfig(doc);

config.setString("appName", "MeineApp");
config.setString("version", "1.0.0");
config.setBoolean("debug", false);

config.addObject("server", Map.of(
    "host", "localhost",
    "port", 8080,
    "ssl", false
));

config.setList("tags", List.of("java", "config"));

// Speichern
SJNA.save(doc, "output.sjna");
System.out.println(SJNA.serialize(doc));
```

**Ausgabe:**
```
appName: "MeineApp";

version: "1.0.0";

debug: false;

server: {
    host: "localhost";
    port: 8080;
    ssl: false;
};

tags: ["java", "config"];
```

---

### Beispiel 3: Schema definieren und verwenden

```java
String schemaContent = """
    /: schema: Employee {
        name:;
        department:;
        level("junior", "senior", "lead"):;
        salary:;
    }
    """;

Document schemaDoc = SJNA.parse(schemaContent);

// Instanz erzeugen
ObjectNode emp = SJNA.createFromSchema(schemaDoc, "Employee", Map.of(
    "name", "Maria Müller",
    "department", "Engineering",
    "level", "senior",
    "salary", 85000
));

// Prüfen
PropertyNode nameNode = emp.getProperty("name");
System.out.println(nameNode.getValue().asString());  // "Maria Müller"

// Schema in Zieldokument einfügen
Document targetDoc = SJNA.parse("project: \"Phoenix\";");
SJNA.insertSchema(schemaDoc, "Employee", targetDoc, "lead", Map.of(
    "name", "Klaus Schmidt",
    "level", "lead"
));

System.out.println(SJNA.serialize(targetDoc));
```

---

### Beispiel 4: Validierung

```java
String content = """
    /: schema: Config {
        env("dev", "prod"):;
        logLevel("DEBUG", "INFO", "WARN", "ERROR"):;
    }

    env: prod;
    logLevel: "TRACE";
    """;

Document doc = SJNA.parse(content);

// Allgemein validieren
ValidationResult r1 = SJNA.validate(doc);
System.out.println(r1.isValid());  // false
r1.getErrors().forEach(e -> System.out.println(e.getMessage()));
// Invalid enum value for 'logLevel': TRACE. Allowed: [DEBUG, INFO, WARN, ERROR]

// Gegen Schema validieren
ValidationResult r2 = SJNA.validateAgainstSchema(doc, "Config");
System.out.println(r2.isValid());  // false
r2.getErrors().forEach(e -> System.out.println(e.getMessage()));
// Schema violation for 'logLevel': TRACE. Expected one of: [DEBUG, INFO, WARN, ERROR]
```

---

### Beispiel 5: Builder-Pattern

```java
Document doc = SJNA.parse("title: \"Mein Projekt\";");
SJNAConfig config = SJNA.asConfig(doc);

// Objekt mit Builder hinzufügen
new SJNAConfig.ObjectBuilder(config, "primaryServer", null)
    .withString("host", "server1.example.com")
    .withInt("port", 8443)
    .withBoolean("ssl", true)
    .withStringList("roles", List.of("web", "api"))
    .build();

// Schema-Builder (mit Schema-Validierung)
String content = """
    /: schema: Contact {
        email:;
        phone:;
        type("work", "personal"):;
    }
    title: "Kontakte";
    """;

Document contactDoc = SJNA.parse(content);
SJNAConfig cConfig = SJNA.asConfig(contactDoc);

new SJNAConfig.SchemaBuilder(cConfig, "Contact", "hauptkontakt")
    .withString("email", "info@example.com")
    .withString("phone", "+41 44 123 45 67")
    .with("type", "work")
    .build();

System.out.println(SJNA.serialize(contactDoc));
```

---

### Beispiel 6: Werte ändern und entfernen

```java
Document doc = SJNA.parse("""
    version: "1.0.0";
    debug: true;
    server: {
        host: "localhost";
        port: 8080;
    };
    """);

SJNAConfig config = SJNA.asConfig(doc);

// Ändern
config.setString("version", "2.0.0");
config.setBoolean("debug", false);
config.setInt("server.port", 9090);

// Entfernen
config.remove("debug");

// Prüfen
System.out.println(config.hasPath("debug"));          // false
System.out.println(config.getString("version"));      // "2.0.0"
System.out.println(config.getInt("server.port"));     // 9090
```

---

### Beispiel 7: Alle Objekte iterieren

```java
Document doc = SJNA.parse("""
    server1: { host: "s1.example.com"; port: 80; };
    server2: { host: "s2.example.com"; port: 443; };
    server3: { host: "s3.example.com"; port: 8080; };
    """);

SJNAConfig config = SJNA.asConfig(doc);

// Alle Objekte auf Root-Ebene
Map<String, ObjectNode> servers = config.getObjectsWithKeys(null);
for (Map.Entry<String, ObjectNode> entry : servers.entrySet()) {
    String name = entry.getKey();
    ObjectNode server = entry.getValue();
    String host = server.getProperty("host").getValue().asString();
    int port = server.getProperty("port").getValue().asNumber().intValue();
    System.out.println(name + " → " + host + ":" + port);
}
// server1 → s1.example.com:80
// server2 → s2.example.com:443
// server3 → s3.example.com:8080
```

---

## 12. Fehlerbehandlung

### ParseException

Wird bei Syntaxfehlern im SJNA-Text geworfen. Enthält Zeile und Spalte.

```java
try {
    Document doc = SJNA.parse("invalid content without semicolon");
} catch (ParseException e) {
    System.out.println(e.getMessage());
    // z.B. "Expected ':' at line 1, column 8"
}
```

### IOException

Wird von `SJNA.load()` und `SJNA.save()` geworfen, wenn die Datei nicht gelesen/geschrieben werden kann.

```java
try {
    Document doc = SJNA.load("nicht_vorhanden.sjna");
} catch (IOException e) {
    System.out.println("Datei nicht gefunden: " + e.getMessage());
} catch (ParseException e) {
    System.out.println("Syntaxfehler: " + e.getMessage());
}
```

### IllegalArgumentException

Wird von `SJNAConfig` geworfen, wenn:
- Ein Pfad nicht gefunden wird (`getString("nicht.da")`)
- Ein ungültiger Enum-Wert gesetzt wird
- Ein Schema nicht gefunden wird

```java
SJNAConfig config = SJNA.asConfig(doc);

// Sicher mit hasPath() prüfen
if (config.hasPath("optional.setting")) {
    String val = config.getString("optional.setting");
}

// Oder mit Fallback-Wert
String val = config.getString("optional.setting", "default");
```

---

## 13. API-Referenz auf einen Blick

### `SJNA` – Statische Methoden

| Methode | Beschreibung |
|---|---|
| `load(filePath)` | SJNA-Datei laden → `Document` |
| `parse(content)` | String parsen → `Document` |
| `save(doc, filePath)` | `Document` in Datei speichern |
| `serialize(doc)` | `Document` als String serialisieren |
| `validate(doc)` | `Document` validieren → `ValidationResult` |
| `validateAgainstSchema(doc, name)` | Gegen Schema validieren → `ValidationResult` |
| `asConfig(doc)` | `Document` als `SJNAConfig` |
| `createFromSchema(doc, name)` | Schema-Instanz ohne Werte |
| `createFromSchema(doc, name, values)` | Schema-Instanz mit Werten |
| `insertSchema(schemaDoc, name, targetDoc, key, values)` | Schema-Instanz als Property einfügen |
| `insertSchemaFlat(...)` | Schema-Felder direkt ins Root einfügen |

### `SJNAConfig` – Lese-Methoden (Pfad-Navigation)

| Methode | Rückgabe |
|---|---|
| `getString(path)` / `getString(path, default)` | `String` |
| `getNumber(path)` / `getNumber(path, default)` | `Number` |
| `getInt(path)` / `getInt(path, default)` | `Integer` |
| `getLong(path)` / `getLong(path, default)` | `Long` |
| `getDouble(path)` / `getDouble(path, default)` | `Double` |
| `getBoolean(path)` / `getBoolean(path, default)` | `Boolean` |
| `getObject(path)` | `ObjectNode` |
| `getList(path)` | `List<String>` |
| `getNumberList(path)` | `List<Number>` |
| `getIntList(path)` | `List<Integer>` |
| `getObjectList(path)` | `List<Map<String,Object>>` |
| `hasPath(path)` | `boolean` |
| `getKeys()` | `List<String>` (Root-Keys) |
| `getKeys(path)` | `List<String>` (Keys eines Unterobjekts) |
| `getAsMap(path)` | `Map<String,Object>` |
| `getAllAsMap()` | `Map<String,Object>` |
| `getObjects(path)` | `List<ObjectNode>` |
| `getObjectsWithKeys(path)` | `Map<String,ObjectNode>` |
| `getObjectsAsMap(path)` | `List<Map<String,Object>>` |
| `getDocument()` | `Document` |

### `SJNAConfig` – Schreib-Methoden

| Methode | Beschreibung |
|---|---|
| `setString(path, value)` | String setzen/überschreiben |
| `setInt(path, value)` | Integer setzen |
| `setLong(path, value)` | Long setzen |
| `setDouble(path, value)` | Double setzen |
| `setBoolean(path, value)` | Boolean setzen |
| `setEnum(path, options, value)` | Enum-Wert setzen (mit Validierung) |
| `setList(path, list)` | Liste setzen |
| `setObject(path, map)` | Objekt setzen |
| `remove(path)` | Property entfernen |
| `addObject(key, values)` | Objekt auf Root-Ebene hinzufügen |
| `addObjectAt(parentPath, key, values)` | Objekt unter Pfad hinzufügen |
| `addFromSchema(schemaName, key, values)` | Objekt aus Schema hinzufügen |

---

*Dokumentation erstellt für SJNA v1.1.2 – Gruppe `ch.AlexInf`*
