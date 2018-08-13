/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatclient;

import java.util.HashSet;
import java.util.Vector;

/**
 *
 * @author ubuntu
 */
public class SavedPreference {
     /*
    Constant status of function return
    */
    /***/
    public int back=0;
    public int statusConnect=0;
    /***/
    public static final int OK = 1;
    /**/
    public int max=0;
    public int size;
    public int sizeRoom=0;
    public int sizeFriend = 0;
    /*VARIABLE TO ROOMGUI*/
    public int sizeRoomGUI=0;
    public int sizeFriendROOMGUI = 0;
    public String nameDesUser="";
    public String nameOwner="";
    public int idRoomOwner=0;
    //public String nameUser="";
    public int idROOM=0;
    public int idRefreshROOM=0;
    /**/
    /*
    private variables
    */
    private static String SERVERIP ="192.168.122.239";
    private static int SERVERPORT = 8888;
    private static SavedPreference instance;
    private static String userName;
    private static String pass;
    public int IDUser;
    public HashSet<UserStruct> onlineUser = new HashSet<UserStruct>(); // structure of online user
    public HashSet<RoomStruct> roomList = new HashSet<RoomStruct>(); //  structure of list room in jlist on client GUI
    public HashSet<UserStruct> friendList = new HashSet<UserStruct>(); // structure of friend user on client GUI
    
    /*ROOM GUI*/
    public HashSet<UserStruct> friendROOMList = new HashSet<UserStruct>();
    /**/
    public int numberOfRoom = 0;
    
    public String nameRoomTemp;
    public String passRoomTemp;
    
    public MainGUI mG;
    public ClientGUI cG;
    public SignUpGUI suG;
    public HashSet<RoomGUI> rGList = new HashSet<RoomGUI>(); // jlist contain all room gui hide/show
    
    public SavedPreference(){
        
    }
    
    protected void InitMainGUI(){
        //Main GUI
        mG = new MainGUI();
    }
    
    /*ROOMGUI*/
    protected void InitRoomGUI(String roomName, int IDROOM){
        //Main GUI
        for (RoomGUI rG: rGList){
            if (rG.IDROOM == IDROOM){
                System.err.println("Duplicate room");
                return;
            }
        }
        rGList.add(new RoomGUI(roomName,IDROOM,0));
    }
    
    protected void StopAllRoomGUI(){
        for (RoomGUI rG: rGList){
            if (rG.isActive()){
                rG.dispose();
            }
        }
    }
    
    protected void OpenRoomGUI(int IDROOM){
        for (RoomGUI rG: rGList){
            if (rG.IDROOM == IDROOM){
                rG.status=1;
                rG.setVisible(true);
                return;
            }
        }
    }
    protected void HideRoomGUI(int IDROOM){
        for (RoomGUI rG: rGList){
            if (rG.IDROOM == IDROOM){
                rG.status=5;
                rG.setVisible(false);
            }
        }
    }
    public int GetstatusROOM(int idROOM){
        for (RoomGUI rG: rGList){
            if (rG.IDROOM == idROOM){
                return rG.status;
            }
        }
        return 7;
    }
    
    protected void TerminateRoomGUI(int IDROOM){
        for (RoomGUI rG: rGList){
            
            if (rG.IDROOM == IDROOM){
                if(rG.status==1)
                    rG.dispose();
                rGList.remove(rG);
            }
            
        }
    }
    protected void RemoveListRoomOnClientGUI (int IDROOM){
        for(RoomStruct rS:roomList){
            if(rS.idRoom== IDROOM){
                roomList.remove(rS);
            }
        }
    }
    
    protected void SetOwnerRoomGUI(int IDROOM){
        for (RoomGUI rG: rGList){
            if (rG.IDROOM == IDROOM){
                rG.isOwner = 1;
                rG.SetButton();
                System.out.println("DA SET ROOM OWNER");
                return;
            }
        }
    }
    
    
    protected int CheckOwnerRoomGUI(int IDROOM){
        for (RoomGUI rG: rGList){
            if (rG.IDROOM == IDROOM){
                //System.out.println("Owner of " + rG.IDROOM);
                return rG.isOwner;
            }
        }
        return 0;
    }
    
    /**/
    protected void InitClientGUI(){
        cG = new ClientGUI();
    }
    
