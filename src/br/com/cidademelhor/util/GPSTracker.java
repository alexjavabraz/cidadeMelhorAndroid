package br.com.cidademelhor.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import br.com.cidademelhor.R;

public class GPSTracker extends Service implements LocationListener{
	
	private Context mContext;
	private boolean isGPSEnabled = false;
	private boolean isNetworkEnabled = false;
	private boolean canGetLocation = false;
	private Location location;
	private Location location2;
	private double latitude;
	private double longitude;
	
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 METROS
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 MINUTO
	
	private LocationManager locationManager;
	
	public GPSTracker(Context contexto){
		mContext = contexto;
//		getLocation();
	}
	
	
	public Location getLocation(){
		
		try{
			
			locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
			// Acquire a reference to the system Location Manager
			
			boolean isGPSEnabled     = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
			if(!isGPSEnabled && !isNetworkEnabled){
				
				mostraMensagemConfiguracao();
				
			}else{
		
				if(isNetworkEnabled){
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					
					location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					
					if(location != null){
						latitude = location.getLatitude();
						longitude = location.getLongitude();
					}
				}
				
				if(isGPSEnabled){
					
					if(location == null){
						locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						location2 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						
						if(location != null){
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
						
					}
					
				}
				
				if(location != null && location2 != null){
					boolean locationIsBetter = UtilFunction.isBetterLocation(location, location2);
					boolean location2IsBetter = UtilFunction.isBetterLocation(location2, location);
					
					if(location2IsBetter){
						location  = location2;
						latitude  = location.getLatitude();
						longitude = location.getLongitude();
					}
				}
				
//				// Define a listener that responds to location updates
				LocationListener locationListener = new LocationListener() {
				    public void onLocationChanged(Location alocation) {
				      // Called when a new location is found by the network location provider.
				      location = alocation;
				    }
			
				    public void onStatusChanged(String provider, int status, Bundle extras) {}
			
				    public void onProviderEnabled(String provider) {}
			
				    public void onProviderDisabled(String provider) {}
				  };
			
				// Register the listener with the Location Manager to receive location updates
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		
//				location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//				
//				if(location == null){
//					location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//				}
			
				
//				mostraMensagem();
				
			}
			
			
		}catch(Exception e){
			
		}
		
		
		return location;
		
	}
	
	public void mostraMensagem(){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		
		builder.setMessage("Location latitude = " + location.getLatitude() + " longitude = " + location.getLongitude())
        .setPositiveButton(R.string.txt_sim, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        })
        .setNegativeButton(R.string.txt_nao, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
		
		 Dialog desejaSair = builder.create();
		 desejaSair.show();
	}


	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	public double getLatitude() {
		return latitude;
	}


	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	public double getLongitude() {
		return longitude;
	}


	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}


	public void setLocation(Location location) {
		this.location = location;
	}
	
	public void mostraMensagemConfiguracao(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		alertDialog.setTitle("Precisamos que você nos forneça sua localização atual.");
		
		alertDialog.setPositiveButton("Configurações", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				mContext.startActivity(intent);
			}
		});
		
		alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				
			}
		});
		
		alertDialog.show();
	}
	
	
	

}
