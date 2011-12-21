package user.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Test;

import server.ChatServer;
import user.User;

/**
 * Testing logging in and resulting server to client messages
 * 
 * Testing strategy:
 * 1. Test a simple login of a single user, should get welcome message back.
 * 2. Test with other users present. Should get welcome message back.
 * 3. Test login with already taken name. Should get error message
 * 4. Test try to send login command while already logged in. Should get error message
 * 5. Test login, logout, then login again. Should get two welcome messages.
 * 
 * NOTE: In the first test we clear the users file by creating a PrintWriter to write to the file.
 * We flag the debug field as true in the creation of a user, to keep the user from
 * automatically logging out when our input stream ends. This creates an infinite loop in the 
 * user, so we place a one-second delay to allow the commands to process.
 * We use the unencrypted streams to test the server. In the actual program with the client/GUI, the 
 * streams are encrypted.
 */
public class LoginTest {

    @Test
    public void singleLogin() throws IOException, InterruptedException {
        PrintWriter clear = new PrintWriter(new FileOutputStream(new File("src/server/userfile")));
        clear.close();
        ChatServer server = new ChatServer(4445);
        String inp = "register cliu 123\nlogin cliu 123\n";
        ByteArrayInputStream in = new ByteArrayInputStream(inp.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, false, false));
        t.start();
        t.join();
        server.kill();
        assertEquals("connection successful\nregisterSuccess cliu\nwelcome cliu\n", out.toString());
    }
    
    
    @Test
    public void multipleUserLogin() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        String user1Inp = "login cliu 123\n";
        String user2Inp = "register jchan 123\nlogin jchan 123\n";
        ByteArrayInputStream in = new ByteArrayInputStream(user1Inp.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in2 = new ByteArrayInputStream(user2Inp.getBytes());
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, false, false));
        Thread t2 = new Thread(new User(server, in2, out2, false, false));
        t.start();
        t.join();
        t2.start();
        t2.join();
        server.kill();
        assertEquals(true, out.toString().contains("welcome cliu\n"));
        assertEquals(true, out2.toString().contains("welcome jchan\n"));
    }
    
    @Test
    public void loginSameName() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        String inp = "login cliu 123\n";
        ByteArrayInputStream in = new ByteArrayInputStream(inp.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        t.start();
        t.join(1000); // debug set to true, thread will loop infinitely
        // give some time before next thread starts
        ByteArrayInputStream in2 = new ByteArrayInputStream(inp.getBytes());
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        Thread t2 = new Thread(new User(server, in2, out2, true, false));
        t2.start();
        t.join(1000);
        server.kill();
        assertEquals("connection successful\nwelcome cliu\n", out.toString());
        assertEquals("connection successful\nerror 4 user with same name is already logged in\n", out2.toString());
    }
    
    @Test
    public void loginTwiceNoLogout() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        String inp = "login cliu 123\nlogin blah 23";
        ByteArrayInputStream in = new ByteArrayInputStream(inp.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, false, false));
        t.start();
        t.join();
        server.kill();
        assertEquals("connection successful\nwelcome cliu\nerror 7 user already online on same client\n", out.toString());
    }
    
    @Test
    public void loginLogoutLogin() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        String inp = "login cliu 123\nlogout\nlogin cliu 123\n";
        ByteArrayInputStream in = new ByteArrayInputStream(inp.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, false, false));
        t.start();
        t.join();
        server.kill();
        assertEquals("connection successful\nwelcome cliu\nwelcome cliu\n", out.toString());
    }
    
    @Test
    public void testNotEnoughArguments() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        String inp = "login cliu\n";
        ByteArrayInputStream in = new ByteArrayInputStream(inp.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, false, false));
        t.start();
        t.join();
        server.kill();
        assertEquals("connection successful\nerror 0 malformed command\n", out.toString());
    }
    
    @Test
    public void testTooManyArguments() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        String inp = "login cliu 0 1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(inp.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, false, false));
        t.start();
        t.join();
        server.kill();
        assertEquals("connection successful\nerror 0 malformed command\n", out.toString());
    }
    
    @Test
    public void testBadPassword() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        String inp = "login cliu 12\n";
        ByteArrayInputStream in = new ByteArrayInputStream(inp.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, false, false));
        t.start();
        t.join();
        server.kill();
        assertEquals("connection successful\nerror 9 invalid login or password\n", out.toString());
    }
    
}


