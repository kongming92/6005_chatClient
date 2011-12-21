package client.test;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import client.MessageTokenizer;
import client.Token;

/**
 * Tests that bold, italic, and strikethrough are formatted properly
 * Conforms to spec of a starting tag must be preceded by a tag or space, and an ending tag must be
 * followed by a tag or space. The same tag that is adjacent (like **) has no effect and the tags are
 * treated like text. 
 * The only exception is at the beginning or end of a message, where the tags do not need to be preceded
 * or followed by spaces.
 * Also tests that the tags are "balanced" -- for example a single * will be treated as text and not bold
 * 
 * Testing strategy:
 * 1-3. Test each formatting on a single character
 * 4. Test formatting on an entire word (many chars)
 * 5. Test tags not padded by spaces - tags should be treated as text
 * 6. Test tags padded by spaces - tags are treated as formatting
 * 7. Test adjacent tags - tags are treated as text
 * 8. Test start tag with no end tag - tag treated as text
 * 9. Test end tag no start tag - tag treated as text
 * 10. Test single tag in the middle - tag treated as text
 * 11. Test nested in order - text formatted according to all the tags
 * 12. Test not nested in order - text should still be formatted according to the tags enclosing it.
 */
public class TestTokenizerFormatted {

    @Test
    public void boldSingleChar() {
        String s = "*a*";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(invisible *)", "Token(a bold)", "Token(invisible * bold)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void italicSingleChar() {
        String s = "_a_";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(invisible _)", "Token(a italic)", "Token(invisible _ italic)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void strikeSingleChar() {
        String s = "-a-";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(invisible -)", "Token(a strike)", "Token(invisible - strike)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void formatWord() {
        String s = "*hi*";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(invisible *)", "Token(h bold)", "Token(i bold)", "Token(invisible * bold)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void tagsInMiddleWithoutSpaces() {
        String s = "a*b*c";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)", "Token(*)", "Token(b)", "Token(*)", "Token(c)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result); 
    }
    
    @Test
    public void tagsInMiddleWithSpaces() {
        String s = "a *b* c";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)", "Token( )", "Token(invisible *)", "Token(b bold)", "Token(invisible * bold)", "Token( )", "Token(c)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result); 
    }
    
    @Test
    public void adjacentTags() {
        String s = "--";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(-)", "Token(-)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result); 
    }
    
    @Test
    public void startNoEndTag() {
        String s = "_ab";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(_)", "Token(a)", "Token(b)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result); 
    }
    
    @Test
    public void endNoStartTag() {
        String s = "ab_";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)", "Token(b)", "Token(_)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result); 
    }
    
    @Test
    public void singleTagMiddle() {
        String s = "a-c";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)", "Token(-)", "Token(c)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result); 
    }
    
    @Test
    public void nestedInOrder() {
        String s = "*_-abc-_*";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(invisible *)", "Token(invisible _ bold)", "Token(invisible - bold italic)",
                "Token(a bold italic strike)", "Token(b bold italic strike)", "Token(c bold italic strike)", 
                "Token(invisible - bold italic strike)", "Token(invisible _ bold italic)", "Token(invisible * bold)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result); 
    }
    
    @Test
    public void notNestedInOrder() {
        String s = "-_a- b_";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(invisible -)", "Token(invisible _ strike)", "Token(a italic strike)",
                "Token(invisible - italic strike)", "Token(  italic)", "Token(b italic)", "Token(invisible _ italic)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result); 
    }
}

