package br.com.cidademelhor.tasks;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import br.com.cidademelhor.FullscreenActivity;
import br.com.cidademelhor.MenuActivity;
import br.com.cidademelhor.util.UtilFunction;

/**
 * Responsavel por obter alguma validação do lado do servidor e iniciar a aplicação
 * 
 * @author asimas
 *
 */
public class GetKeyTaskInitializer extends AsyncTask   <String, Void, String> {
	
    private FullscreenActivity contexto;
    private static final String LOG_NAME = "GetKeyTask";
    private static final String RETORNO = "OK";
    
    public GetKeyTaskInitializer(FullscreenActivity context){
    	contexto = context;
    }
	
    @Override
    protected void onPreExecute() {
        Log.i(LOG_NAME, "onPreExecute");
    }
    
    /**
     * Envia para a página de MENU APÓS O SPLASH SCREEN 
     */
    @Override
    protected void onPostExecute(String result) {
        Log.i(LOG_NAME, "onPostExecute");
        Intent i = new Intent(contexto, MenuActivity.class);
        i.putExtra(UtilFunction.PARAMETRO_CHAVE_APLICACAO, RETORNO);
        contexto.startActivity(i);
        contexto.finish();
    }	    

    @Override
    protected void onProgressUpdate(Void... values) {
        Log.i(LOG_NAME, "onProgressUpdate");
    }

	@Override
	protected String doInBackground(String... params) {
		return RETORNO;
	}

}