    protected void OpenClientGUI(){
        cG.setVisible(true);
    }
    
    protected void HideClientGUI(){
        cG.setVisible(false);
    }
    
    protected void HideMainGUI(){
        mG.setVisible(false);
    }
    
    protected void OpenMainGUI(){
        this.mG.setVisible(true);
    }
    
    public int SetServerIP(String IP){
        SERVERIP = IP;
        return OK;
    }
    
    public String GetServerIP(){
        return SERVERIP;
    }
    
    public int SetServerPORT(int Port){
        SERVERPORT = Port;
        return OK;
    }
    
    public int GetServerPORT(){
        return SERVERPORT;
    }
    
    public void SetUserName(String usr){
        userName = usr;
    }
    
    public String GetUserName(){
        return userName;
    }
    
    public void SetPass(String password){
        pass = password;
    }
    
    public String GetPassword(){
        return pass;
    }
    
    public int GetIDUser(){
        return IDUser;
    }
    
    public void SetIDUser(int value){
        IDUser = value;
    }
    
    public void RemoveDupli(){
        HashSet<UserStruct> hs1 = new HashSet<UserStruct>(onlineUser);
        Vector<UserStruct> vc2 = new Vector<UserStruct>(hs1);
        //onlineUser = vc2;
    }
    
    public int RealizeOnlineList(){
        System.out.println("Size + size friend: " + size + sizeFriend);
        //for (int i = 0; i < this.sizeFriend; i++){
        for (UserStruct uS: friendList){
            
            //friendList.elementAt(i).status = 1;
            System.out.println("realize " + uS.userName + uS.status);
            for (UserStruct uS1 : onlineUser){
                if (uS1.userName.equals(uS.userName)){
                    uS.status = 1;
                    System.out.println("Thay doi" + uS.status);
                    break;
                }
                else{
                    uS.status = 0;
                    //System.out.println("ko Thay doi" + uS.status);
                }
            }
        }
        
        return 1;
    }
    
    public int AddOnlineUser(char[] usr, int status){
        String bufferString = new String(usr).trim().replaceAll(" ", "");
        
        //Debug
        System.out.print("moi add :");
        System.out.println(bufferString);
        
        //Struct User 
        UserStruct temp = new UserStruct();
        temp.userName = bufferString; // put username in
        
        //String temp = usr.toString().trim();
        if (!onlineUser.contains(bufferString)){
            if (status == 1){
                temp.status = 1;
                onlineUser.add(temp);
                return 1;
            }
            else{
                temp.status = 0;
                onlineUser.add(temp);
                return 1;
            }
        }
        return 0;
    }
    
    public int UpdateOnlineUser(char[] usr, int status){
        String bufferString = new String(usr).trim().replaceAll(" ", "");
        
        //Struct User 
        UserStruct temp = new UserStruct();
        
        temp.userName = bufferString; // put username in
        temp.status = status;
        
        if (status == 1){
            for (UserStruct uS: onlineUser){
                if (uS.userName.equals(temp.userName)){
                    uS.status = 1;
                    System.out.println("da co roi trong onlineUser");
                    return 1;
                }
            }
            onlineUser.add(temp);
            System.out.println("Moi them: " + temp.userName + temp.status);
        }
        
        else if (status == 0){
            for (UserStruct uS: onlineUser){
                if (uS.userName.equals(temp.userName)){
                    onlineUser.remove(uS);
                    System.out.println("da Remove");
                    return 1;
                }
            }
                        
            return 1;
        }
        
        for (UserStruct uS: onlineUser){
                System.out.println("online user when status = 0" + uS.userName + uS.status);
        }
        
        return 1;
    }
/* 
==========================================================================
                       REFRESH LIST ROOMGUI (#14)
========================================================================== 
*/
    
    
    public int UpdateFriendROOMList(char[] usr, int status){
        String bufferString = new String(usr).trim().replaceAll(" ", "");
   
        //Struct User 
        UserStruct temp = new UserStruct();        
        
        temp.userName = bufferString; // put username in
        temp.status = status;
        
        //System.out.println("Vao Update Friend List" + temp.userName + temp.status);
        
        
        for (UserStruct uS: friendROOMList){
            //System.out.println("US: " + uS.userName + uS.status);
            if (uS.userName.equals(temp.userName)){
                System.out.println("Duplicate" + uS.userName);
                return 1;
            }
                
        }
        
        if (!friendList.contains(temp)){
            friendList.add(temp);
            System.err.println("Added: " + temp.userName + temp.status);
        }

        return 1;
    }

/* 
==========================================================================
                       REFRESH LIST ROOMGUI (#14)
========================================================================== 
*/    
    
