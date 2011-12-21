package client.test;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import client.MessageTokenizer;
import client.Token;

/** 
 * Tests for proper tokenization of emoticons, make sure that tokenization conforms to the spec
 * of there must be spaces before and after, or the emoticon must appear at the beginning or end
 * 
 * Testing strategy:
 * 1-7: Test that the emoticons individually work
 * 8. Test that the emoticons show up when in the middle, but with spaces on the side
 * 9. Test that the emoticons do not show up when in between characters, no spaces (such as d:)d)
 * 10. Test that the emoticons show up at the beginning even with no space
 * 11. Test that the emoticons show up at the end even with no space
 *
 */
public class TestTokenizerEmoticon {
    
    @Test
    public void testColonCloseSmiley() {
        String s = ":)";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(:) EMOTICON)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void testColonOpenFrown() {
        String s = ":(";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(:( EMOTICON)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void testColonD() {
        String s = ":D";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(:D EMOTICON)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void testColonO() {
        String s = ":O";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(:O EMOTICON)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void testColonP() {
        String s = ":P";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(:P EMOTICON)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void testSemiClose() {
        String s = ";)";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(;) EMOTICON)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void test8Close() {
        String s = "8)";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(8) EMOTICON)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void inBetweenWithSpaces() {
        String s = "a :) a";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)", "Token( )", "Token(:) EMOTICON)", "Token( )", "Token(a)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void inBetweenNoSpace() {
        String s = "a:Da";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)", "Token(:)", "Token(D)", "Token(a)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void emoticonAtStart() {
        String s = "8)a";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(8) EMOTICON)", "Token(a)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void emoticonAtEnd() {
        String s = "ad:(";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)", "Token(d)", "Token(:( EMOTICON)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }

}
