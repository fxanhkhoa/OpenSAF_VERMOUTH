/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javachatclient;

import GUIPannel.ClientPannel;
import GUIPannel.PrivatePanel;
import GUIPannel.RoomPannel;
import GUIPannel.SignInPannel;
import GUIPannel.SignUpPannel;
import javachatclient.Struct.UserStruct;
import javachatclient.Struct.RoomStruct;
import javachatclient.Struct.ProtocolCS;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.HashSet;
import jdk.nashorn.internal.objects.Global;

/**
 *
 * @author ubuntu
 */
public class GlobalStatic {
    
    private static GlobalStatic instance;
    
    public static ClientThread clientThread;
    public static String SERVERIP ="192.168.122.239";
    public static int SERVERPORT = 8888;
    
    public static Socket socket;
    public static BufferedWriter os;
    public static BufferedReader is;
    public static boolean connectionStatus; // Variables for dectection if connecting
    
    public static int myIDUser;
    
    public static String myUserName;
    public static String myPass;
    /*temp pass*/
    public static String myTempPass;
    public static ProtocolCS blockToSend;
    
    //For List
    public static HashSet<UserStruct> onlineUser = new HashSet<UserStruct>(); // structure of online user
    public static HashSet<RoomStruct> roomList = new HashSet<RoomStruct>(); //  structure of list room in jlist on client GUI
    public static HashSet<UserStruct> friendList = new HashSet<UserStruct>(); // structure of friend user on client GUI
    public static HashSet<RoomPannel> rPList = new HashSet<RoomPannel>(); //jlist contain all room pannel hide/show
    public static HashSet<PrivatePanel> pPList = new HashSet<PrivatePanel>();
    
    //public HashSet<RoomGUI> rGList = new HashSet<RoomGUI>(); // jlist contain all room gui hide/show
    //
    public static int size; // Number of Online User
    public static int sizeFriend; // Number of Friend User
    public static int sizeRoom; // Number of Room
    
    //For GUI
    public static SignInPannel signinPannel;
    public static SignUpPannel signupPannel;
    public static ClientPannel clientPannel;
    
    public static MainWindow mainWindow;
    // Variable support for ClientGUI
    public static String passRoomTemp = "";
    public static String nameRoomTemp = "";

    
    //For Room
    public static int myIDRoom;
    public static String nameDesUser;
    public static String nameOwner;
    public static int idRoomOwner;
    public static int idRefreshROOM;
    public static int numberOfRoom;
    public static int sizeFriendROOMGUI;
    
    //For Tab Index
    public static int numberOfIndexTab = 0;
    
    /*
    ************* Function here **************
    */
    
    public GlobalStatic(){
        signinPannel = new SignInPannel();
        signupPannel = new SignUpPannel();
        clientPannel= new ClientPannel();
        clientThread = new ClientThread();
        blockToSend = new ProtocolCS();
    }
    
    public static synchronized GlobalStatic getInstance(){
        if (instance == null){
            instance = new GlobalStatic();
        }
        return instance;
    }
    
    /*
    ********************************************************************
              FUNCION FOR USER LIST
    ********************************************************************
    */
    
