package me.nillerusr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.libsdl.app.SDLActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import me.sanyasho.tf2classic.R;
import su.xash.fwgslib.CertCheck;

public class LauncherActivity extends Activity
{
	public static String TAG = "LauncherActivity";

	public static String MOD_NAME = "tf2classic";
	public static String PKG_NAME;

	static EditText cmdArgs = null, GamePath = null;
	public SharedPreferences mPref;

	final static int REQUEST_PERMISSIONS = 42;

	public void applyPermissions( final String[] permissions, final int code )
	{
		List< String > requestPermissions = new ArrayList<>();
		for( String permission : permissions )
		{
			if( checkSelfPermission( permission ) != PackageManager.PERMISSION_GRANTED )
				requestPermissions.add( permission );
		}

		if( !requestPermissions.isEmpty() )
		{
			String[] requestPermissionsArray = new String[ requestPermissions.size() ];
			for( int i = 0; i < requestPermissions.size(); i++ )
				requestPermissionsArray[ i ] = requestPermissions.get( i );
			requestPermissions( requestPermissionsArray, code );
		}
	}

	public void onRequestPermissionsResult( int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults )
	{
		if( requestCode == REQUEST_PERMISSIONS )
		{
			if( grantResults[ 0 ] == PackageManager.PERMISSION_DENIED )
			{
				Toast.makeText( this, R.string.srceng_launcher_error_no_permission, Toast.LENGTH_LONG ).show();
				finish();
			}
		}
	}

	public static String getDefaultDir()
	{
		File dir = Environment.getExternalStorageDirectory();
		if( dir == null || !dir.exists() )
			return "/sdcard/";
		return dir.getPath();
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

	private void startBrowser( String url )
	{
		Intent browserIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
		startActivity( browserIntent );
	}

	private boolean IsDeviceBrick()
	{
		ActivityManager actManager = ( ActivityManager ) getSystemService( ACTIVITY_SERVICE );
		assert actManager != null;

		ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
		actManager.getMemoryInfo( memInfo );

		double dTotalMemory = ( double ) ( memInfo.totalMem / ( 1024 * 1024 * 1024 ) );

		Log.d( TAG, "totalMemory: " + dTotalMemory );

		if( dTotalMemory <= 2.7d ) // +-3GB
		{
			return true;
		}

		return false;
	}

	private boolean IsDeviceTooOld()
	{
		if( Build.VERSION.SDK_INT < Build.VERSION_CODES.O ) // Minimal is Android 8.0
		{
			return true;
		}

		return false;
	}

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		PKG_NAME = getApplication().getPackageName();
		requestWindowFeature( 1 );

		super.setTheme( 0x01030224 );

		if( CertCheck.dumbAntiMoronCheck( this ) )
		{
			Toast.makeText( this, "Buckle the fuck up little doggy", Toast.LENGTH_LONG ).show();

			finish();
			return;
		}

		mPref = getSharedPreferences( "mod", 0 );

		setContentView( R.layout.activity_launcher );

		cmdArgs = findViewById( R.id.edit_cmdline );
		GamePath = findViewById( R.id.edit_gamepath );

		Button button = findViewById( R.id.button_launch );
		if( IsDeviceBrick() || IsDeviceTooOld() )
		{
			button.setEnabled( false );

			new AlertDialog.Builder( this )
				.setTitle( R.string.srceng_launcher_error )
				.setMessage( R.string.tf2classic_bad_device )
				.setPositiveButton( R.string.srceng_launcher_ok, null )
				.show();
		}
		else
		{
			button.setOnClickListener( LauncherActivity.this::startSource );
		}

		Button aboutButton = findViewById( R.id.button_about );
		aboutButton.setOnClickListener( this::aboutEngine );

		Button dirButton = findViewById( R.id.button_gamedir );
		dirButton.setOnClickListener( v ->
		{
			Intent intent = new Intent( LauncherActivity.this, DirchActivity.class );
			intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			startActivity( intent );
		} );

		Button getHL2Button = findViewById( R.id.get_hl2 );
		getHL2Button.setOnClickListener( v ->
		{
			startBrowser( "https://store.steampowered.com/app/320" ); // Half-Life 2: Deathmatch store page
		} );

		Button getTF2CButton = findViewById( R.id.get_tf2c );
		getTF2CButton.setOnClickListener( v ->
		{
			startBrowser( getString( R.string.tf2c_download_url ) );
		} );

		cmdArgs.setText( mPref.getString( "argv", getString( R.string.default_commandline_arguments ) ) );
		GamePath.setText( mPref.getString( "gamepath", getDefaultDir() + "/srceng_tf2classic" ) );

		// permissions check
		applyPermissions( new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO }, REQUEST_PERMISSIONS );

		boolean isCommitEmpty = getResources().getString( R.string.current_commit ).isEmpty();
		boolean isURLEmpty = getResources().getString( R.string.update_url ).isEmpty();

		if( !isCommitEmpty && !isURLEmpty )
		{
			UpdateSystem upd = new UpdateSystem( this );
			upd.execute();
		}
	}

	public void aboutEngine( View view )
	{
		final Activity a = this;
		this.runOnUiThread( () ->
		{
			final Dialog dialog = new Dialog( a );
			dialog.requestWindowFeature( Window.FEATURE_NO_TITLE ); // hide the dialog title
			dialog.setContentView( R.layout.about );
			dialog.setCancelable( true );
			dialog.show();

			TextView Links = dialog.findViewById( R.id.about_links );
			Links.setMovementMethod( LinkMovementMethod.getInstance() );

			dialog.findViewById( R.id.about_button_ok ).setOnClickListener( v -> dialog.cancel() );

		} );
	}

	public void saveSettings( SharedPreferences.Editor editor )
	{
		String argv = prepareArgv( cmdArgs.getText().toString() );
		String gamepath = GamePath.getText().toString();

		editor.putString( "argv", argv );
		editor.putString( "gamepath", gamepath );
		editor.commit();
	}

	private Intent prepareIntent( Intent i )
	{
		String argv = prepareArgv( cmdArgs.getText().toString() );
		i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		saveSettings( mPref.edit() );

		// SanyaSho: add -force_hardware_id here
		String APKKEY = CertCheck.getApkKey();
		if( !APKKEY.isEmpty() )
		{
			argv = argv + "-force_hardware_id " + APKKEY + " ";
		}

		if( argv.length() != 0 )
			i.putExtra( "argv", argv );

		i.putExtra( "gamedir", MOD_NAME );

		return i;
	}

	public void startSource( View view )
	{
		String argv = prepareArgv( cmdArgs.getText().toString() );
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString( "argv", argv );

		if( argv.contains( "-game" ) )
		{
			new AlertDialog.Builder( this )
					.setTitle( R.string.srceng_launcher_error )
					.setMessage( R.string.tf2classic_game_check )
					.setPositiveButton( R.string.srceng_launcher_ok, null )
					.show();

			return;
		}

		saveSettings( editor );

		Intent intent = new Intent( LauncherActivity.this, SDLActivity.class );
		intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent = prepareIntent( intent );
		startActivity( intent );
	}

	public void onPause()
	{
		saveSettings( mPref.edit() );
		super.onPause();
	}
}