    public int UpdateFriendList(char[] usr, int status){
        String bufferString = new String(usr).trim().replaceAll(" ", "");
   
        //Struct User 
        UserStruct temp = new UserStruct();        
        
        temp.userName = bufferString; // put username in
        temp.status = status;
        
        //System.out.println("Vao Update Friend List" + temp.userName + temp.status);
        
        
        for (UserStruct uS: friendList){
            //System.out.println("US: " + uS.userName + uS.status);
            if (uS.userName.equals(temp.userName)){
                System.out.println("Duplicate" + uS.userName);
                return 1;
            }
                
        }
        
        if (!friendList.contains(temp)){
            friendList.add(temp);
            System.err.println("Added: " + temp.userName + temp.status);
        }

        return 1;
    }
    
    public int UpdateRoomList(int id, char[] roomName, char[] pass){
        
        RoomStruct temp = new RoomStruct();
        temp.idRoom = id;
        temp.roomName = new String(roomName).trim().replaceAll(" ", "");
        temp.pass = new String(pass).trim().replaceAll(" ", "");
        
        for (RoomStruct rS: roomList){
            if (rS.idRoom == id){
                System.out.println("Duplicate");
                return 1;
            }
        }
        
        roomList.add(temp);
        System.out.println("Da Tao dc Room " + temp.idRoom + temp.roomName + temp.pass);
        
        return 1;
    }
    public int GetIDROOM(String nameROOM){
        for(RoomStruct rS: roomList){
            if(rS.roomName.equals(nameROOM)){
                return rS.idRoom;
            }
        }
        return 0;
    }
    public String GetNameROOM(int idROOM){
        for(RoomStruct rS: roomList){
            if(rS.idRoom==idROOM){
                return rS.roomName;
            }
        }
        return "";
    }
    
    //Use For Room GUI
    public void MakeRefreshClickInRoomGUI(int id){
        for (RoomGUI rG: rGList){
            if (rG.IDROOM == id){
                rG.RefreshClick();
                return;
            }
        }
    }
    
    //Use For Room GUI
    public int AddToListInRoomGUI(int id, String userName){
        for (RoomGUI rG: rGList){
            if (rG.IDROOM == id){
                rG.AddToList(userName);
            }
        }
        return 1;
    }
    //Use For Room GUI
    public int RemoveToListInRoomGUI(int id, String userName){
        for (RoomGUI rG: rGList){
            if (rG.IDROOM == id){
                rG.RemoveFromList(userName);
            }
        }
        return 1;
    }
    
    
    //Use For RoomGUI
    public int AddMessInRoomGUI(int id, String user, String mess){
        for (RoomGUI rG: rGList){
            if (rG.IDROOM == id){
                rG.AddChatMess(user, mess);
            }
        }
        return 1;
    }
    
    
    /*find the man go heaven*/
    public void AddOfflineUser(char[] usr){
        
        String bufferString = new String(usr).trim().replaceAll(" ", "");
        System.out.print("client sign out :");
        System.out.println(bufferString);
        //String temp = usr.toString().trim();
        //offlineUser.add(bufferString);
        
        onlineUser.remove(bufferString);
    }
    
    public int AddRoom(int IDRoom, String pass){
        System.err.print("vao dc: ");
        //String bufferPass = new String(pass).trim().replaceAll(" ", "");
        RoomStruct rS = new RoomStruct();
        rS.idRoom = IDRoom;
        //rS.pass = bufferPass;
        rS.pass = pass;
        roomList.add(rS);
        System.err.print("vao d dc: ");
        numberOfRoom += 1;
        System.err.print("Add dc: ");
        System.err.println(rS.idRoom);
        return 1;
    }
    
    /*******************************/
    
    public static synchronized SavedPreference getInstance(){
        if (instance == null){
            instance = new SavedPreference();
        }
        return instance;
    }
}
