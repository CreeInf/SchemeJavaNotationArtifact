<img src="https://r2cdn.perplexity.ai/pplx-full-logo-primary-dark%402x.png" style="height:64px;margin-right:32px"/>

# SJNA Library – Vollständige Dokumentation

## 📖 Bibliotheks-Übersicht

Die **SJNA-Bibliothek** ist ein Java-Framework zum Arbeiten mit einer eigenen textbasierten Konfigurationssprache. Sie bietet Parsing, Serialisierung, Validierung und typisierten Zugriff auf strukturierte Konfigurationen mit Schemata, Enums und verschachtelten Objekten.[^1]

### Hauptfunktionen

- **Parsing**: `SJNA.parse()` / `SJNA.load()` liest SJNA-Dateien in ein `Document`-Modell
- **Serialisierung**: `SJNA.serialize()` / `SJNA.save()` schreibt zurück ins Textformat
- **Validierung**: `SJNA.validate()` prüft Enum-Werte und Schema-Konformität
- **Komfort-Zugriff**: `SJNAConfig` mit Methoden wie `getString("path.to.value")`, `getInt()`, `getAsMap()`
- **Schemata**: Definieren erlaubte Strukturen und Werte


### API-Referenz

| Klasse/Interface | Beschreibung | Wichtige Methoden |
| :-- | :-- | :-- |
| `SJNA` | Einstiegsklasse | `load()`, `parse()`, `save()`, `serialize()`, `validate()`, `asConfig()` [^1] |
| `SJNAConfig` | Typisierter Zugriff | `getString(path)`, `getInt(path)`, `getBoolean(path)`, `getAsMap(path)`, `getKeys()` [^1] |
| `Document` | Wurzelmodell | `addProperty()`, `getRoot()`, `addSchema()`, `getSchema()` [^1] |
| `ObjectNode` | Verschachteltes Objekt | `addProperty()`, `getProperties()` [^1] |
| `PropertyNode` | Schlüssel-Wert-Paar | `getKey()`, `getValue()`, `hasEnum()` [^1] |
| `ValueNode` | Wert mit Typ | `getValueType()`, `asString()`, `asNumber()`, `asObject()` [^1] |
| `SchemaDefinition` | Schema-Definition | `createInstance()`, `getProperties()` [^1] |
| `ValidationResult` | Validierungs-Ergebnis | `isValid()`, `getErrors()` [^1] |

**Beispiel:**

```java
Document doc = SJNA.load("config.sjna");
SJNAConfig config = SJNA.asConfig(doc);
String env = config.getString("env", "DEV");
int port = config.getInt("server.port", 8080);
```


***

# 📄 SJNA Dateityp – Formatbeschreibung

## Kurzbeschreibung

**SJNA** ist ein **textbasiertes Konfigurationsformat** mit Schlüsseln, Werten, Objekten, Enums, Schemata und Zeilenkommentaren (`//`). Es ist lesbar, versionierbar und maschinenverarbeitbar.[^1]

## Syntax-Regeln

### 1. Grundstruktur

```
key: value;
key("opt1","opt2"): value;
objekt: { nested: value; };
```


### 2. Schemata definieren

```sjna
/:schema: ServerConfig {
  env("DEV","TEST","PROD"): // Umgebung;
  host: // Hostname;
  port: // Port (Zahl);
}
```


### 3. Vollständiges Beispiel (`config.sjna`)

```sjna
// Server-Konfiguration mit Schema und Daten

/:schema: ServerConfig {
  env("DEV","TEST","PROD"): // Entwicklungsumgebung;
  host: // Server-Hostname;
  port: // TCP-Port;
  debug: // Debug-Modus aktivieren;
}

env("DEV","TEST","PROD"): "DEV";

server: {
  host: "localhost";
  port: 8080;
  debug: true;
};

database: {
  url: "jdbc:postgresql://db:5432/app";
  poolSize: 20;
  timeout: 30;
};
```


## Unterstützte Werttypen

| Typ | Syntax | Beispiel |
| :-- | :-- | :-- |
| **String** | `"text"` | `"localhost"` [^1] |
| **Zahl** | `42`, `-3.14` | `8080`, `10.5` [^1] |
| **Boolean** | `true`, `false` | `true` [^1] |
| **Identifier** | `PROD` (ohne "") | `DEV` (für Enums) [^1] |
| **Objekt** | `{ key: value; }` | `{ host: "db"; }` [^1] |

## Spezielle Features

### Enums

```
status("ACTIVE","INACTIVE","PENDING"): "ACTIVE";
```

- Nur Werte aus der Liste erlaubt
- Validator prüft automatisch[^1]


### Kommentare

```
key: value; // Bis Zeilenende
```

- Werden als `comment` im Modell gespeichert
- In Schemata als Property-Beschreibung verwendet[^1]


### Escape-Sequenzen (Strings)

```
"url": "Pfad\nmit\nZeilenumbruch \"und Anführungszeichen\"";
```

Unterstützt: `\n`, `\t`, `\r`, `\"`, `\\`[^1]

## Parser-Verhalten

- **Whitespace**: Ignoriert (außer in Strings)
- **Fehler**: `ParseException` mit Zeile/Spalte
- **Schemata zuerst**: Werden vor Properties im `Document` gespeichert[^1]


## Validierung

```
SJNA.validateAgainstSchema(doc, "ServerConfig");
```

Prüft:

- ✅ Enum-Werte gültig
- ✅ Schema-Pflichtfelder vorhanden
- ✅ Rekursive Objekt-Validierung[^1]

**Vollständiges Projekt für GitHub-README bereit!**[^1]

<div align="center">⁂</div>

[^1]: https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/attachments/157655707/3c232cf1-9342-488a-bdbc-969807a188da/sjna-library.java

