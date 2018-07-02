/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

/**
 *
 * @author ubuntu
 */
public class ProtocolCS {
    public enum commandCode{
        None,
        Signup,
        Signin,
        PrivateChat,
        RoomChat,
        CreateRoom,
        DeleteRoom,
        InviteToRoom,
        KickFromRoom,
        SignOut,
        RequestToRoom,
    };
    
    public static String username;
    public static String password;
    public int command = commandCode.None.ordinal();
    public int IDRoom;
    public String ownUsername; // 30 characters
    public String desUsername; // 30 characters
    public String ownPassword; // 30 characters
    public String roomPassword; // 30 characters
    public String message; // 896 characters

    public ProtocolCS() {
    }

    public ProtocolCS(int IDRoom, String ownUsername, String desUsername, String ownPassword, String roomPassword, String message) {
        this.IDRoom = IDRoom;
        this.ownUsername = ownUsername;
        this.desUsername = desUsername;
        this.ownPassword = ownPassword;
        this.roomPassword = roomPassword;
        this.message = message;
    }
    
    public char[] GetText(){
        char[] cbuf = new char[1016];
        int tempLength = 0;
        tempLength = ownUsername.length();
        for (int i = 0; i < tempLength; i++){
            cbuf[0 + i] = ownUsername.charAt(i);
        }
        tempLength = desUsername.length();
        for (int i = 0; i < tempLength; i++){
            cbuf[30 + i] = desUsername.charAt(i);
        }
        tempLength = ownPassword.length();
        for (int i = 0; i < tempLength; i++){
            cbuf[60 + i] = ownPassword.charAt(i);
        }
        tempLength = roomPassword.length();
        for (int i = 0; i < tempLength; i++){
            cbuf[90 + i] = roomPassword.charAt(i);
        }
        tempLength = message.length();
        for (int i = 0; i < tempLength; i++){
            cbuf[120 + i] = message.charAt(i);
        }
        return cbuf;
    }
 }
