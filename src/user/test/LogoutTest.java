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
 * Tests for the logout functionality of the server
 *
 * Testing strategy:
 * 1. Testing logout having done nothing to rooms. Checks status for offline.
 * 2. Testing say to room after logout. Check for error messages.
 * 3. Test someone else say to room after logout. Checks that you do not receive it.
 * 4. Test invite a logged out user, checks for appropriate error.
 * 5. Test accept an invite after logging out, checks for appropriate error
 * 6. Test creating a room after logging out, checks for appropriate error
 * 7. Test attempt inviting others after logging out, checks for appropriate error.
 */
public class LogoutTest {
    
    private void send(PipedOutputStream pipe, Thread t, String msg) throws IOException, InterruptedException {
        pipe.write(msg.getBytes());
        pipe.flush();
        t.join(100);
    }
    
    @Test
    public void testNoRoomLogout() throws IOException, InterruptedException{
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
        send(pipe, t, "logout\n");
        send(pipe, t, "status\n");
        server.kill();
        String user1expected = "connection successful\nregisterSuccess cliu\nwelcome cliu\nroomcreated 0\nstatus offline\n";
        assertEquals(user1expected, out.toString());
    }
    
    @Test
    public void testSayAfterLogout() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        t.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe, t, "logout\n");
        send(pipe, t, "say 0 hi\n");
        server.kill();
        String user1expected = "connection successful\nwelcome cliu\nroomcreated 0\nerror 2 user not online\n";
        assertEquals(user1expected, out.toString());
    }
    
    @Test
    public void othersSayAfterLogout() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        PipedOutputStream pipe2 = new PipedOutputStream();
        PipedInputStream in2 = new PipedInputStream(pipe2);
        ByteArrayOutputStream out2 = new ByteArrayOutputStream();
        Thread t2 = new Thread(new User(server, in2, out2, true, false));
        t2.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe2, t2, "register user pass\n");
        send(pipe2, t2, "login user pass\n");
        send(pipe, t, "invite user 0\n");
        send(pipe2, t2, "accept 0\n");
        send(pipe, t, "logout\n");
        send(pipe2, t2, "say 0 hi\n");
        server.kill();
        assertEquals(false, out.toString().contains("message cliu 0 hi"));
    }
    
    @Test
    public void inviteLoggedOutUser() throws IOException, InterruptedException {
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
        send(pipe2, t2, "logout\n");
        send(pipe, t, "invite user 0\n");
        server.kill();
        assertEquals(true, out.toString().contains("error 6 contact is not online"));
        assertEquals(false, out2.toString().contains("invite 0 cliu"));
    }
    
    @Test
    public void acceptPreexistingInvite() throws IOException, InterruptedException {
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
        send(pipe, t, "invite user 0");
        send(pipe2, t2, "logout\n");
        send(pipe2, t2, "accept 0\n");
        server.kill();
        assertEquals(false, out.toString().contains("enter user 0"));
        assertEquals(true, out2.toString().contains("error 2 user not online"));
    }
    
    @Test
    public void attemptCreateRoom() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        t.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "logout\n");
        send(pipe, t, "create\n");
        server.kill();
        String user1expected = "connection successful\nwelcome cliu\nerror 2 user not online\n";
        assertEquals(user1expected, out.toString());
    }
    
    @Test
    public void attemptInvite() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user pass\n");
        send(pipe, t, "create\n");
        send(pipe, t, "logout\n");
        send(pipe, t, "invite user 0\n");
        server.kill();
        String user1expected = "connection successful\nwelcome cliu\nuserOnline user\nroomcreated 0\nerror 2 user not online\n";
        assertEquals(user1expected, out.toString());
        assertEquals(false, out2.toString().contains("invite cliu 0"));
    }
    

}
