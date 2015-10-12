#!/bin/bash

from=$(echo $1)
to=$(echo $2)
folder=$3

declare -a arr=("java" "groovy" "properties" "gradle")

fromDir=$(echo ${from//./\\/})
fromReg=$(echo ${from//./\\.})
echo "$fromDir"
echo "$fromReg"
toDir=$(echo ${to//./\\/})
toReg=$(echo ${to//./\\.})
echo "$toDir"
echo "$toReg"
for ext in "${arr[@]}"
do
	result="grep -rnil --include \*.$ext '$1' $3 | sed -E 's|(.*)\/($fromDir)\/([^\/]*)\/.*|echo \1\/\2\/\3|g' | sort -u | sh 2>&1 | sed '/Permission denied/d;' | sed -E 's|(.*)\/($fromDir)\/(.*)|mkdir -p \1\/$toDir\/\3;cp -r \1\/\2\/\3 \1\/$toDir;|g' | sh"
	echo $result
	eval $result
done

IFS='.' read -a fromArr <<< "$from"
IFS='.' read -a toArr <<< "$to"

fl=${#fromArr[@]}
tl=${#toArr[@]}

#echo $fl
#echo $tl
m=$(($fl<$tl?$fl:$tl))

function join { local IFS="$1"; shift; echo "$*"; }

for i in $(eval echo "{0..$((m-1))}")
do
	idx=$((m-i-1))
    if [ "${fromArr[m-i-1]}" != "${toArr[m-i-1]}" ]; then
    	echo "removing ${fromArr[m-i-1]} at $idx"
    	subarr=${fromArr[@]:0:idx+1}
    	echo "$subarr"
    	x=$(join , "${subarr[@]}")	
    	y=$(echo ${x// /\\/})
    	echo "$y"
  		for ext in "${arr[@]}"
  		do
	       result="grep -rnil --include \*.$ext '$1' $3 | sed -E 's|(.*)\/($y)\/.*|echo \1\/\2|g' | sort -u | sh 2>&1 | sed '/Permission denied/d;' | sed -E 's|(.*)|rm -r \1|g' | sh"
	       echo $result
	       eval $result
	    done
	fi
done

for ext in "${arr[@]}"
do
	replace="find $folder -type f -name '*.$ext' -print0 | xargs -0 sed -i 's/$fromReg/$toReg/g'"
	echo $replace
	eval $replace
done
