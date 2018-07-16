#include "../include/server.h"
List<user> user_data;
string file_path = "database";
mutex mu;
mutex mu_lock_file;
int main()
{
  
  user_data.load("database");
  server *sv = server::get_instance();
  thread yyy(&server::chat_client, sv);
  yyy.detach();
  
  thread xxx(&server::listen_connect, sv);
  xxx.join();



  // string name,pass;
  // cout<<"nhap so luong: ";
  // int n;
  
  // cin>>n;
  // fflush(stdin);
  // while(n--)
  // {
  //   cout<<"nhap username: ";
    
  //   cin>>name;
  //   fflush(stdin);
  //   cout<<"\nnhap pass: ";
    
  //   cin>>pass;
  //   fflush(stdin);
  //   user tmp(name.c_str(),pass.c_str());
  //   cout<<tmp.get_id()<<endl;
  //   cout<<user_data.insert(tmp)->get_id();
  // }
  // user_data.map_to_file("database");

// List<user> xx("database");
// vector<user> aaa;
// xx.to_vector(aaa);

// for(auto &c:aaa)
// c.show();


  return 0;
}
