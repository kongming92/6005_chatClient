package client;

/**
 * Class that represents individual Token objects in the chat message.
 * A Token can be either a single character of text or an emoticon. 
 * Text can be formatted with bold, italic, strikethrough. Emoticons cannot be formatted. 
 * If the token is an emoticon (not Emoticon.NONE), the text must be ' ' (a single space)
 * 
 * The boolean show flag is useful for tokenizing and the parser.
 */
public class Token {
    
    private boolean bold;
    private boolean italic;
    private boolean strike;
    private Emoticon emoticon;
    private Character text;
    private boolean show;
    
    /**
     * Create a Token object
     * @param bold
     * @param italic
     * @param strike
     * @param emoticon - requires a valid member of Emoticon. In conjunction with text, must satisfy the rep invariant given below
     * @param text - requires text not null
     * @param show
     */
    public Token(boolean bold, boolean italic, boolean strike, Emoticon emoticon, Character text, boolean show) {
        // rep invariant - if emoticon != Emoticon.NONE, text == ' ' (a single space)
        this.bold = bold;
        this.italic = italic;
        this.strike = strike;
        this.emoticon = emoticon;
        if (emoticon != Emoticon.NONE) {
            this.text = ' ';
        }
        else {
            this.text = text;
        }
        this.show = show;
    }
    
    /**
     * Create a Token object, used specifically for Emoticons.
     * Ensures that all attributes are false, text is ' '
     * @param emoticon
     */
    public Token(Emoticon emoticon) {
        this(false, false, false, emoticon, ' ', true);
    }
    
    /**
     * Outputs the style of the Token. If a character, the style is a String containing
     * "bold", "italic", and "strikethrough" if the properties are true, arranged in alphabetical order
     * If emoticon, the style is the text representation of the emoticon.
     * The styles are used in displaying formatting and emoticons in the GUI
     * @return - style of the Token as a String
     */
    public String getStyle() {
        if (emoticon != Emoticon.NONE) {
            return emoticon.getStyle();
        }
        else {
            String s = "";
            if (bold) {
                s += "bold";
            }
            if (italic) {
                s += "italic";
            }
            if (strike) {
                s += "strikethrough";
            }
            if (s.length() == 0) {
                s = "regular";
            }
            return s;
        }
    }
    
    /**
     * @return - the text of the token (the text character, or ' ' if emoticon)
     */
    public String getText() {
        return text.toString();
    }
    
    @Override
    public String toString() {
        String s = "Token(";
        if (!show) {
            s += "invisible ";
        }
        if (emoticon != Emoticon.NONE) {
            return s + emoticon.getStyle() + " EMOTICON)";
        }
        s += text + " ";
        if (bold) {
            s += "bold ";
        }
        if (italic) {
            s += "italic ";
        }
        if (strike) {
            s += "strike ";
        }
        return s.substring(0, s.length()-1) + ")";
    }
    
    /*
     * The following three methods set the respective attributes to false. This is useful
     * in the tokenizer, when we must go back and undo formatting if one of the formatting markers
     * is not actually used for formatting (for example, a start bold '*' without an ending '*')
     */
    public void removeBold() {
        bold = false;
    }
    
    public void removeItalic() {
        italic = false;
    }
    
    public void removeStrike() {
        strike = false;
    }
    
    /*
     * Sets the token to be visible. Used in the same case as the above three methods.
     * In those cases, the formatting token is actually a text token, so we must make it visible
     * in the GUI.
     */
    public void setVisible() {
        show = true;
    }
    
    public boolean isBold() {
        return bold;
    }
    
    public boolean isItalic() {
        return italic;
    }
    
    public boolean isStrike() {
        return strike;
    }
    
    public boolean isVisible() {
        return show;
    }
}
