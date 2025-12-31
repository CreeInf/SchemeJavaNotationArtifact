

<h1>SJNA Library – Übersicht</h1>
<p>
Die SJNA‑Bibliothek ist ein kleines Framework zum Parsen, Validieren, Serialisieren und komfortablen Auslesen einer eigenen Konfigurationssprache auf Basis von Schlüsseln, Werten, Objekten und Schemata.[1]
</p>

<h2>Pakete und Hauptkomponenten</h2>
<ul>
  <li><code>io.sjna</code>: Einstiegsklasse <code>SJNA</code>, Serializer <code>SJNASerializer</code> und Konfigurations-Wrapper <code>SJNAConfig</code>.</li>[1]
  <li><code>io.sjna.model</code>: Datenmodell mit <code>Document</code>, <code>ObjectNode</code>, <code>PropertyNode</code>, <code>ValueNode</code>, <code>SchemaDefinition</code>, <code>SchemaProperty</code>, <code>EnumDefinition</code> und <code>NodeType</code>.</li>[1]
  <li><code>io.sjna.parser</code>: Parser <code>SJNAParser</code> und <code>ParseException</code> für das Einlesen des Textformats in ein <code>Document</code>.</li>[1]
  <li><code>io.sjna.validation</code>: Validierung mit <code>Validator</code>, <code>ValidationResult</code> und <code>ValidationError</code>.</li>[1]
</ul>

<h2>Hauptklasse SJNA</h2>
<ul>
  <li><code>load(String filePath)</code>: Liest eine Konfigurationsdatei von der Festplatte und gibt ein <code>Document</code> zurück.</li>[1]
  <li><code>parse(String content)</code>: Parst einen String mit Inhalt in ein <code>Document</code>.</li>[1]
  <li><code>save(Document doc, String filePath)</code>: Serialisiert ein <code>Document</code> in das SJNA‑Textformat und speichert es.</li>[1]
  <li><code>serialize(Document doc)</code>: Gibt den seriellen Text des Dokuments zurück.</li>[1]
  <li><code>validate(Document doc)</code> / <code>validateAgainstSchema(Document doc, String schemaName)</code>: Führt Validierung ohne bzw. gegen ein benanntes Schema durch.</li>[1]
  <li><code>asConfig(Document doc)</code>: Erzeugt ein <code>SJNAConfig</code>‑Objekt für komfortables Auslesen.</li>[1]
</ul>

<h2>SJNAConfig – Zugriff auf Konfiguration</h2>
<ul>
  <li>Pfadsyntax: <code>"rootKey.nestedKey.etc"</code>, wobei jeder Teil einem Eigenschaftsnamen entspricht.</li>[1]
  <li>Methoden:
    <ul>
      <li><code>getString(path)</code>, <code>getString(path, default)</code></li>
      <li><code>getNumber(path)</code>, <code>getInt</code>, <code>getLong</code>, <code>getDouble</code> inkl. Default‑Varianten</li>
      <li><code>getBoolean(path)</code> und Default‑Variante</li>
      <li><code>getObject(path)</code>: Liefert <code>ObjectNode</code> unter einem Pfad.</li>[1]
      <li><code>getAsMap(path)</code>: Wandelt ein <code>ObjectNode</code> in eine <code>Map&lt;String,Object&gt;</code> um.</li>[1]
      <li><code>getAllAsMap()</code>: Gibt das komplette Dokument als Map zurück.</li>[1]
      <li><code>getKeys()</code> und <code>getKeys(path)</code>: Listet Schlüssel auf Root‑Ebene oder innerhalb eines Objekts.</li>[1]
      <li><code>getObjects(path)</code>, <code>getObjectsAsMap(path)</code>, <code>getObjectsWithKeys(path)</code>: Arbeit mit mehreren Objekt‑Kindern.</li>[1]
    </ul>
  </li>
</ul>

