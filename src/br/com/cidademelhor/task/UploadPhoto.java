package br.com.cidademelhor.task;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import br.com.cidademelhor.util.UtilFunction;

public class UploadPhoto extends AsyncTask<String, Void, String> {
	private static final String SUFIXO_JPG = ".jpg";
	private static final String LOG_NAME = "UploadPhoto";
	private String latitude;
	private String longitude;
	private String titulo;
	private String descricao;
	private String endereco;
	
	public UploadPhoto() {
	}

	@Override
	protected String doInBackground(String... params) {

		try {
			disableConnectionReuseIfNecessary();
			HttpClient httpClient = null;
			String caminho        = (String) params[0];
			latitude              = params[1];
			longitude             = params[2];
			titulo                = params[3];
			descricao             = params[4];
			endereco              = params[5];
			send();
		} catch (Exception e) {
			Log.e(LOG_NAME, e.getMessage());
		}

		return "OK";
	}

	private String uploadDemanda(final String caminhoDaImagemNoDispositivo, HttpClient httpClient)
			throws ClientProtocolException, IOException {
		final String URL = UtilFunction.SERVIDOR + "rest/demanda/ins/nova/id/" + latitude	+ "/" + longitude + "/"+titulo+"/"+descricao+"/"+endereco;

		HttpPost postRequest = new HttpPost(URL);

		postRequest.setHeader("Content-Type", "application/octet-stream");

		File arquivo = new File(caminhoDaImagemNoDispositivo);
		Log.e(LOG_NAME, arquivo.getName());
		String nome = arquivo.getName();
		nome = nome.trim().replace(" ", "");

		HttpEntity reqEntity = new FileEntity(arquivo, "application/octet-stream"); // MultipartEntity
																					// HttpMultipartMode.BROWSER_COMPATIBLE
		postRequest.setEntity(reqEntity);

		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, UtilFunction.timeoutConnection);
		HttpConnectionParams.setSoTimeout(httpParameters, UtilFunction.timeoutConnection);
		httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = httpClient.execute(postRequest);

		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		String sResponse;
		StringBuilder s = new StringBuilder();

		while ((sResponse = reader.readLine()) != null) {
			s = s.append(sResponse);
		}

		Log.d(LOG_NAME, response.getStatusLine().toString());

		return s.toString();
	}

	private void uploadFotos(final Integer id, final String caminhoDaImagemNoDispositivo, HttpClient httpClient)
			throws ClientProtocolException, IOException {

		File arquivo     = new File(caminhoDaImagemNoDispositivo);
		String nome      = arquivo.getName();
		nome             = nome.trim().replace(" ", "");
		final String URL = UtilFunction.SERVIDOR + "/oss/rest/demanda/ins" + id + "/" + nome + "/uploadUmaFoto";

		Log.e(LOG_NAME, arquivo.getName());
		
		HttpEntity reqEntity  = new FileEntity(arquivo, "application/octet-stream"); // MultipartEntity
		HttpPost postRequest  = new HttpPost(URL);
		HttpResponse response = httpClient.execute(postRequest);
		
		postRequest.setEntity(reqEntity);

		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		String sResponse;
		StringBuilder s = new StringBuilder();

		while ((sResponse = reader.readLine()) != null) {
			s = s.append(sResponse);
		}

		Log.d(LOG_NAME, response.getStatusLine().toString());

	}

	private String toString(InputStream is) throws IOException {
		byte[] bytes = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int lidos;
		while ((lidos = is.read(bytes)) > 0) {
			baos.write(bytes, 0, lidos);
		}
		return new String(baos.toByteArray());
	}

	private String getResponseText(InputStream inStream) {
		return new Scanner(inStream).useDelimiter("\\A").next();
	}

	private void disableConnectionReuseIfNecessary() {
		if ((Build.VERSION.SDK_INT) < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}
	
	@Override
	protected void onProgressUpdate(Void... values) {
		Log.i(LOG_NAME, "onProgressUpdate");
		super.onProgressUpdate(values);
	}	

	@Override
	protected void onCancelled(String result) {
		super.onCancelled(result);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(String result) {
		Log.i(LOG_NAME, "onPostExecute");
		super.onPostExecute(result);
	}
	
	public void send(){
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);

		nameValuePairs.add(new BasicNameValuePair("titulo",    titulo));  
		nameValuePairs.add(new BasicNameValuePair("descricao", descricao)); 
		nameValuePairs.add(new BasicNameValuePair("endereco",  endereco));
		nameValuePairs.add(new BasicNameValuePair("latitude",  latitude));
		nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
		
		String res = "";
		try {
			
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, UtilFunction.timeoutConnection);
			HttpConnectionParams.setSoTimeout(httpParameters, UtilFunction.timeoutConnection);
			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			HttpPost httppost = new HttpPost( UtilFunction.SERVIDOR + "rest/demanda/ins");

			// Add your data

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			res = EntityUtils.toString(response.getEntity());
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}// END
