package su.xash.fwgslib;

import android.content.*;
import android.util.*;
import android.content.pm.*;

import androidx.annotation.NonNull;

import java.lang.*;
import java.security.MessageDigest;

public class CertCheck
{
	private static String TAG = "CertCheck";

	public static boolean dumbAntiMoronCheck( Context context )
	{
		// SanyaSho: disable that for staging builds.
		if( true )
			return false;

		// Check for old a1batross keystore
		if( dumbCertificateCheck( context, context.getPackageName(), new String[] { "aIjIyg/z8WCSWWq2RurEGz9yV4g=" }, false ) )
		{
			Log.e( TAG, "Please, don't sign release builds with old a1batross keystore!" );
			return true;
		}

		// Certificate checking
		if( dumbCertificateCheck( context, context.getPackageName(), new String[] { "4ZAs8ABJX7mIVC6DAPxT0B2Rh04=" }, false ) )
		{
			Log.e( TAG, "Please, don't resign our public release builds!" );
			return true;
		}

		return false;
	}

	public static boolean dumbCertificateCheck( Context context, String pkgName, @NonNull String[] sig, boolean failIfNoPkg )
	{
		Log.d( TAG, "pkgName = " + pkgName );
		try
		{
			PackageInfo info = context.getPackageManager().getPackageInfo( pkgName, PackageManager.GET_SIGNATURES );

			for( Signature signature : info.signatures )
			{
				Log.d( TAG, "found signature" );
				MessageDigest md = MessageDigest.getInstance( "SHA" );
				final byte[] signatureBytes = signature.toByteArray();

				md.update( signatureBytes );

				final String curSIG = Base64.encodeToString( md.digest(), Base64.NO_WRAP );

				//if( XashConfig.isXashDebugBuild() )
				//	Log.d( "XASH3D_DEBUG", "Signature: " + curSIG );

				for( String signat : sig )
				{
					if( signat.equals( curSIG ) )
					{
						Log.d( TAG, "Found valid cert" );
						return false;
					}
				}
			}
		}
		catch( PackageManager.NameNotFoundException e )
		{
			Log.d( TAG, "Package not found" );

			e.printStackTrace();
			if( !failIfNoPkg )
				return false;
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}

		return true;
	}
}
