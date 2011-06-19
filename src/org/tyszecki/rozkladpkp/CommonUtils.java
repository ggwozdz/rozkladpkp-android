/*******************************************************************************
 * This file is part of the RozkladPKP project.
 * 
 *     RozkladPKP is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     RozkladPKP is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License 
 *     along with RozkladPKP.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.tyszecki.rozkladpkp;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CommonUtils {
	
	/*
	 * Sprawdza czy aplikacja ma dostęp do internetu, pokazuje Toast z błędem, jeśli nie ma.
	 */
	public static boolean onlineCheck(Context c)
	{
		return onlineCheck(c, "Nie można wykonać tej operacji - brak połączenia internetowego.");
	}
	
	public static boolean onlineCheck(Context c, String msgError)
	{
		ConnectivityManager cm = (ConnectivityManager)  c.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }

	    Toast.makeText(c.getApplicationContext(), msgError, Toast.LENGTH_SHORT).show();
	    return false;
	}
	
	/*
	 * Zwraca ID drawable'a który odpowiada typowi pociągu, którego numer podano w parametrze
	 */
	
	private static final HashMap<String, Integer> typeDrawables = new HashMap<String, Integer>(){
		
		private static final long serialVersionUID = 1L;
		{
			put("IR", R.drawable.back_ir);
			put("RE", R.drawable.back_re);
			
			for(String a : new String[]{"Fußweg","Übergang"})
				put(a,R.drawable.back_foot);
			
			for(String a : new String[]{"TGV","ES","KDP"}) //;)
				put(a,R.drawable.back_kdp);
			
			for(String a : new String[]{"TLK","D"})
				put(a,R.drawable.back_tlk);
			
			for(String a : new String[]{"EC","EIC", "EN", "EX"})
				put(a,R.drawable.back_ec);
			
			for(String a : new String[]{"SKM","SKW", "WKD"})
				put(a,R.drawable.back_skm);	
			
			for(String a : new String[]{"Bus","Tra", "Metro"})
				put(a,R.drawable.back_bus);
		}
	};
	
	public static int drawableForTrainType(String t)
	{
         if(t != null && t.length() > 0 && typeDrawables.containsKey(t))
        	 return typeDrawables.get(t);
         else
        	 return R.drawable.back_reg;	
	}
	
	public static String trainType(String number)
	{
		if(number.equals("Fußweg") || number.equals("Übergang"))
			return number;
		
        Matcher m = Pattern.compile("([a-zA-Z]*)").matcher(number);   
        return m.find() ? m.group(1) : null;
	}
	
	/*
	 * Metoda zwraca tekst jaki zostanie wyświetlony jako typ pociągu.
	 * Przydaje się do zamiany niemieckiego "Fussweg" i "Ubergang" i usuwania podwojonych spacji.
	 */
	public static String trainDisplayName(String number)
	{
		if(number.equals("Fußweg"))
			return "Pieszo";
		if(number.equals("Übergang"))
			return "Przejście";
		
		else return number.replaceAll("\\s+", " ");
	}
	
	/*
	 * Zwraca miejscowość, na podstawie pamiętanej przez urządzenie lokalizacji,
	 * podczas operacji, pokazuje wiadomość o postępie.
	 * Przekazywanie aktywności może nie jest najelegantsze, ale w obecnym wypadku,
	 * jest to sensowne rozwiązanie.
	 */
	public static void currentLocality(final Activity cx, final LocationResult callback)
	{
		Resources res = cx.getResources();
		final ProgressDialog p = ProgressDialog.show(cx, res.getString(R.string.progressTitle), res.getString(R.string.progressBodyLocation));
		new Thread( new Runnable() {
			
			@Override
			public void run() {
				LocationManager lm = (LocationManager) cx.getSystemService(Context.LOCATION_SERVICE);
				Location l = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				Geocoder c = new Geocoder(cx);
				try {
					List<Address> addresses = c.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
					callback.gotLocality(addresses.get(0).getLocality());
				} catch (Exception e) {
					callback.gotLocality(null);
				} 
				//TODO: Czy to wywołanie może w ogóle zawieść? 
				cx.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
							p.dismiss();
					}
				});
			}
		}).start();
	}
	
	public static abstract class LocationResult{
        public abstract void gotLocality(String s);
    }
	
	// http://stackoverflow.com/questions/2833474/how-to-toggle-orientation-lock-in-android
	public static void setActivityOrientation(Activity activity, int preferenceOrientation) {
	    if (preferenceOrientation == Configuration.ORIENTATION_LANDSCAPE) { 
	        if( activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){ 
	        // You need to check if your desired orientation isn't already set because setting orientation restarts your Activity which takes long
	            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	        }
	    } else if (preferenceOrientation == Configuration.ORIENTATION_PORTRAIT) {
	        if( activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
	            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	        }    
	    } else {
	        if( activity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR){
	            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	        }
	    }
	}
	
	public static String StationIDfromSID(String ID)
	{
		for(String t: ID.split("@"))
			if(t.startsWith("L="))
				return t.split("=")[1];
		return null;
	}
	
	public static String SIDfromStationID(int ID, String name)
	{
		return "A=1@O="+name+"@L="+Integer.toString(ID)+"@";
	}
	
	//Metoda używana do wygenerowania nazwy pliku z wynikami
	public static String ResultsHash(String stationFrom, String stationTo, Boolean departure)
	{
		StringBuilder b = new StringBuilder();
		
		b.append((stationFrom != null) ? stationFrom : "");
		b.append((stationTo != null) ? stationTo : "");
		b.append((departure != null) ? ((departure) ? "-1" : "-2") : "");
		
		return b.toString();
	}
}
