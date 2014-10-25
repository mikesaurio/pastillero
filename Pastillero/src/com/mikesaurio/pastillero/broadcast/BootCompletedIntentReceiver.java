package com.mikesaurio.pastillero.broadcast;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mikesaurio.pastillero.servicio.servicio_alarma;

 
public class BootCompletedIntentReceiver extends BroadcastReceiver {
 @Override
 public void onReceive(Context context, Intent intent) {

     /***** For start Service  ****/
      Intent myIntent = new Intent(context, servicio_alarma.class);
      context.startService(myIntent);
 
 }
}