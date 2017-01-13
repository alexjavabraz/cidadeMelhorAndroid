package br.com.cidademelhor.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Base64;

public class UtilFunction {
	
	private static final String EMPTY_STRING = "";
	public static final String SERVIDOR      =  "http://www.melhorarminhacidade.com.br:8080/"; //"http://www.cotiamelhor.com.br/"; //
	public static final String PARAMETRO_ID_DO_CLIENTE_LOGADO = "ID_CLIENTE";
	public static final String PARAMETRO_CHAVE_APLICACAO = "KEY_APLICACAO";
	public static final String MENSAGEM_CHAVE_INVALIDA = "Chave Invalida";
	public static final String PARAMETRO_USUARIO = "PARAMETRO_USUARIO_OBJECT";
	public static final String MENSAGEM_SENHA_RECUPERADA_COM_SUCESSO = "Email enviado com sucesso";
	public static int timeoutConnection      = 30000;
	public static int timeoutSocket          = 80000;
	
	public static byte[] decodeImage(String imageDataString) {
        return Base64.decode(imageDataString, 0);
    }
	
	public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeToString(imageByteArray, 0);
    }
	
    public static boolean isBlankOrNull(Object o) {
        if (o == null) {
            return true;
        }

        if (EMPTY_STRING.equals(o.toString())) {
            return true;
        }

        return false;
    }

    public static boolean isBlankZeroOrNull(Object o) {
        if (o == null) {
            return true;
        }

        if (o instanceof Number) {

            if ((((Number) o)).intValue() == 0) {
                return true;
            }

        }

        if (EMPTY_STRING.equals(o.toString())) {
            return true;
        }

        return false;
    }

	@SuppressLint("SimpleDateFormat")
	public static String getImageName() {
		return new SimpleDateFormat("dd-MM-yyyy-mm-ss").format(new Date());
	}
	

    public static String encriptBase64(String criptoString)  {
        String encript = null;

        try {
            encript = new String(Base64.encodeToString(criptoString.getBytes(), Base64.NO_WRAP));
        }
        catch (Exception e) {
            e.printStackTrace();
        }


        return encript;
    }

    @SuppressLint("SimpleDateFormat")
	public static String getDataHojeString() {
		return new SimpleDateFormat("dd-MM-yyyy-mm-ss").format(new Date());
	}
    
    /**
	 * Location
	 */
	
	private static final int ONE_MINUTES = 1000 * 60 * 1;
	
    /** Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	  */
	public static boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > ONE_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -ONE_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}
	
	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

}
