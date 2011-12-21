package client;

import java.util.LinkedList;
import java.util.List;

/**
 * MessageTokenizer class
 * Objects of this class represent messages to be sent to the room. This object contains methods
 * for splitting the messages into invididual Tokens, apply formatting, and create emoticons
 * 
 * For emoticons, we require that one end of the emoticon (either the eyes or the mouth) must be at the ends
 * of the message, or both ends must be adjacent to at least one space.
 * 
 * For formatting, we require that the tags be either surrounded by spaces or other tags, or be at the beginning
+ * or end of a message 
 */
public class MessageTokenizer {
    
    private List<Token> tokens;
    private String message;
    
    // current states
    private boolean bold;
    private boolean italic;
    private boolean strike;
    private int currentIndex;
    
    // last start tag -- in case they don't end, need to revert
    private int lastStartBold;
    private int lastStartItalic;
    private int lastStartStrike;
    
    /**
     * Create a MessageTokenizer object
     * @param s - Requires s be a non-null String
     */
    public MessageTokenizer(String s) {
        this.tokens = new LinkedList<Token>();
        this.message = s.trim();
        this.bold = false;
        this.italic = false;
        this.strike = false;
        this.currentIndex = 0;
        this.lastStartBold = -1;
        this.lastStartItalic = -1;
        this.lastStartStrike = -1;
    }
    
    /**
     * Method to break the string into individual Tokens
     * A token is either a single Character or an emoticon. Tokens contain information about the formatting
     * of that character.
     * Tokens that correspond to the formatting markers and thus not displayed are set to invisible
     * Modifies - the state variables bold, italic, strike, currentIndex, lastStartBold, lastStartItalic, lastStartStrike
     * Modifies - the list of tokens as it iterates through the String
     * @return - the list of tokens after completion. This method returns an empty Token array given an empty String
     */
    public Token[] getTokens() {
        if (message.length() == 0) {
            return new Token[0];
        }
        while (currentIndex < message.length()) {
            char current = message.charAt(currentIndex);
            if (isTag(current)) {
                if (handleTag(current)) {
                    currentIndex++;
                    continue;
                }
            }
            if (isStartEmoticon(current)) {
                if (handleEmoticon(current)) {
                    currentIndex += 2;
                    continue;
                }
            }
            // just a normal character
            tokens.add(new Token(bold, italic, strike, Emoticon.NONE, current, true));
            currentIndex++;
        }
        
        // Now, if any of bold, italic, or strike is still set, it means there was a begin without an end
        // For example, only one asterisk. In this case, we make that tag visible (it's not actually formatting)
        // and undo the effects of that tag down the String
        if (bold) {
            tokens.get(lastStartBold).setVisible();
            for (int i = lastStartBold + 1; i < tokens.size(); i++) {
                tokens.get(i).removeBold();
            }
        }
        if (italic) {
            tokens.get(lastStartItalic).setVisible();
            for (int i = lastStartItalic + 1; i < tokens.size(); i++) {
                tokens.get(i).removeItalic();
            }        
        }
        if (strike) {
            tokens.get(lastStartStrike).setVisible();
            for (int i = lastStartStrike + 1; i < tokens.size(); i++) {
                tokens.get(i).removeStrike();
            }        
        }
        return tokens.toArray(new Token[0]);
    }
    
