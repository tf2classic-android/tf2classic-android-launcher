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

// Release CertCheck.java

import java.security.MessageDigest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

public class CertCheck
{
	private static String TAG = "CertCheck";
	private static String APK_KEY = "";

	public static boolean IsDebugBuild()
	{
		return false;
	}

	public static String getApkKey()
	{
		return APK_KEY;
	}

	public static boolean dumbAntiMoronCheck( Context context )
	{
		// Certificate checking
		if( dumbCertificateCheck( context, context.getPackageName(), "4ZAs8ABJX7mIVC6DAPxT0B2Rh04=", false ) )
		{
			Log.e( TAG, "Please, don't resign our public release builds!" );
			return true;
		}

		return false;
	}

	public static boolean dumbCertificateCheck( Context context, String pkgName, @NonNull String sig, boolean failIfNoPkg )
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

				APK_KEY = curSIG;

				//Log.d( TAG, "Signature: " + curSIG );

				if( sig.equals( curSIG ) )
				{
					Log.d( TAG, "Found valid cert" );
					return false;
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
