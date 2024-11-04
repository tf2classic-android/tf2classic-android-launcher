/*
* tf2classic-android-launcher
* Copyright (C) 2024  SanyaSho
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package su.xash.fwgslib;

// Debug CertCheck.java
// I made this because i'm tired of commenting dumbAntiMoronCheck in release builds.
// Also getApkKey will always return my correct key for DRM check in native part.

import android.content.Context;

public class CertCheck
{
	public static boolean IsDebugBuild()
	{
		return true;
	}

	public static String getApkKey()
	{
		return "4ZAs8ABJX7mIVC6DAPxT0B2Rh04="; // totally not a security breach
	}

	public static boolean dumbAntiMoronCheck( Context context )
	{
		return false; // we're always white
	}
}
