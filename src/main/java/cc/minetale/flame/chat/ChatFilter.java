package cc.minetale.flame.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ChatFilter {
    private static List<String> blockedWords;
    private static Map<String, List<String>> replaceableCharacters;

    public static final Pattern WEBSITE_PATTERN = Pattern.compile("((?:(?:https?)://)?[\\w-_.]{2,})\\.([a-zA-Z]{2,3}(?:/\\S+)?)");
    public static final Pattern IP_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    public static void init() {
        blockedWords = Arrays.asList("nigger", "nigga", "beaner", "spik", "faggot", "faggots", "kike", "coon", "coonass", "cracker", "gringo", "gypsy");
        replaceableCharacters = new LinkedHashMap<>();

        replaceableCharacters.put("a", Arrays.asList("4", "@", "/\\", "/-\\", "Д"));
        replaceableCharacters.put("b", Arrays.asList("I3", "|3", "13", "!3", "(3", ")3", "6", "ß"));
        replaceableCharacters.put("c", Arrays.asList("[", "{", "<", "(", "¢", "©"));
        replaceableCharacters.put("d", Arrays.asList("|)", "c|"));
        replaceableCharacters.put("e", Arrays.asList("3", "€", "ë", "£"));
        replaceableCharacters.put("f", Arrays.asList("ƒ"));
        replaceableCharacters.put("g", Arrays.asList("&"));
        replaceableCharacters.put("h", Arrays.asList("#"));
        replaceableCharacters.put("i", Arrays.asList("!"));
        //replaceableCharacters.put('j', Arrays.asList(''));
        //replaceableCharacters.put('k', Arrays.asList(''));
        replaceableCharacters.put("m", Arrays.asList("|V|", "/\\/\\", "/V\\"));
        replaceableCharacters.put("n", Arrays.asList("|\\|", "/\\/", "|V", "/V", "И", "ท"));
        replaceableCharacters.put("o", Arrays.asList("0", "()", "\\[]", "Ø"));
        replaceableCharacters.put("p", Arrays.asList("|>"));
        //replaceableCharacters.put("q", Arrays.asList(''));
        replaceableCharacters.put("r", Arrays.asList("I2", "12", "®", "Я"));
        replaceableCharacters.put("s", Arrays.asList("$", "5"));
        replaceableCharacters.put("u", Arrays.asList("(_)", "|_|", "µ", "บ"));
        replaceableCharacters.put("v", Arrays.asList("\\/", "|/", "\\|"));
        replaceableCharacters.put("w", Arrays.asList("\\/\\/", "VV", "\\N", "\\^/", "\\V/", "\\X/", "พ", "Ш", "Щ"));
        replaceableCharacters.put("x", Arrays.asList("><", "×", "Ж"));
        replaceableCharacters.put("y", Arrays.asList("Ч"));
        replaceableCharacters.put("z", Arrays.asList("2", "7_"));
        replaceableCharacters.put("t", Arrays.asList("7", "†"));
        replaceableCharacters.put("l", Arrays.asList("1", "|_", "|"));
    }

    public static boolean isSafe(String originalText) {
        String text = originalText.replaceAll("\\s", "").toLowerCase();

        for (Map.Entry<String, List<String>> ent : replaceableCharacters.entrySet()) {
            for (String replaceableChar : ent.getValue()) {
                text = text.replace(replaceableChar, ent.getKey());
            }
        }

        for (String blockedWord : blockedWords) {
            if (text.contains(blockedWord)) {
                return false;
            }
        }

        return !WEBSITE_PATTERN.matcher(text).find()|| !IP_PATTERN.matcher(text).find();
    }
}
