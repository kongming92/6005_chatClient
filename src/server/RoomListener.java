package server;

/**
 * Interface RoomListener
 * Whenever there is a client --> server message that affects the entire room
 * the RoomListener is used to send a server --> client message back to everyone
 * who is in the room.
 */
public interface RoomListener {
    
    public void notifySay(String user,String said, int roomno);
    public void notifyUserEnteredRoom(String user, int roomno);
    public void notifyUserLeftRoom(String user, int roomno);
    public void notifyUserTyping(String user, int roomno);
    public void notifyUserEnteredText(String user, int roomno);
    public void notifyUserIdle(String user, int roomno);
    
}
