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
echo "Generating Android SDK"

if [ -e "android-sdk" ]; then
	rm -rf android-sdk
fi

SDKTOOLS=""

# check Java version
if [[ $( java -version 2>&1 ) == *"1.8.0"* ]]; then
	echo "Using old sdk-tools without Java 11+ support"
	SDKTOOLS="https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip"
	SDKMANAGER="tools/bin/sdkmanager"
else # -version argument is removed
	echo "Using modern commandlinetools with Java 11+ support"
	SDKTOOLS="https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip"
	SDKMANAGER="cmdline-tools/bin/sdkmanager --sdk_root=android-sdk" # /shrug
fi

mkdir android-sdk || exit 1
pushd android-sdk > /dev/null
wget $SDKTOOLS -qO sdktools.zip > /dev/null || exit 1
unzip sdktools.zip || exit 1
rm sdktools.zip || exit 1
popd > /dev/null

echo y | android-sdk/$SDKMANAGER --install "build-tools;33.0.1" "platform-tools" "platforms;android-29"

echo "Now run"
echo "export ANDROID_SDK=\"$PWD/android-sdk\""
