package message.tag;

public class MessageTag {
    public static final byte PING = 0;
    public static final byte PLAY = -128;
    public static final byte ROOM_FULL = -127;
    public static final byte MY_INFO = -126;
    public static final byte JOIN_ROOM = -125;
    public static final byte UPDATE_ROOM = -124;
    public static final byte UPDATE_SLOT_EMPTY = -123;
    public static final byte ALL_ROLES = -121;
    public static final byte MY_ROLE = -120;
    public static final byte CHAT = -119;
    public static final byte NOTIFICATION = -118;
    public static final byte CHANGE_GAME_STATE = -117;
    public static final byte ROLE_DISCLOSURE = -116;
    public static final byte COUNT_DOWN = -115;
    public static final byte SELECT_PLAYER = -114;
    public static final byte VOTE = -113;
    public static final byte UN_VOTE = -112;
    public static final byte SET_CAN_CHAT = -111;
    public static final byte SET_CAN_VOTE = -110;
    public static final byte SET_ALIVE = -109;
}
