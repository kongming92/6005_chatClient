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
 * Tests the say function into and out of the server
 * 
 * Testing Strategy:
 * 1. Say something in a room with no one else. Test that you get the message back
 * 2. Say something in a room with others. Test that everyone gets the message.
 * 3. Say something in a room before and after an invite is accepted. Test that the message is
 * only received after the invite is accepted.
 * 4. Say a message with a newline in the middle of the string (note that this is not possible in the GUI)
 * Check to see malformed command error (the command will end at the newline, and the next command picks up from there)
 * 5. Say a message to a room that you are not currently in. Checks for correct error message.
 * 6. Say a message to a room that you've been invited to but not accepted. Checks for correct error message.
 * 7. Say a message to a room that you've declined. Checks for correct error message
 * 
 * Note that cases 4-7 are not possible in a GUI but are possible if you are interacting directly with the server (via telnet for example)
 */
public class SayTest {

    private void send(PipedOutputStream pipe, Thread t, String msg) throws IOException, InterruptedException {
        pipe.write(msg.getBytes());
        pipe.flush();
        t.join(100);
    }
       
    @Test
    public void sayNoOneElse() throws IOException, InterruptedException{
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
        send(pipe, t, "say 0 hello\n");
        server.kill();
        String user1expected = "connection successful\nregisterSuccess cliu\nwelcome cliu\nroomcreated 0\nmessage cliu 0 hello\n";
        assertEquals(user1expected, out.toString());
    }
    
    @Test
    public void createSayWithOthers() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        PipedOutputStream pipe2 = new PipedOutputStream();
        PipedInputStream in2 = new PipedInputStream(pipe2);
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        Thread t2 = new Thread(new User(server, in2, out2, true, false));
        t.start();
        t2.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe2, t2, "register user2 123\n");
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe2, t2, "accept 0\n");
        send(pipe, t, "say 0 hello goodbye\n");
        server.kill();
        assertEquals(true, out.toString().contains("message cliu 0 hello goodbye\n"));
        assertEquals(true, out2.toString().contains("message cliu 0 hello goodbye\n"));
    }
    
    @Test
    public void invitedSayWithOthers() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        PipedOutputStream pipe2 = new PipedOutputStream();
        PipedInputStream in2 = new PipedInputStream(pipe2);
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        Thread t2 = new Thread(new User(server, in2, out2, true, false));
        t.start();
        t2.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "say 0 not good here\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe2, t2, "accept 0\n");
        send(pipe2, t2, "say 0 hello goodbye\n");
        server.kill();
        assertEquals(true, out.toString().contains("message user2 0 hello goodbye\n"));
        assertEquals(true, out2.toString().contains("message user2 0 hello goodbye\n"));
        assertEquals(false, out2.toString().contains("not good here\n"));
    }
    
    @Test
    public void sayNewLineTest() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        t.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe, t, "say 0 he\nllo\n");
        server.kill();
        assertEquals(true, out.toString().contains("error 1 command not found\n"));
    }
    
    @Test
    public void sayToRoomNotCurrentlyIn() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        t.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe, t, "say 1 hello\n");
        server.kill();
        String expected = "connection successful\nwelcome cliu\nroomcreated 0\nerror 3 room does not exist or you are not in it\n";
        assertEquals(expected, out.toString());
    }
    
    @Test
    public void sayToInvitedNotAccepted() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        PipedOutputStream pipe2 = new PipedOutputStream();
        PipedInputStream in2 = new PipedInputStream(pipe2);
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        Thread t2 = new Thread(new User(server, in2, out2, true, false));
        t.start();
        t2.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe2, t2, "register user pass\n");
        send(pipe2, t2, "login user pass\n");
        send(pipe, t, "invite user 0\n");
        send(pipe2, t2, "say 0 hello\n");
        server.kill();
        String userexpected = "connection successful\nregisterSuccess user\nwelcome user\ninvite 0 cliu\nerror 3 room does not exist or you are not in it\n";
        assertEquals(userexpected, out2.toString());
    }
    
    @Test
    public void sayToDeclined() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        PipedOutputStream pipe2 = new PipedOutputStream();
        PipedInputStream in2 = new PipedInputStream(pipe2);
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        Thread t2 = new Thread(new User(server, in2, out2, true, false));
        t.start();
        t2.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe2, t2, "login user pass\n");
        send(pipe, t, "invite user 0\n");
        send(pipe2, t2, "decline 0\n");
        send(pipe2, t2, "say 0 hello\n");
        server.kill();
        String userexpected = "connection successful\nwelcome user\ninvite 0 cliu\nerror 3 room does not exist or you are not in it\n";
        assertEquals(userexpected, out2.toString());
    }
}
