#!/bin/bash

if [ -e "../app/src/main/extras_dir.vpk" ]; then
	rm ../app/src/main/extras_dir.vpk
fi

if ! command -v vpk &> /dev/null; then
	echo "VPK not found. Installing..."
	pip install vpk
fi

vpk -c vpk/ ../app/src/main/assets/extras_dir.vpk

export VPKSHA=$(sha1sum ../app/src/main/assets/extras_dir.vpk | cut -d ' ' -f 1)

echo "VPK sha1sum =" $VPKSHA

# aftomathicc
sed -i "s|GET_VPK_CHECKSUM_AND_REPLACE_ME|$VPKSHA|" ../app/src/main/res/values/vpk.xml
