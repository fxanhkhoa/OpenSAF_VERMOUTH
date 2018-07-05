#!/bin/bash

KhoaBranch=$HOME/Documents/

VermouthHelp(){
  echo "Command list
  GoMyBranch
  RunClient
  GoMasterBranch
  CloneFX
  CloneDEK
  "
}

GoMyBranch(){
  cd /home/ubuntu/Documents/OpenSAF_VERMOUTH/
  exec bash
}

RunClient(){
  java -jar /home/ubuntu/Documents/OpenSAF_VERMOUTH/Chat_Service/Client_Java/ChatClient/dist/ChatClient.jar
}

GoMasterBranch(){
  cd /home/ubuntu/OpenSAF_VERMOUTH/
  exec bash
}

CloneFX(){
  mkdir /home/ubuntu/Documents/FX
  git clone https://github.com/fxanhkhoa/OpenSAF_VERMOUTH.git /home/ubuntu/Documents/FX
  chmod -R 777 /home/ubuntu/Documents/FX
}

CloneDEK(){
  mkdir /home/ubuntu/Documents/DEK
  git clone http://fxanhkhoa@192.168.122.24/fxanhkhoa/OpenSAF_VERMOUTH.git /home/ubuntu/Documents/DEK
  chmod -R 777 /home/ubuntu/Documents/DEK
}
#read commandvar

case "$1" in
        GoMyBranch)
            GoMyBranch
            ;;
        RunClient)
            RunClient
            ;;
        GoMasterBranch)
            GoMasterBranch
            ;;
        CloneFX)
            CloneFX
            ;;
        CloneDEK)
            CloneDEK
            ;;
        *)
            VermouthHelp
            exit 1
esac
shift

exit 0
