#!/bin/bash
COOLDESKTOP_FILE="/opt/cooldesktop/cooldesktop.jar"
COOLDESKTOP_SERVICE="/etc/systemd/system/cooldesktop.service"
if [ "$EUID" -ne 0 ];then
    echo "Please run this script as root"
    exit 1
fi

java=$(java -version > /dev/null 2>&1)
if [ $? -ne 0 ];then
    echo "java not install"
    exit 1
fi
mkdir -p /opt/cooldesktop
wget https://www.houxinlin.com/res/cool_sum.txt -O cool_sum.txt
if [ $? -ne 0 ];then
    echo "校验合下载失败"
    exit 1
fi
cooldesktopSum=$(cat "./cool_sum.txt" | awk '{print $1}')
cooldesktopDownloadUrl=$(cat "./cool_sum.txt" | awk '{print $2}')

while :
do
    if [ ! -f "$COOLDESKTOP_FILE" ] || [ $(shasum $COOLDESKTOP_FILE | awk '{print $1}') != $cooldesktopSum ]
    then
        rm -rf $COOLDESKTOP_FILE
        echo "download"
        if [ -f "$COOLDESKTOP_SERVICE" ]; then
            systemctl stop cooldesktop.service
        fi
        wget $cooldesktopDownloadUrl -O $COOLDESKTOP_FILE
    else
        break
    fi
done
$(touch $COOLDESKTOP_SERVICE)
java_home=$(which java)
echo "[Unit]
Description=Cooldesktop Service
After=syslog.target network.target

[Service]
SuccessExitStatus=143
User=root
Type=simple
WorkingDirectory=/opt/cooldesktop/
ExecStop=/bin/kill -15 \$MAINPID
ExecStart=$java_home -jar cooldesktop.jar

[Install]
WantedBy=multi-user.target
" > $COOLDESKTOP_SERVICE
systemctl daemon-reload
echo "done."
echo "服务配置成功,请运行'systemctl start coolstop.service'启动cooldesktop或'systemclt stop cooldsktop.service'停止cooldesktop."
isRun=$(systemctl status cooldesktop.service |grep "active (running)")
if [ $? -ne 0 ];then
    echo "是否启动?y/n"
    read state
    if [ $state = "y" ];then
        systemctl start cooldesktop.service
    fi
else
    echo $isRun
fi
