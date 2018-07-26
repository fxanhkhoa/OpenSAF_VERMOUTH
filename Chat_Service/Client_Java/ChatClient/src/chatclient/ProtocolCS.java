/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.nio.ByteBuffer;

/**
 *
 * @author ubuntu
 */
public class ProtocolCS{
    public enum commandCode{
        None,//0
        Signup,
        Signin,
        PrivateChat,
        RoomChat,
        CreateRoom,
        DeleteRoom,
        InviteToRoom,
        KickFromRoom,
        SignOut,//9
        RequestToRoom,
        RePass,//11
        a12,//12
        a13,//13
        a14,//14
        a15,//15
        a16,//16
        a17,//17
        a18,//18
        a19,//19
        RequestFriend,//20
        ReFresh//21
    }
    
   
    // 999 request connect server
    // 998 success
    
    public static String username;
    public static String password;
    public int command = commandCode.None.ordinal();
    public int IDRoom;
    public String ownUsername; // 30 characters
    public String desUsername; // 30 characters
    public String ownPassword; // 30 characters
    public String roomPassword; // 30 characters
    public String message; // 892 characters
    public int mIDUser;
    
    private SavedPreference sP =  SavedPreference.getInstance();

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
        char[] cbuf = new char[1024];
        char[] number_cmd = new char[4];
        char[] number_room = new char[4];
        char[] number_user_ID = new char[4];
        
        cbuf[0] = '\0';
        //number_cmd = Character.toChars(command);
        //number_cmd = Character.allocate(4).putInt(command).array();
        //number_room = Character.toChars(IDRoom);
        //number_room = ByteBuffer.allocate(4).putInt(IDRoom).array();
        number_cmd[0] = (char) ((command >> 0) & 0xFF);//
        number_cmd[1] = (char) ((command >> 8) & 0xFF);
        number_cmd[2] = (char) ((command >> 16) & 0xFF);
        number_cmd[3] = (char) ((command >> 24) & 0xFF);
        
        System.out.print(number_cmd[0]);
        System.out.print(number_cmd[1]);
        System.out.print(number_cmd[2]);
        System.out.println(number_cmd[3]);
        
        number_room[0] = (char) ((IDRoom >> 0) & 0xFF);
        number_room[1] = (char) ((IDRoom >> 8) & 0xFF);
        number_room[2] = (char) ((IDRoom >> 16) & 0xFF);
        number_room[3] = (char) ((IDRoom >> 24) & 0xFF);
        for (int i = 0; i < 4; i++){
            cbuf[0 + i] = number_cmd[i];
        }
        for (int i = 0; i < 4; i++){
            cbuf[4 + i] = number_room[i];
        }
        int tempLength = 0;
        tempLength = ownUsername.length();
        for (int i = 0; i < tempLength; i++){
            cbuf[8 + i] =  ownUsername.charAt(i);
        }
        tempLength = desUsername.length();
        for (int i = 0; i < tempLength; i++){
            cbuf[38 + i] = desUsername.charAt(i);
        }
        tempLength = ownPassword.length();
        for (int i = 0; i < tempLength; i++){
            cbuf[68 + i] = ownPassword.charAt(i);
        }
        tempLength = roomPassword.length();
        for (int i = 0; i < tempLength; i++){
            cbuf[98 + i] = roomPassword.charAt(i);
        }
        tempLength = message.length();
        for (int i = 0; i < tempLength; i++){
            cbuf[128 + i] = message.charAt(i);
        }
        
        mIDUser = sP.IDUser;
        
        number_user_ID[0] = (char) ((mIDUser >> 0) & 0xFF);
        number_user_ID[1] = (char) ((mIDUser >> 8) & 0xFF);
        number_user_ID[2] = (char) ((mIDUser >> 16) & 0xFF);
        number_user_ID[3] = (char) ((mIDUser >> 24) & 0xFF);
        System.err.println(number_user_ID);
        System.err.println(mIDUser);
        for (int i = 0; i < 4; i++){
            cbuf[1020 + i] = number_user_ID[i];
        }
        
        return cbuf;
    }
 }
