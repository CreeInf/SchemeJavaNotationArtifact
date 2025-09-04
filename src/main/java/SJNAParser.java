import java.util.*;
import java.util.regex.*;

public class SJNAParser {
    public Map<String, String> parse(List<String> lines) {
        Map<String, String> values = new HashMap<>();

        Pattern pattern = Pattern.compile("(\\w+)\\s*:\\s*\"?(.*?)\"?;");

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("//")) continue;

            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                values.put(matcher.group(1), matcher.group(2));
            }
        }
        return values;
    }
}
