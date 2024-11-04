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

#!/bin/sh

if [ -z $1 ];then echo usage: $0 "<in>";exit;fi

mkdir -p res/drawable res/drawable-hdpi res/drawable-xhdpi res/drawable-xxhdpi res/drawable-xxxhdpi

rsvg-convert $1 -w 48 -h 48 > res/drawable/launcher_update.png
rsvg-convert $1 -w 72 -h 72 > res/drawable-hdpi/launcher_update.png
rsvg-convert $1 -w 96 -h 96 > res/drawable-xhdpi/launcher_update.png
rsvg-convert $1 -w 144 -h 144 > res/drawable-xxhdpi/launcher_update.png
rsvg-convert $1 -w 192 -h 192 > res/drawable-xxxhdpi/launcher_update.png

