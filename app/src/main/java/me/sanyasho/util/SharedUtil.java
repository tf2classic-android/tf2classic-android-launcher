package me.sanyasho.util;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class SharedUtil
{
	public static String TAG = "SharedUtil";

	public static void startBrowser(Context ctx, String url)
	{
		Intent browserIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
		ctx.startActivity( browserIntent );
	}

	public static boolean IsDeviceBrick( Context ctx )
	{
		ActivityManager actManager = ( ActivityManager ) ctx.getSystemService( ACTIVITY_SERVICE );
		assert actManager != null;

		ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
		actManager.getMemoryInfo( memInfo );

		double dTotalMemory = ( double ) ( memInfo.totalMem / ( 1024 * 1024 * 1024 ) );

		Log.d( TAG, "totalMemory: " + dTotalMemory );

		if( dTotalMemory < 2.0d ) // TF_MOD: 2 GB is minimal.
		{
			return true;
		}

		return false;
	}

	public static boolean IsDeviceTooOld( Context ctx )
	{
		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.O ) // Minimal is Android 8.0
		{
			return true;
		}

		return false;
	}

	public static String prepareArgv( String oldargv )
	{
		boolean bHasForceVendorID = oldargv.contains( "-force_vendor_id" );
		boolean bHasForceDeviceID = oldargv.contains( "-force_device_id" );

		StringBuilder sb = new StringBuilder();

		// SanyaSho: force this args to command line
		if( !bHasForceVendorID )
		{
			Log.d( TAG, "force_vendor_id argv is missing, appending." );
			sb.append( "-force_vendor_id 0x10DE" ).append( " " );
		}
		if( !bHasForceDeviceID )
		{
			Log.d( TAG, "force_device_id argv is missing, appending." );
			sb.append( "-force_device_id 0x1180" ).append( " " );
		}

		String[] oldargvarr = oldargv.split( " " );
		for( String str : oldargvarr )
			sb.append( str ).append( " " );

		String newargv = sb.toString();
		Log.d( TAG, String.format( "prepareArgv( %s ): %s", oldargv, newargv ) );
		return newargv;
	}
}