    public static int UpdateOnlineUser(char[] usr, int status){
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
    
    public static int RealizeOnlineList(){
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
    
    public static int UpdateFriendList(char[] usr, int status){
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
    
    /*
    ********************************************************************
              FUNCION FOR ROOM
    ********************************************************************
    */
    
    public static int CheckOwnerRoomGUI(int IDROOM){
        for (RoomPannel rP: rPList){
            if (rP.IDROOM == IDROOM){
                //System.out.println("Owner of " + rG.IDROOM);
                return rP.isOwner;
            }
        }
        return 0;
    }
    
   public static int GetstatusROOM(int idROOM){
        for (RoomPannel rP: rPList){
            if (rP.IDROOM == idROOM){
                return rP.status;
            }
        }
        return 7;
    } 
   
   // ROOMPANEL
   
   public static int SetPassRoomPanel(int idROOM, String passRoom){
        for (RoomPannel rP: rPList){
            if (rP.IDROOM == idROOM){
                rP.passROOM = passRoom;
                return 1;
            }
        }
        return 7;
    }
    
    //Use For Room PANEL
    public static int AddToListInRoomGUI(int id, String userName){
        for (RoomPannel rP: rPList){
            if (rP.IDROOM == id){
                rP.AddToList(userName);
                System.out.println("javachatclient.GlobalStatic.AddToListInRoomGUI(): "+ userName);
                return 1;
            }
            else{
                System.err.println("NONE IF");
            }
        }
        return 0;
    }  
    
    public static void ReloadListInRoomGUI(int id){
        for (RoomPannel rP: rPList){
            if (rP.IDROOM == id){
                rP.ReloadList();
                return;
            }
        }
    }
    
    public static void ClearListInRoomGUI(int id){
        for (RoomPannel rP: rPList){
            if (rP.IDROOM == id){
                rP.ClearUserList();
                return;
            }
        }
        
    }
    
    public static void SeftOutRoom(String nameRoom){
//        System.out.println("javachatclient.GlobalStatic.SeftOutRoom() + nameroom = " + nameRoom);
        for (RoomPannel rP: rPList){
//            System.out.println("javachatclient.GlobalStatic.SeftOutRoom() + eachname = " + rP.Name);
            if (rP.Name.trim().equals(nameRoom)){
                GlobalStatic.clientThread.IOutRoom(rP.IDROOM);
                System.out.println("javachatclient.GlobalStatic.SeftOutRoom "+rP.IDROOM);
                rPList.remove(rP);
                return;
            }
        }
    }
    public static String GetNameROOM(int idROOM){
        for(RoomStruct rS: roomList){
            if(rS.idRoom==idROOM){
                return rS.roomName;
            }
        }
        return "";
    }
    
    public static int UpdateRoomList(int id, char[] roomName, char[] pass){
        
        RoomStruct temp = new RoomStruct();
        temp.idRoom = id;
        temp.roomName = new String(roomName).trim().replaceAll(" ", "");
        temp.pass = new String(pass).trim().replaceAll(" ", "");
        
        for (RoomStruct rS: roomList){
            if (rS.idRoom == id){
                System.out.println("Duplicate");
                return 0;
            }
        }
        
        roomList.add(temp);
        System.out.println("Da Tao dc Room " + temp.idRoom + temp.roomName + temp.pass);
        
        return 1;
    }
    
    public static int UpdatePrivatePanelList(String tabName){
        
        for (PrivatePanel pP: pPList){
            if (pP.GetNameTab().equals(tabName)){
                System.out.println("Duplicate Private Pane List");
                return 0;
            }
        }
        // Not in List so Add one
        pPList.add(new PrivatePanel(tabName));
        System.out.println("Add one new PrivatePanel " + tabName);
        return 1;
    }
    
    public static int RemovePrivatePanelList(String tabName){
        
        for (PrivatePanel pP: pPList){
            if (pP.GetNameTab().equals(tabName)){
                pPList.remove(pP);
                System.out.println("Remove Private Pane List");
                return 0;
            }
        }
        return 1;
    }
    
//    public static int 
    
    // For Private Panel
    public static PrivatePanel GetPrivatePanel(String tabName){
        for (PrivatePanel pP: pPList){
            System.out.println("Tab name = " + pP.GetNameTab() + tabName);
            if (pP.GetNameTab().equals(tabName)){
                System.out.println("Get dc roi: " + pP.GetNameTab());
                return pP;
            }
        }
        return null;
    }
    
    //For Private Panel
    public static void AddTextToBoxPrivatePanel(char[] curUser, char[] tabName, char[] text){
        for (PrivatePanel pP: pPList){
//            System.out.println("Tab name = " + pP.GetNameTab() + tabName);
            if (pP.GetNameTab().equals(String.valueOf(tabName).trim())){
//                System.out.println("Get dc roi: " + pP.GetNameTab());
                pP.AddToTextBox(String.valueOf(curUser).trim(), String.valueOf(text).trim());
                /**/
                String msg= String.valueOf(curUser).trim()+" : "+ String.valueOf(text).trim();
                pP.RecvSetAlign(" "+msg+  "\n");
                return;
            }
        }
    }
    
    public static void SetHaveMessPrivatePanel(String user, int status){
        for (UserStruct uS: friendList){
            if (uS.userName.equals(user)){
                uS.haveMessStatus = status;
                return;
            }
        }
    }
    
    public static void ClearAllData(){
        GlobalStatic.friendList.clear();
        GlobalStatic.onlineUser.clear();
        GlobalStatic.roomList.clear();
        pPList.clear();
        rPList.clear();
        GlobalStatic.clientPannel.ClearTab();
    }
    
    
    /*ROOMPANNEL*/
    public static void InitRoomGUI(String roomName, int IDROOM){
        //Main GUI
        for (RoomPannel rP: rPList){
            if (rP.IDROOM == IDROOM){
                System.err.println("Duplicate room");
                return;
            }
        }
        
        rPList.add(new RoomPannel(roomName,IDROOM,0));
        System.out.println("javachatclient.GlobalStatic.InitRoomGUI() OK " + IDROOM);
    }
    
    public static void SetOwnerRoomGUI(int IDROOM){
        for (RoomPannel rP: rPList){
            System.out.println("javachatclient.GlobalStatic.SetOwnerRoomGUI() " + rP.IDROOM);
            if (rP.IDROOM == IDROOM){
                rP.isOwner = 1;
                rP.SetButton();
                System.out.println("DA SET ROOM OWNER");
                return;
            }
        }
    }
    
    public static RoomPannel GetRoomPanel(int IDROOM){
        for (RoomPannel rP: rPList){
            if (rP.IDROOM == IDROOM){
                System.out.println("Got " + rP.Name);
                return rP;
            }
        }
        return null;
    }
    
    //Use For Room Panel
    public static int RemoveToListInRoomGUI(int id, String userName){
        for (RoomPannel rP: rPList){
            if (rP.IDROOM == id){
                rP.RemoveFromList(userName);
            }
        }
        return 1;
    }
    
    //Use For Room Panel
    public static int AddMessInRoomGUI(int id, String user, String mess){
        for (RoomPannel rP: rPList){
            if (rP.IDROOM == id){
                rP.AddChatMess(user, mess);
            }
        }
        return 1;
    }
    
    public static void TerminateRoomGUI(int IDROOM){
       for (RoomPannel rP: rPList){
            
            if (rP.IDROOM == IDROOM){
                if(rP.status==1)
                rPList.remove(rP);
                return;
            }
            
        }
    }
    
    public static int GetIDROOM(String nameROOM){
        for(RoomStruct rS: roomList){
            System.out.println("javachatclient.GlobalStatic.GetIDROOM()"+ rS.roomName);
            if(rS.roomName.equals(nameROOM)){
                return rS.idRoom;
            }
        }
        return 0;
    }
    /**/

    

    public static void RemoveListRoomOnClientGUI (int IDROOM){
        for(RoomStruct rS:roomList){
            if(rS.idRoom== IDROOM){
                roomList.remove(rS);
                return;
            }
        }
    }
}
