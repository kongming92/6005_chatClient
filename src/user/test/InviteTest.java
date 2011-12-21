package user.test;

import static org.junit.Assert.*;

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
 * Tests invite message passing into and out of the server
 *
 * Testing strategy:
 * 1. Test invite a single user to a room. Tests for the user receiving the invite message and joining the room.
 * 2. Test invite a single user and leave room. Tests for the user still being able to join.
 * 3. Test multiple invites, all accept. Tests that they can all join
 * 4. Test multiple invites, all decline. Tests that we do not see any join.
 * 5. Test multiple invites, some accept.
 * 6. Test accepting an invite to a no longer existing room. Tests for error message
 * 7. Test declining an invite to a no longer existing room. Makes sure there is no error message
 * 8. Test sending accept message to a nonexistent invite. Tests for error message
 * 9. Test sending decline message to a nonexistent invite. Make sure there is no error message
 * 10. Test invite multiple times to the same room, only receive one invite message.
 * 11. Test invite same user to multiple rooms, receive all the messages.
 * 12. Test invite a nonexistent user, test for error message
 * 13-18. Test invite, accept and decline messages that do not conform to the grammar. Test for malformed command error.
 */
public class InviteTest {

    private void send(PipedOutputStream pipe, Thread t, String msg) throws IOException, InterruptedException {
        pipe.write(msg.getBytes());
        pipe.flush();
        t.join(100);
    }
                                                            