<h2>Modellstruktur</h2>
<ul>
  <li><code>Document</code>: Wurzel, enthält <em>Root‑Properties</em> und benannte <code>SchemaDefinition</code>‑Instanzen.</li>[1]
  <li><code>Node</code>: Basisinterface mit <code>getType()</code>, <code>getComment()</code>, <code>setComment()</code>.</li>[1]
  <li><code>NodeType</code>: Aufzählung <code>DOCUMENT</code>, <code>OBJECT</code>, <code>PROPERTY</code>, <code>VALUE</code>, <code>SCHEMA</code>.</li>[1]
  <li><code>ObjectNode</code>: Sammlung von <code>PropertyNode</code>‑Kindern (verschachtelte Objekte).</li>[1]
  <li><code>PropertyNode</code>: Repräsentiert ein Schlüssel‑Wert‑Paar mit optionalem Enum.</li>[1]
  <li><code>ValueNode</code>: Enthält konkreten Wert und Typ (<code>STRING</code>, <code>NUMBER</code>, <code>BOOLEAN</code>, <code>IDENTIFIER</code>, <code>OBJECT</code>).</li>[1]
  <li><code>EnumDefinition</code>: Hält eine Liste erlaubter Werte und prüft deren Gültigkeit.</li>[1]
  <li><code>SchemaDefinition</code> / <code>SchemaProperty</code>: Beschreiben das Schema eines Objekts mit optionalen Enums und Beschreibungen.</li>[1]
</ul>

