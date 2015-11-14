#!/bin/sh
HOST_FILE='/etc/hosts'

# 
# function
# get link name that is up and has inet4
#
get_4link() {
    LANG=C ip -4 -o addr | grep -v '127.0.0.1' |awk '{print $2;}'
}

# 
# function
# get ip4 upon link bond,br,eth,em
#
get_4ip() {
    _link=
    for l in `get_4link` ;do
        echo -n $l | grep -q -P '^(bond|br|eth|em)'
        if [ $? -eq 0 ] ;then
            _link=$l
            break
        fi
    done
    _ip=
    if [ -n "$_link" ] ;then
        _ip=`LANG=C ip -4 -o addr show $_link \
            | awk '{print $4}' \
            | awk -F/ '{print $1}'`
        echo $_ip
    fi
}

#
# get ip4
#
get_ip() {
    get_4ip
}

# 
# function
# instert any string into /etc/hosts
#
insert_host() {
    echo $@ >> $HOST_FILE
    echo "Append $@ into $HOST_FILE"
}

# 
# function
# append any ip host pair if you want
#
append_host() {
    _ip=$1
    _name=$2
    if [ -z "$_ip" -o -z "$_name" ] ;then
        echo "ERR, ip not specified or name not specified" 1>&2
    fi
    echo "Append $_ip $_name"
    echo -n $_ip | grep -q -P '^[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}$'
    if [ $? -eq 0 ] ;then
        _found=`grep "$_ip" $HOST_FILE`
        if [ -z "$_found" ] ;then
            insert_host $_ip $_name
        else
            echo $_found | awk '{print $2}' | grep -q "^$_name$"
            if [ $? -ne 0 ] ;then
                insert_host $_ip $_name
            else
                echo "Host already existed, $_ip $_name"
            fi
        fi
    else
        echo "ERR, invalid ip, $_ip" 1>&2
    fi
}

#
# append local ip (non 127.0.0.1) and hostname after /etc/hosts
#
append_hostname() {
    _ip=`get_4ip`
    _name=`hostname`
    append_host $_ip $_name
}

#
# function
# mkdir, check before mkddir
#
wy_mkdir() {
    _dir=$1
    echo "$_dir" | grep -q -v '\s'
    if [ $? -ne 0 ] ;then
        echo "ERR, dir cannot have space chararacter" >&2
        exit 1
    fi
    if [ ! -d "$_dir" ] ;then
        mkdir "$_dir"
        if [ $? -ne 0 ] ;then
            echo "ERR, cannot create $_dir" >&2
            exit 1
        fi
    fi
}