    @Test
    public void testSingleInvite() throws IOException, InterruptedException {
        PrintWriter clear = new PrintWriter(new FileOutputStream(new File("src/server/userfile")));
        clear.close();
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
        send(pipe, t, "register cliu 123\n");
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe2, t2, "register user2 123\n");
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe2, t2, "accept 0\n");
        server.kill();
        String user1expected = "connection successful\nregisterSuccess cliu\nwelcome cliu\nroomcreated 0\nuserOnline user2\nenter user2 0\n";
        String user2expected = "connection successful\nregisterSuccess user2\nwelcome user2\ninvite 0 cliu\nenter user2 0\n";
        assertEquals(user1expected, out.toString());
        assertEquals(user2expected, out2.toString());
    }
    
    @Test
    public void testOriginalInviteLeave() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe2, t2, "accept 0\n");
        send(pipe3, t3, "register user3 456\n");
        send(pipe3, t3, "login user3 456\n");
        send(pipe, t, "invite user3 0\n");
        send(pipe, t, "logout\n");
        send(pipe3, t3, "accept 0\n");
        server.kill();
        String user3expected = "connection successful\nregisterSuccess user3\nwelcome user3\ninvite 0 cliu\noffline cliu\nenter user3 0\n";
        server.kill();
        assertEquals(user3expected, out3.toString());
    }
    
    @Test
    public void inviteMultiplePeopleAccept() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user2 123\n");
        send(pipe3, t3, "login user3 456\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe, t, "invite user3 0\n");
        send(pipe2, t2, "accept 0\n");
        send(pipe3, t3, "accept 0\n");
        server.kill();
        String userexpected = "connection successful\nwelcome cliu\nroomcreated 0\nuserOnline user2\nuserOnline user3\nenter user2 0\nenter user3 0\n";
        server.kill();
        assertEquals(userexpected, out.toString());
    }
    
    @Test
    public void inviteMultiplePeopleDecline() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user2 123\n");
        send(pipe3, t3, "login user3 456\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe, t, "invite user3 0\n");
        send(pipe2, t2, "decline 0\n");
        send(pipe3, t3, "decline 0\n");
        server.kill();
        String userexpected = "connection successful\nwelcome cliu\nroomcreated 0\nuserOnline user2\nuserOnline user3\n";
        server.kill();
        assertEquals(userexpected, out.toString());
    }
    
    @Test
    public void inviteMultiplePeopleDeclineAccept() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user2 123\n");
        send(pipe3, t3, "login user3 456\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe, t, "invite user3 0\n");
        send(pipe2, t2, "decline 0\n");
        send(pipe3, t3, "accept 0\n");
        server.kill();
        String userexpected = "connection successful\nwelcome cliu\nroomcreated 0\nuserOnline user2\nuserOnline user3\nenter user3 0\n";
        server.kill();
        assertEquals(userexpected, out.toString());
    }
    
    @Test
    public void acceptNoLongerExistent() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe, t, "logout\n");
        send(pipe2, t2, "accept 0\n");
        server.kill();
        String user2expected = "connection successful\nwelcome user2\ninvite 0 cliu\noffline cliu\nerror 3 room does not exist or you are not in it\n";
        server.kill();
        assertEquals(user2expected, out2.toString());
    }
    
    @Test
    public void declineNoLongerExistent() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe, t, "logout\n");
        send(pipe2, t2, "decline 0\n");
        server.kill();
        String user2expected = "connection successful\nwelcome user2\ninvite 0 cliu\noffline cliu\n";
        server.kill();
        assertEquals(user2expected, out2.toString());
    }
    
    @Test
    public void acceptNonexistentInvite() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        t.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "accept 1\n");
        server.kill();
        String expected = "connection successful\nwelcome cliu\nerror 3 room does not exist or you are not in it\n";
        assertEquals(expected, out.toString());
    }
    
    @Test
    public void declineNonexistentInvite() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        t.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "decline 1\n");
        server.kill();
        String expected = "connection successful\nwelcome cliu\n";
        assertEquals(expected, out.toString());
    }
    
    @Test
    public void multipleInviteSameRoom() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe3, t3, "login user3 456\n");
        send(pipe, t, "invite user3 0\n");
        send(pipe, t, "invite user3 0\n");
        server.kill();
        String expected = "connection successful\nwelcome user3\ninvite 0 cliu\n";
        assertEquals(expected, out3.toString());
    }
    
    @Test
    public void invitesToMultipleRooms() throws IOException, InterruptedException {
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
        send(pipe, t, "create\n");
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe, t, "invite user2 1\n");
        server.kill();
        String expected2 = "connection successful\nwelcome user2\ninvite 0 cliu\ninvite 1 cliu\n";
        assertEquals(expected2, out2.toString());
    }
    
    @Test
    public void inviteNonexistentUser() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        t.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe, t, "invite user 0\n");
        server.kill();
        String expected = "connection successful\nwelcome cliu\nroomcreated 0\nerror 6 contact is not online\n";
        assertEquals(expected, out.toString());
    }
    
    @Test
    public void inviteTooShort() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        t.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe, t, "invite user\n");
        server.kill();
        String expected = "connection successful\nwelcome cliu\nroomcreated 0\nerror 0 malformed command\n";
        assertEquals(expected, out.toString());
    }
    
    @Test
    public void inviteTooLong() throws IOException, InterruptedException {
        ChatServer server = new ChatServer(4445);
        PipedOutputStream pipe = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(pipe);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thread t = new Thread(new User(server, in, out, true, false));
        t.start();
        send(pipe, t, "login cliu 123\n");
        send(pipe, t, "create\n");
        send(pipe, t, "invite user 1 2\n");
        server.kill();
        String expected = "connection successful\nwelcome cliu\nroomcreated 0\nerror 0 malformed command\n";
        assertEquals(expected, out.toString());
    }
    
    @Test
    public void acceptTooShort() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe2, t2, "accept \n");
        server.kill();
        String user2expected = "connection successful\nwelcome user2\ninvite 0 cliu\nerror 0 malformed command\n";
        assertEquals(user2expected, out2.toString());
    }
    
    @Test
    public void acceptTooLong() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe2, t2, "accept 0 1 \n");
        server.kill();
        String user2expected = "connection successful\nwelcome user2\ninvite 0 cliu\nerror 0 malformed command\n";
        assertEquals(user2expected, out2.toString());
    }
    
    @Test
    public void declineTooShort() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe2, t2, "decline\n");
        server.kill();
        String user2expected = "connection successful\nwelcome user2\ninvite 0 cliu\nerror 0 malformed command\n";
        assertEquals(user2expected, out2.toString());
    }
    
    @Test
    public void declineTooLong() throws IOException, InterruptedException {
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
        send(pipe2, t2, "login user2 123\n");
        send(pipe, t, "invite user2 0\n");
        send(pipe2, t2, "decline 0 0\n");
        server.kill();
        String user2expected = "connection successful\nwelcome user2\ninvite 0 cliu\nerror 0 malformed command\n";
        assertEquals(user2expected, out2.toString());
    }
}

