package server.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import server.ChatServer;
import server.Room;

/**
 * Tests to make sure that the invite/accept/decline mechanism for rooms works properly
 * NOTE: Other tests to make sure the server is functioning correctly is placed in the package user.test
 * as they test the message passing into and out of the server.
 * 
 * Testing strategy
 * 1. Test to make sure a room can be created with proper ID
 * 2. Test simple invite, make sure guest is on guest list, uninvited guests are not
 * 3. Test accept invite, make sure join is successful, guest is removed from guest list
 * 4. Test join without invite, make sure join is unsuccessful
 * 5. Test decline invite, make sure removed from guest list
 * 6. Test decline then accept, make sure removed from guest list, and the join is unsucessful
 */
public class TestServerRoom {
    
    @Test
    public void testRoom() throws IOException {
        ChatServer server = new ChatServer(4445);
        Room room = new Room(server, "cliu", new TestListener(), 1);
        server.kill();
        assertEquals(room.getId(), 1);
    }
    
    @Test
    public void testInvite() throws IOException {
        ChatServer server = new ChatServer(4445);
        Room room = new Room(server, "cliu", new TestListener(), 1);
        room.inviteUser("cliu", "random");
        server.kill();
        assertEquals(true, room.isInvited("random"));
        assertEquals(false, room.isInvited("stranger"));
    }

    @Test
    public void testInviteAccept() throws IOException {
        ChatServer server = new ChatServer(4445);
        Room room = new Room(server, "cliu", new TestListener(), 1);
        room.inviteUser("cliu", "random");
        boolean invited = room.isInvited("random");
        boolean joined = room.join("random", new TestListener());
        server.kill();
        assertEquals(true, invited);
        assertEquals(true, joined);
        assertEquals(false, room.isInvited("random"));
    }
    
    @Test
    public void testNoInviteJoin() throws IOException {
        ChatServer server = new ChatServer(4445);
        Room room = new Room(server, "cliu", new TestListener(), 1);
        boolean invited = room.isInvited("random");
        boolean joined = room.join("random", new TestListener());
        server.kill();
        assertEquals(false, invited);
        assertEquals(false, joined);
    }
    
    @Test
    public void testDecline() throws IOException {
        ChatServer server = new ChatServer(4445);
        Room room = new Room(server, "cliu", new TestListener(), 1);
        room.inviteUser("cliu", "random");
        boolean invited = room.isInvited("random");
        room.decline("random");
        server.kill();
        assertEquals(true, invited);
        assertEquals(false, room.isInvited("random"));
    }
    
    @Test
    public void testDeclineThenJoin() throws IOException {
        ChatServer server = new ChatServer(4445);
        Room room = new Room(server, "cliu", new TestListener(), 1);
        room.inviteUser("cliu", "random");
        boolean invited = room.isInvited("random");
        room.decline("random");
        boolean joined = room.join("random", new TestListener());
        server.kill();
        assertEquals(true, invited);
        assertEquals(false, joined);
        assertEquals(false, room.isInvited("random"));
    }
}
