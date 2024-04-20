package com.valvesoftware;

import java.io.File;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import me.nillerusr.ExtractAssets;
import me.nillerusr.LauncherActivity;

import me.sanyasho.tf2classic.R;
import su.xash.fwgslib.CertCheck;

public class ValveActivity2 // not activity, i am lazy to change native methods
{
	public static SharedPreferences mPref;

	public static native void setArgs( String args );

	public static native int setenv( String name, String value, int overwrite );

	private static native void nativeOnActivityResult( Activity activity, int i, int i2, Intent intent );

	public static boolean findGameinfo( String path )
	{
		File dir = new File( path );
		if( !dir.isDirectory() )
			return false;

		for( File file : dir.listFiles() )
		{
			if( file.isDirectory() )
			{
				for( File f : file.listFiles() )
				{
					if( f.getName().toLowerCase().equals( "gameinfo.txt" ) )
						return true;
				}
			}
		}

		return false;
	}

	static public boolean isModGameinfoExists( String path )
	{
		File dir = new File( path );
		if( !dir.isDirectory() )
			return false;

		for( File file : dir.listFiles() )
		{
			if( file.isFile() && file.getName().toLowerCase().equals( "gameinfo.txt" ) )
				return true;
		}

		return false;
	}

	static public boolean preInit( Context context, Intent intent )
	{
		mPref = context.getSharedPreferences( "mod", 0 );
		String gamepath = mPref.getString( "gamepath", LauncherActivity.getDefaultDir() + "/srceng_tf2classic" );
		String gamedir = intent.getStringExtra( "gamedir" );
		if( gamedir == null || gamedir.isEmpty() )
			gamedir = LauncherActivity.MOD_NAME;

		if( !findGameinfo( gamepath ) || !isModGameinfoExists( gamepath + "/" + gamedir ) )
			return false;

		return true;
	}

	static public void initNatives( Context context, Intent intent )
	{
		mPref = context.getSharedPreferences( "mod", 0 );
		ApplicationInfo appinf = context.getApplicationInfo();
		String gamepath = mPref.getString( "gamepath", LauncherActivity.getDefaultDir() + "/srceng_tf2classic" );

		String argv = intent.getStringExtra( "argv" );
		String gamedir = intent.getStringExtra( "gamedir" );
		String customVPK = intent.getStringExtra( "vpk" );
		Log.v( "SRCAPK", "argv=" + argv );

		if( gamedir == null || gamedir.isEmpty() )
			gamedir = LauncherActivity.MOD_NAME;

		if( argv == null || argv.isEmpty() )
			argv = mPref.getString( "argv", context.getString( R.string.default_commandline_arguments ) );

		argv = "-game " + gamedir + " " + argv;

		ExtractAssets.extractAssets( context );

		String vpks = context.getFilesDir().getPath() + "/" + ExtractAssets.VPK_NAME;
		if( customVPK != null && !customVPK.isEmpty() )
			vpks = customVPK + "," + vpks;

		Log.v( "SRCAPK", "vpks=" + vpks );

		setenv( "EXTRAS_VPK_PATH", vpks, 1 );
		setenv( "LANG", Locale.getDefault().toString(), 1 );
		setenv( "APP_DATA_PATH", appinf.dataDir, 1 );
		setenv( "APP_LIB_PATH", appinf.nativeLibraryDir, 1 );
		setenv( "VALVE_GAME_PATH", gamepath, 1 );

		Log.v( "SRCAPK", "argv=" + argv );
		setArgs( argv );
	}
}
