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
public class RoomStruct {
    public int idRoom;
    public String roomName;
    public String pass;
    
    public RoomStruct(){
        
    }
    
    @Override
    public String toString() {
        return String.valueOf(roomName);
    }
}
