package client;

import java.util.LinkedList;
import java.util.List;

/**
 * Parse the tokens into arrays of text and styles
 * Returns String arrays of text (single character strings) and styles
 * The styles are either the text representation for emoticons, "regular", or 
 * "bold", "italic", "strikethrough" in some combination, without space separation, 
 * in that order.
 * Tokens with visible=false are ignored.
 * 
 * Methods of this class should all be called in a static context. This is an abstract
 * class, do not create instances of this classes.
 *
 */
public abstract class MessageParser {
    
    /**
     * Get the array of text from the Tokens
     * @param tokens - requires array of Tokens non null. On null input returns null
     * @return - a String array corresponding to the text value of all visible tokens
     */
    public static String[] getTextArray(Token[] tokens) {
        if (tokens == null) {
            return null;
        }
        List<String> result = new LinkedList<String>();
        for (Token t : tokens) {
            if (t.isVisible()) {
                result.add(t.getText());
            }
        }
        return result.toArray(new String[0]);
    }
    
    /**
     * Get the array of style from the Tokens
     * @param tokens - requires array of Tokens non null. On null input returns null
     * @return - a String array corresponding to the style value of all visible tokens
     */
    public static String[] getStyleArray(Token[] tokens) {
        if (tokens == null) {
            return null;
        }
        List<String> result = new LinkedList<String>();
        for (Token t : tokens) {
            if (t.isVisible()) {
                result.add(t.getStyle());
            }
        }
        return result.toArray(new String[0]);
    }
}
