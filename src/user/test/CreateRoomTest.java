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
 * Test cases for create room command. Ensures that the server-client messages are correct
 * 
 * Testing strategy:
 * 1. Test create single room.
 * 2. Test create multiple rooms.
 * 3. Test invalid grammar.
 */
public class CreateRoomTest {

    @Test
    public void testCreateSingle() throws IOException, InterruptedException {
        PrintWriter clear = new PrintWriter(new FileOutputStream(new File("src/server/userfile")));
        clear.close();
        ChatServer server = new ChatServer(4445);
        String inp = "register cliu 123\nlogin cliu 123\ncreate\n";
        ByteArrayInputStream in = new ByteArrayInputStream(inp.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, false, false));
        t.start();
        t.join();
        server.kill();
        assertEquals("connection successful\nregisterSuccess cliu\nwelcome cliu\nroomcreated 0\n", out.toString());
    }
    
    @Test
    public void testCreateMultiple() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        String inp = "login cliu 123\ncreate\ncreate\ncreate\n";
        ByteArrayInputStream in = new ByteArrayInputStream(inp.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, false, false));
        t.start();
        t.join();
        server.kill();
        assertEquals("connection successful\nwelcome cliu\nroomcreated 0\nroomcreated 1\nroomcreated 2\n", out.toString());
    }
    
    @Test
    public void testMalformed() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        String inp = "login cliu 123\ncreate 1\n";
        ByteArrayInputStream in = new ByteArrayInputStream(inp.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, false, false));
        t.start();
        t.join();
        server.kill();
        assertEquals("connection successful\nwelcome cliu\nerror 0 malformed command\n", out.toString());
    }
}