    /**
     * Method to call when the character being read could potentially start an emoticon
     * @param current - the character currently being read. Requires it be a valid start emoticon
     * @return - true if there is an emoticon starting at current, false otherwise
     */
    private boolean handleEmoticon(char current) {
        if (message.length() < currentIndex + 2) { // first char at start -- make sure we have enough characters
            return false;
        }
        if ((currentIndex == message.length() - 2) ||   // end of msg
                (currentIndex == 0) || // start of msg
                // middle of message, obviously not the start or second to last
                // requires that the emoticon is surrounded by spaces
                (currentIndex != 0 && message.charAt(currentIndex - 1) == ' ' &&
                    currentIndex <= message.length()-2 && message.charAt(currentIndex+2) == ' ')) {
            
            char next = message.charAt(currentIndex+1);
            if (current == ':') {
                if (next == ')') {
                    tokens.add(new Token(Emoticon.COLON_CLOSE));
                    return true;
                }
                if (next == 'D') {
                    tokens.add(new Token(Emoticon.COLON_D));
                    return true;
                }
                if (next == '(') {
                    tokens.add(new Token(Emoticon.COLON_OPEN));
                    return true;
                }
                if (next == 'P') {
                    tokens.add(new Token(Emoticon.COLON_P));
                    return true;
                }
                if (next == 'O') {
                    tokens.add(new Token(Emoticon.COLON_O));
                    return true;
                }
            }
            else if (current == ';' && next == ')') {
                tokens.add(new Token(Emoticon.SEMI_CLOSE));
                return true;
                
            }
            else if (current == '8' && next == ')') {
                tokens.add(new Token(Emoticon.EIGHT_CLOSE));
                return true;
            }
        }
        return false;
    }
    
    /**
     * Method called when the current character being read could be a formatting marker
     * @param current - the current character. Requires it be a valid formatting marker
     * @return  true if there exists a formatting tag in a valid location, false otherwise
     */
    private boolean handleTag(char current) {
        
        /*
         *  A tag may start at the beginning, if it is preceded by another tag or a space and is not 
         *  at the end, AND the attribute it currently refers to is false
         */
        boolean startCase = (currentIndex == 0 || isTag(message.charAt(currentIndex - 1)) || 
                                message.charAt(currentIndex-1) == ' ') && currentIndex != message.length()-1;
        
        /*
         * A tag may end at the end of the message, when it is followed by another tag or a space, 
         * AND the attribute it currently refers to is true.
         */
        boolean endCase = currentIndex == message.length() - 1 || 
                            isTag(message.charAt(currentIndex + 1)) || 
                            message.charAt(currentIndex + 1) == ' ';
        
        /*
         * The same tag adjacent to each other (ie **) does not format anything and is not
         * considered to be a formatting tag.
         */
        if (startCase && current == '*' && bold == false && message.charAt(currentIndex+1) != '*') {
            tokens.add(new Token(bold, italic, strike, Emoticon.NONE, '*', false));
            bold = true;
            lastStartBold = tokens.size() - 1;
            return true;
        }
        
        if (startCase && current == '_' && italic == false && message.charAt(currentIndex+1) != '_') {
            tokens.add(new Token(bold, italic, strike, Emoticon.NONE, '_', false));
            italic = true;
            lastStartItalic = tokens.size() - 1;
            return true;
        }
        
        if (startCase && current == '-' && strike == false && message.charAt(currentIndex+1) != '-') {
            tokens.add(new Token(bold, italic, strike, Emoticon.NONE, '-', false));
            strike = true;
            lastStartStrike = tokens.size() - 1;
            return true;
        }
        
        if (endCase && current == '*' && bold == true) {
            tokens.add(new Token(bold, italic, strike, Emoticon.NONE, '*', false));
            bold = false;
            return true;
        }
        
        if (endCase && current == '_' && italic == true) {
            tokens.add(new Token(bold, italic, strike, Emoticon.NONE, '_', false));
            italic = false;
            return true;
        }
        
        if (endCase && current == '-' && strike == true) {
            tokens.add(new Token(bold, italic, strike, Emoticon.NONE, '-', false));
            strike = false;
            return true;
        }
        return false;
    }
    
    /**
     * Method to determine whether a character could start a formatting section
     * @param c - the character
     * @return true if the character starts a formatting tag (*, -, or _), false otherwise
     */
    private boolean isTag(char c) {
        return c=='*' || c=='-' || c=='_';
    }
    
    /**
     * Method to determine whether a character could start an emoticon
     * @param c - the character
     * @return - true if the character starts an emoticon (:, ;, or 8), false otherwise
     */
    private boolean isStartEmoticon(char c) {
        return c==':' || c==';' || c=='8';
    }
}
