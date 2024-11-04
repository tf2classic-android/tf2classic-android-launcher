# tf2classic-android-launcher
# Copyright (C) 2024  SanyaSho
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

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
git checkout ../app/src/main/res/values/vpk.xml
sed -i "s|GET_VPK_CHECKSUM_AND_REPLACE_ME|$VPKSHA|" ../app/src/main/res/values/vpk.xml
