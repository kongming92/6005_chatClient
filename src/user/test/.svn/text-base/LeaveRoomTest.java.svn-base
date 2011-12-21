package user.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;

import org.junit.Test;

import server.ChatServer;
import user.User;

/**
 * Tests for leave room command to and from server
 * 
 * Testing strategy:
 * 1. Single person in the room. Upon leaving the room, the room dies. Subsequent creation
 * of rooms increases the room number.
 * 2. Multiple people in the room. When one user (the one who started the room) leaves, the remaining
 * users get the leave message from the server
 * 3. Multiple people in the room. The creator of the room invites someone and leaves before the invite
 * is received. Checks to make sure that the invite can still be accepted, and that the people left
 * in the room receive both the leave messages and the new enter message from the newcomer.
 *
 */
public class LeaveRoomTest {
    
    private void send(PipedOutputStream pipe, Thread t, String msg) throws IOException, InterruptedException {
        pipe.write(msg.getBytes());
        pipe.flush();
        t.join(100);
    }
    
    @Test
    public void testSinglePersonLeaveRoomDies() throws IOException, InterruptedException {
        PrintWriter clear = new PrintWriter(new FileOutputStream(new File("src/server/userfile")));
        clear.close();
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        t.start();
        send(pipe, t, "register cliu 123\n");
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe, t, "leave 0\n");
        send(pipe, t, "create\n");
        server.kill();
        String user1expected = "connection successful\nregisterSuccess cliu\nwelcome cliu\nroomcreated 0\nroomcreated 1\n";
        assertEquals(user1expected, out.toString());
    }
    
    @Test
    public void testTwoPeopleOneLeaves() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PipedOutputStream pipe2 = new PipedOutputStream();
        PipedInputStream in2 = new PipedInputStream(pipe2);
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        Thread t2 = new Thread(new User(server, in2, out2, true, false));
        t.start();
        t2.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe2, t2, "register user2 456\n");
        send(pipe2, t2, "login user2 456\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe2, t2, "accept 0\n");
        send(pipe, t, "leave 0\n");
        server.kill();
        assertEquals(true, out2.toString().contains("leave cliu 0"));
        assertEquals(false, out.toString().contains("leave cliu 0"));
    }
    
    @Test
    public void testInviteThenLeave() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PipedOutputStream pipe2 = new PipedOutputStream();
        PipedInputStream in2 = new PipedInputStream(pipe2);
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        PipedOutputStream pipe3 = new PipedOutputStream();
        PipedInputStream in3 = new PipedInputStream(pipe3);
        ByteArrayOutputStream out3 = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        Thread t2 = new Thread(new User(server, in2, out2, true, false));
        Thread t3 = new Thread(new User(server, in3, out3, true, false));
        t.start();
        t2.start();
        t3.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe2, t2, "login user2 456\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe3, t3, "register bleh pass\n");
        send(pipe3, t3, "login bleh pass\n");
        send(pipe2, t2, "accept 0\n");
        send(pipe, t, "invite bleh 0\n");
        send(pipe, t, "leave 0\n");
        send(pipe3, t3, "accept 0\n");
        server.kill();
        assertEquals(true, out2.toString().contains("leave cliu 0"));
        assertEquals(false, out.toString().contains("leave cliu 0"));
        assertEquals(true, out3.toString().contains("enter bleh 0"));
        assertEquals(false, out3.toString().contains("leave cliu 0"));
    }
}
