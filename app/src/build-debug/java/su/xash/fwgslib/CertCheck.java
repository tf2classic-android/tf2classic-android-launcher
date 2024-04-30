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
