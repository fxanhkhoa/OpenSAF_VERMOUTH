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
    
    public String GetText(){
        int k = 98;
        char cm = (char) k;
        char rid = (char)(IDRoom);
        int tempLength = 0;
        tempLength = ownUsername.length();
        for (int i = 0; i < 30 - tempLength; i++){
            ownUsername += ' ';
        }
        tempLength = desUsername.length();
        for (int i = 0; i < 30 - tempLength; i++){
            desUsername += ' ';
        }
        tempLength = ownPassword.length();
        for (int i = 0; i < 30 - tempLength; i++){
            ownPassword += ' ';
        }
        tempLength = roomPassword.length();
        for (int i = 0; i < 30 - tempLength; i++){
            roomPassword += ' ';
        }
        tempLength = message.length();
        for (int i = 0; i < 896 - tempLength; i++){
            message += ' ';
        }
        return cm + rid + ownUsername + desUsername + ownPassword + roomPassword + message;
    }
 }
