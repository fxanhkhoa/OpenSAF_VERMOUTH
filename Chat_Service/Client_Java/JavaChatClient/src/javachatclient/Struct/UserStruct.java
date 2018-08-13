/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javachatclient.Struct;

/**
 *
 * @author ubuntu
 */
public class UserStruct {
    
    public String userName;
    public int status = 0;
    public int haveMessStatus = 0;
    
    public UserStruct(){
        
    }
    
    public UserStruct(String Name, int Status){
        this.userName = Name;
        this.status = Status;
        this.haveMessStatus = 0;
    }
    
    @Override
    public String toString() {
        return userName;
    }
}
