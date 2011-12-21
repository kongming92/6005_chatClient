package server.test;

import server.RoomListener;

/**
 * A listener that does nothing
 * It is simply there to create Room objects to see that the internal data structures
 * of the Room object work
 */
public class TestListener implements RoomListener {

    @Override
    public void notifySay(String user, String said, int roomno) {
    }

    @Override
    public void notifyUserEnteredRoom(String user, int roomno) {
    }

    @Override
    public void notifyUserLeftRoom(String user, int roomno) {
    }

    @Override
    public void notifyUserTyping(String user, int roomno) {
    }

    @Override
    public void notifyUserEnteredText(String user, int roomno) {
    }

    @Override
    public void notifyUserIdle(String user, int roomno) {
    }

}
