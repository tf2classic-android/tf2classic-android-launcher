package me.nillerusr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import me.sanyasho.tf_mod.R;

public class UpdateService extends Service
{
	public static String TAG = "UpdateService";
	static boolean service_work = false;
	NotificationManager nm;
	Bundle extras;

	@Override
	public void onCreate()
	{
		super.onCreate();
		nm = ( NotificationManager ) getSystemService( NOTIFICATION_SERVICE );
	}

	public int onStartCommand( Intent intent, int flags, int startId )
	{
		if( !service_work )
		{
			service_work = true;

			try
			{
				extras = intent.getExtras();
				sendNotif();
			}
			catch( Exception ignored )
			{
			}
		}

		return START_NOT_STICKY;
	}

	private void sendNotif()
	{
		Notification notif = new Notification( R.drawable.launcher_update, "UPDATE", System.currentTimeMillis() );
		notif.contentView = new RemoteViews( getPackageName(), R.layout.update_notify );

		Intent browserIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( extras.get( "url" ).toString() ) );

		notif.contentIntent = PendingIntent.getActivity( this, 0, browserIntent, PendingIntent.FLAG_IMMUTABLE );
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notif.defaults |= Notification.DEFAULT_LIGHTS; // LED
		notif.defaults |= Notification.DEFAULT_VIBRATE; // Vibration
		notif.defaults |= Notification.DEFAULT_SOUND; // Sound
		notif.priority |= Notification.PRIORITY_HIGH;

		nm.notify( 1, notif );
	}

	public IBinder onBind( Intent intent )
	{
		return null;
	}
}
