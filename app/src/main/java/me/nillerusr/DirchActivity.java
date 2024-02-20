package me.nillerusr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.sanyasho.tf2classic.R;

public class DirchActivity extends Activity implements OnTouchListener
{
	public static String cur_dir;
	static LinearLayout body;
	public SharedPreferences mPref;

	@Override
	public boolean onTouch( View v, MotionEvent event )
	{
		if( event.getAction() == MotionEvent.ACTION_UP )
		{
			TextView btn = v.findViewById( R.id.dirname );
			if( cur_dir == null )
				ListDirectory( "" + btn.getText() );
			else
				ListDirectory( cur_dir + "/" + btn.getText() );
		}
		return false;
	}

	public void ListDirectory( String path )
	{
		TextView header = findViewById( R.id.header_txt );
		File myDirectory = new File( path );

		File[] directories = myDirectory.listFiles( File::isDirectory );

		if( directories != null && directories.length > 1 )
		{
			Arrays.sort( directories, Comparator.comparing( object -> object.getName().toUpperCase() ) );
		}

		LayoutInflater ltInflater = getLayoutInflater();
		if( directories == null )
			return;

		try
		{
			cur_dir = myDirectory.getCanonicalPath();
			header.setText( cur_dir );
		}
		catch( IOException e )
		{
		}

		body.removeAllViews();
		View view = ltInflater.inflate( R.layout.directory, body, false );
		TextView txt = view.findViewById( R.id.dirname );
		txt.setText( ".." );
		body.addView( view );
		view.setOnTouchListener( this );

		for( File dir : directories )
		{
			view = ltInflater.inflate( R.layout.directory, body, false );
			txt = view.findViewById( R.id.dirname );
			txt.setText( dir.getName() );
			body.addView( view );
			view.setOnTouchListener( this );
		}
	}

	public List< String > getExtStoragePaths()
	{
		List< String > list = new ArrayList<>();
		File fileList[] = new File( "/storage/" ).listFiles();
		if( fileList == null )
			return list;

		for( File file : fileList )
		{
			if( !file.getAbsolutePath().equalsIgnoreCase( Environment.getExternalStorageDirectory().getAbsolutePath() ) && file.isDirectory() && file.canRead() )
				list.add( file.getAbsolutePath() );
		}
		return list;
	}

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		mPref = getSharedPreferences( "mod", 0 );
		requestWindowFeature( 1 );

		super.setTheme( 16973829 );

		setContentView( R.layout.activity_directory_choice );
		cur_dir = null;
		body = findViewById( R.id.bodych );
		TextView header = findViewById( R.id.header_txt );
		header.setText( "" );

		Button button = findViewById( R.id.button_choice );
		button.setOnClickListener( v ->
		{
			if( cur_dir != null )
			{
				if( LauncherActivity.GamePath != null )
					LauncherActivity.GamePath.setText( cur_dir + "/" );
				SharedPreferences.Editor editor = mPref.edit();
				editor.putString( "gamepath", cur_dir + "/" );
				editor.commit();
				finish();
			}
		} );

		List< String > l = getExtStoragePaths();
		if( l == null || l.isEmpty() )
		{
			ListDirectory( LauncherActivity.getDefaultDir() );
			return;
		}

		LayoutInflater ltInflater = getLayoutInflater();
		View view = ltInflater.inflate( R.layout.directory, body, false );
		TextView txt = view.findViewById( R.id.dirname );
		txt.setText( LauncherActivity.getDefaultDir() );
		body.addView( view );
		view.setOnTouchListener( this );

		for( String dir : l )
		{
			view = ltInflater.inflate( R.layout.directory, body, false );
			txt = view.findViewById( R.id.dirname );
			txt.setText( dir );
			body.addView( view );
			view.setOnTouchListener( this );
		}
	}
}