<h2>Parser und Serializer</h2>
<ul>
  <li><code>SJNAParser</code>: Liest Text, baut <code>Document</code>, erkennt Schemata (<code>/:schema: Name { ... }</code>) und Root‑Properties.</li>[1]
  <li>Unterstützt:
    <ul>
      <li>Strings mit Escape‑Sequenzen (<code>\n</code>, <code>\t</code>, <code>\r</code>, <code>\"</code>, <code>\\</code>).</li>[1]
      <li>Zahlen (Integer/Double), Booleans (<code>true</code>/<code>false</code>), Identifier, Objekte in <code>{ ... }</code>.</li>[1]
      <li>Enum‑Definitionen in runden Klammern hinter dem Key.</li>[1]
      <li>Kommentare mit <code>//</code> bis zum Zeilenende.</li>[1]
    </ul>
  </li>
  <li><code>SJNASerializer</code>: Wandelt ein <code>Document</code> zurück in Text, schreibt zuerst Schemata, dann Root‑Properties, mit korrektem Einrücken und Escaping.</li>[1]
</ul>

<h2>Validierung</h2>
<ul>
  <li><code>Validator</code>:
    <ul>
      <li><code>validate(Document doc)</code>: Prüft insbesondere Enum‑Werte und verschachtelte Objekte.</li>[1]
      <li><code>validateAgainstSchema(Document doc, String schemaName)</code>: Prüft Dokument gegen ein benanntes Schema (Pflicht‑Properties, Enum‑Werte).</li>[1]
    </ul>
  </li>
  <li><code>ValidationResult</code>: Enthält Liste von <code>ValidationError</code> und Flag <code>isValid()</code>.</li>[1]
  <li><code>ValidationError</code>: Hält eine Fehlermeldung und überschreibt <code>toString()</code>.</li>[1]
</ul>

<hr>

<h1>SJNA Dateiformat – Beschreibung</h1>
<p>
Das SJNA‑Dateiformat ist eine textbasierte, schemageführte Konfigurationssprache mit Unterstützung für Schemata, Enums, verschachtelte Objekte und Kommentare.[1]
</p>

<h2>Grundaufbau</h2>
<ul>
  <li>Eine Datei besteht aus beliebig vielen Schema‑Definitionen und Root‑Properties.</li>[1]
  <li>Jede Property endet mit einem Semikolon <code>;</code> und hat die Form <code>key[:enum]: value;</code> (Enum optional).</li>[1]
  <li>Whitespace (Leerzeichen, Zeilenumbrüche) ist weitgehend frei platzierbar.</li>[1]
</ul>

<h2>Schemata definieren</h2>
<ul>
  <li>Syntax für ein Schema:
    <pre>/:schema: SchemaName {
  key1("Opt1","Opt2"): // Beschreibung;
  key2: // Beschreibung;
}</pre>
[1]
  </li>
  <li><code>SchemaName</code> ist ein Identifier, der das Schema im <code>Document</code> eindeutig benennt.</li>[1]
  <li>Jede <code>SchemaProperty</code> kann:
    <ul>
      <li>einen Schlüssel (<code>key</code>)</li>
      <li>optional eine Enum‑Definition in <code>( ... )</code></li>
      <li>optional eine Beschreibung als Kommentar hinter <code>//</code></li>
    </ul>
    enthalten.[1]
  </li>
</ul>

<h2>Eigenschaften (Properties)</h2>
<ul>
  <li>Schlüssel können:
    <ul>
      <li>als einfacher Identifier geschrieben werden, z.&nbsp;B. <code>port</code></li>
      <li>oder in Anführungszeichen, z.&nbsp;B. <code>"server-name"</code>, wenn Sonderzeichen verwendet werden.</li>[1]
    </ul>
  </li>
  <li>Optionale Enum‑Liste: <code>key("DEV","PROD")</code> oder mit nicht‑String‑Identifiern.</li>[1]
  <li>Der Wert folgt nach einem Doppelpunkt <code>:</code> und kann ein einfacher Wert oder ein Objekt sein.</li>[1]
</ul>

<h2>Werttypen</h2>
<ul>
  <li>String:
    <ul>
      <li><code>"Text mit \n und \"Escapes\""</code></li>
      <li>Unterstützt die Escape‑Sequenzen \n, \t, \r, \", \\.</li>
    </ul>
[1]
  </li>
  <li>Zahl:
    <ul>
      <li>Ganzzahlen und Dezimalzahlen, optional mit Minus, z.&nbsp;B. <code>42</code> oder <code>-3.14</code>.</li>
    </ul>
[1]
  </li>
  <li>Boolean:
    <ul>
      <li><code>true</code> oder <code>false</code>.</li>
    </ul>
[1]
  </li>
  <li>Identifier:
    <ul>
      <li>Wird als symbolischer Wert interpretiert, z.&nbsp;B. <code>PROD</code> oder <code>localhost</code> ohne Anführungszeichen.</li>
    </ul>
[1]
  </li>
  <li>Objekt:
    <ul>
      <li>Syntax: <code>{ key: value; key2: value2; }</code> mit beliebig vielen Properties.</li>[1]
    </ul>
  </li>
</ul>

<h2>Objekte und Verschachtelung</h2>
<ul>
  <li>Objekte erlauben verschachtelte Strukturen, z.&nbsp;B.:
    <pre>server: {
  host: "localhost";
  port: 8080;
};</pre>
[1]
  </li>
  <li>Jede Eigenschaft innerhalb eines Objekts folgt den gleichen Regeln wie auf Root‑Ebene und kann wiederum Enum‑Definitionen besitzen.</li>[1]
</ul>

<h2>Enums im Dateiformat</h2>
<ul>
  <li>Ein Enum wird direkt nach dem Schlüssel in runden Klammern angegeben:
    <pre>env("DEV","TEST","PROD"): "DEV";</pre>
[1]
  </li>
  <li>Der Parser erlaubt Strings oder Identifier in Enum‑Listen, und der Validator prüft, ob der gesetzte Wert in dieser Liste enthalten ist.</li>[1]
  <li>Bei Schema‑Properties definiert das Enum die erlaubten Werte für alle Instanzen dieses Schlüssels.</li>[1]
</ul>

<h2>Kommentare</h2>
<ul>
  <li>Kommentare beginnen mit <code>//</code> und gehen bis zum Zeilenende.</li>[1]
  <li>Im Schema werden Kommentare als Beschreibungen (<code>description</code>) einer <code>SchemaProperty</code> gespeichert, im Modell als <code>comment</code> in <code>Node</code>.</li>[1]
</ul>

<h2>Fehlerbehandlung</h2>
<ul>
  <li>Syntaxfehler im Dateiformat führen zu einer <code>ParseException</code> mit Zeilen‑ und Spalteninformation.</li>[1]
  <li>Schema‑Verstöße (z.&nbsp;B. ungültige Enum‑Werte oder fehlende Pflicht‑Properties) werden als <code>ValidationError</code> im <code>ValidationResult</code> gesammelt.</li>[1]
</ul>


[1](https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/attachments/157655707/3c232cf1-9342-488a-bdbc-969807a188da/sjna-library.java)
