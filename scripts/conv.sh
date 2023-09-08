#!/bin/sh

if [ -z $1 ];then echo usage: $0 "<in>";exit;fi

QDIR=$(dirname $0)

mkdir -p $QDIR/res/drawable $QDIR/res/drawable-hdpi $QDIR/res/drawable-xhdpi $QDIR/res/drawable-xxhdpi $QDIR/res/drawable-xxxhdpi

convert $1 -resize 48x48 $QDIR/res/drawable/mod_icon.png
convert $1 -resize 72x72 $QDIR/res/drawable-hdpi/mod_icon.png
convert $1 -resize 96x96 $QDIR/res/drawable-xhdpi/mod_icon.png
convert $1 -resize 144x144 $QDIR/res/drawable-xxhdpi/mod_icon.png
convert $1 -resize 192x192 $QDIR/res/drawable-xxxhdpi/mod_icon.png

