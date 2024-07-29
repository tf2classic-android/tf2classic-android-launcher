package me.nillerusr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import me.sanyasho.tf_mod.R;

public class UpdateSystem extends AsyncTask< String, Integer, String >
{
	public static String TAG = "UpdateSystem";
	static String git_url, apk_url;
	Context mContext;
	String commit;

	public UpdateSystem( Context context )
	{
		mContext = context; // save application context
		commit = context.getResources().getString( R.string.current_commit );

		git_url = context.getResources().getString( R.string.update_url );
		apk_url = "http://127.0.0.1/";
	}

	private static String toString( InputStream inputStream )
	{
		String[] update;
		try
		{
			BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( inputStream, StandardCharsets.UTF_8 ) );
			String inputLine;
			StringBuilder stringBuilder = new StringBuilder();
			while( ( inputLine = bufferedReader.readLine() ) != null )
			{
				stringBuilder.append( inputLine );
			}
			update = stringBuilder.toString().split( ":", 2 );

			apk_url = update[ 1 ]; // url
			return update[ 0 ]; // commit
		}
		catch( Exception ex )
		{
			Log.e( TAG, String.format( "Failed to fetch update list! (Exception: %s)", ex ) );
		}

		return "";
	}

	@Override
	protected String doInBackground( String... params )
	{
		URL urlObject;
		URLConnection urlConnection;

		try
		{
			urlObject = new URL( git_url );
			urlConnection = urlObject.openConnection();

			String web_commit = toString( urlConnection.getInputStream() );

			Log.d( TAG, String.format( "%s | %s", web_commit, commit ) );

			return web_commit;
		}
		catch( IOException ignored )
		{
		}

		return null;
	}

	protected void onPostExecute( String result )
	{
		if( result != null && !result.equals( "" ) && !commit.equals( result ) )
		{
			Intent notif = new Intent( mContext, UpdateService.class );
			notif.putExtra( "url", apk_url );
			mContext.startService( notif );
		}
	}
}
