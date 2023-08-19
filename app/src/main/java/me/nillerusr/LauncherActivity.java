package me.nillerusr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import me.sanyasho.tf2classic.R;

public class LauncherActivity extends Activity
{
	public static String forced_argv = " -force_vendor_id 0x10DE -force_device_id 0x1180 ";
	public static String TAG = "LauncherActivity";

	public static String MOD_NAME = "tf2classic";
	public static String PKG_NAME;

	static EditText cmdArgs;
	public static SharedPreferences mPref;

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		PKG_NAME = getApplication().getPackageName();
		requestWindowFeature( 1 );

		super.setTheme( 0x01030224 );

		mPref = getSharedPreferences( "mod", 0 );

		setContentView( R.layout.activity_launcher );

		cmdArgs = findViewById( R.id.edit_cmdline );

		Button button = findViewById( R.id.button_launch );
		button.setOnClickListener( LauncherActivity.this::startSource );

		Button aboutButton = findViewById( R.id.button_about );
		aboutButton.setOnClickListener( v ->
		{
			Dialog dialog = new Dialog( LauncherActivity.this );
			dialog.setTitle( R.string.srceng_launcher_about_title );
			ScrollView scroll = new ScrollView( LauncherActivity.this );
			scroll.setLayoutParams( new LayoutParams( LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT ) );
			scroll.setPadding( 5, 5, 5, 5 );
			TextView text = new TextView( LauncherActivity.this );
			text.setText( R.string.srceng_launcher_about_text );
			text.setLinksClickable( true );
			text.setTextIsSelectable( true );
			Linkify.addLinks( text, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES );
			scroll.addView( text );
			dialog.setContentView( scroll );
			dialog.show();
		} );

		cmdArgs.setText( mPref.getString( "argv", getString( R.string.default_commandline_arguments ) ) );

		boolean isCommitEmpty = getResources().getString( R.string.current_commit ).isEmpty();
		boolean isURLEmpty = getResources().getString( R.string.update_url ).isEmpty();

		if( !isCommitEmpty && !isURLEmpty )
		{
			UpdateSystem upd = new UpdateSystem( this );
			upd.execute();
		}
	}

	public void saveSettings( SharedPreferences.Editor editor )
	{
		String argv = cmdArgs.getText().toString();

		editor.putString( "argv", argv + forced_argv );
		editor.commit();
	}

	private Intent prepareIntent( Intent i )
	{
		String argv = cmdArgs.getText().toString();
		i.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		saveSettings( mPref.edit() );

		if( argv.length() != 0 )
			i.putExtra( "argv", argv + forced_argv );

		i.putExtra( "gamedir", MOD_NAME );
		i.putExtra( "gamelibdir", getApplicationInfo().nativeLibraryDir );
		i.putExtra( "vpk", getFilesDir().getPath() + "/" + ExtractAssets.VPK_NAME );

		return i;
	}

	public void startSource( View view )
	{
		String argv = cmdArgs.getText().toString();
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString( "argv", argv + forced_argv );

		ExtractAssets.extractAssets( this );

		try
		{
			Intent intent = new Intent();
			intent.setComponent( new ComponentName( "com.valvesoftware.source", "org.libsdl.app.SDLActivity" ) );
			intent = prepareIntent( intent );
			startActivity( intent );
			return;
		}
		catch( Exception ignored )
		{
		}

		new AlertDialog.Builder( this ).setTitle( R.string.srceng_launcher_error ).setMessage( R.string.source_engine_fatal ).setPositiveButton( R.string.srceng_launcher_ok, ( DialogInterface.OnClickListener ) null ).show();
	}

	public void onPause()
	{
		saveSettings( mPref.edit() );
		super.onPause();
	}
}
