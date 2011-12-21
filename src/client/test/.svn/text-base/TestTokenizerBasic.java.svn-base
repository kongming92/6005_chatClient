package client.test;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import client.MessageTokenizer;
import client.Token;

/**
 * Test basic tokenization of characters and spaces
 * Testing strategy:
 * 1. Test empty string
 * 2. Test space for trimming (should go to empty)
 * 3. Test single character
 * 4. Test multiple characters no spaces
 * 5. Test multiple characters with spaces
 * 6. Test multiple characters with spaces in between (all spaces should show up)
 * 7. Test characters in between spaces (spaces on ends should be trimmed)
 */
public class TestTokenizerBasic {
    
    @Test
    public void testEmpty() {
        String s = "";
        assertArrayEquals(new Token[0], new MessageTokenizer(s).getTokens()); 
    }
    
    @Test
    public void testSpace() {
        String s = " ";
        assertArrayEquals(new Token[0], new MessageTokenizer(s).getTokens());
    }
    
    @Test
    public void testSingleChar() {
        String s = "a";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void testManyChars() {
        String s = "ac";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)", "Token(c)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void testManyCharsWithSpace() {
        String s = "a c";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)", "Token( )", "Token(c)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void testManySpaces() {
        String s = "a  c";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)", "Token( )", "Token( )", "Token(c)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }
    
    @Test
    public void testLeadingTrailingSpaces() {
        String s = "  ac  ";
        Token[] tokens = new MessageTokenizer(s).getTokens();
        String[] expected = {"Token(a)", "Token(c)"};
        String[] result = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            result[i] = tokens[i].toString();
        }
        assertArrayEquals(expected, result);
    }   
}
