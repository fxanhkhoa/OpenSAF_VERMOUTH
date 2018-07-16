#!/usr/bin/env bash

path=/home/hieupham/Documents/OpenSAF_VERMOUTH
echo " Hello guys. That's so EZ for Linux :D"

toWorkspace()
{
	cd $path
	exec bash
}
toGitPrepare()
{
	cd $path
	git add .
	git commit .
}

hello(){
	echo "hello"
}
help(){
	echo "cmd following method here"
}

while test ${#} -gt 0; do
    case "$1" in
        build_cluster)
            osaf_build_cluster
            ;;
        clean)
            osaf_clean
            ;;
        configure)
            osaf_configure
            ;;
        make)
            osaf_make
            ;;
        install)
            osaf_install_cluster
            ;;
        setup)
            osaf_setup
            ;;
        hello)
            hello
            ;;
				toW)
		        toWorkspace
		            ;;
				toGP)
						toGitPrepare
												;;
        help)
            help
            ;;
        *)
            echo "Unknown command: $1"
            osaf_help
            exit 1
    esac
    shift
done
