
function pause(){
    read -p "$*"
}

JARFILE=`find . -name "autoGenDp.jar"`
cd $(dirname $JARFILE)

java -jar autoGenDp.jar 368 320 360 384 392 400 411 533 590 -a -v19
pause "按任意键继续"