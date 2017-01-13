package br.com.cidademelhor;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import br.com.cidademelhor.MenuActivity.PlaceholderFragmentPhoto;
import br.com.cidademelhor.direct.AlbumStorageDirFactory;
import br.com.cidademelhor.direct.BaseAlbumDirFactory;
import br.com.cidademelhor.direct.FroyoAlbumDirFactory;
import br.com.cidademelhor.entity.Usuario;
import br.com.cidademelhor.task.UploadPhoto;
import br.com.cidademelhor.util.GPSTracker;
import br.com.cidademelhor.util.UtilFunction;

public class PhotoActivity extends Fragment implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	
	private static final String JPEG_FILE_PREFIX           = "IMG_";
	private static final String JPEG_FILE_SUFFIX           = ".jpg";
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private String mCurrentPhotoPath;

	/**
	 * Activities 
	 */
	private static final int ACTION_TAKE_PHOTO_B           = 1;
	private static final int ACTION_GET_PHOTO_FROM_GALLERY = 2;
	
	/**
     * Define the number of items visible when the carousel is first shown.
     */
    private static final float INITIAL_ITEMS_COUNT = 3.5F;
    
    private static final String LOG_NAME = "PhotoActivity";
    
//    private ProgressBar pg;
//    private ProgressDialog dialog = null;
    
    private int imageWidth = 0;
    
    /**
     * Carousel container layout
     */
    private LinearLayout mCarouselContainer;
    
    android.graphics.Bitmap oBitmap;
    
    private String ID_CLIENTE;
    private String CHAVE_APLICACAO;
    private ImageButton botaoUpload = null;
    private Object[] caminhoDasFotosNoDispositivo = null;
    private Usuario usuario;
    private Location location;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//	    FragmentActivity faActivity  = (FragmentActivity) super.getActivity();
	    final LinearLayout llLayout        = (LinearLayout)     inflater.inflate(R.layout.activity_photo, container, false);
	    final EditText endereco                     = (EditText)         llLayout.findViewById(R.id.endereco);
	    final ImageButton b                = (ImageButton)      llLayout.findViewById(R.id.btAbrirCamera);
		
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				abreCamera(ACTION_TAKE_PHOTO_B);
			}
		});
		
		final EditText titulo      = (EditText) llLayout.findViewById(R.id.titulo);
		final EditText descricao   = (EditText) llLayout.findViewById(R.id.descricao);
		botaoUpload = (ImageButton) llLayout.findViewById(R.id.photo_bt_upload);
//		botaoUpload.setEnabled(Boolean.FALSE);
		botaoUpload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
						final String URL = UtilFunction.SERVIDOR+"rest/demanda/ins/nova/id/"+String.valueOf(location.getLatitude())+"/"+String.valueOf(location.getLongitude())+"/"+titulo.getText().toString()+"/"+descricao.getText().toString()+"/"+endereco.getText().toString();
						
						Toast.makeText(PhotoActivity.this.getContext(), URL, Toast.LENGTH_LONG).show();
						
//						alerta = ProgressDialog.show(getContext(), "Estamos enviando para os responsáveis",   "Por favor aguarde um momento...", true);
						
						new UploadPhoto().execute(new String[]{mCurrentPhotoPath, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), titulo.getText().toString(), descricao.getText().toString(), endereco.getText().toString()});
						
//						alerta.dismiss();
						mCarouselContainer.removeAllViews();
//						pg.setVisibility(View.INVISIBLE);

				} catch (Exception e) {
					Log.d(LOG_NAME, "Eror" + e.getLocalizedMessage());
//					pg.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		final ImageButton botaoChamarGallery = (ImageButton) llLayout.findViewById(R.id.photo_bt_chamar_gallery);
		botaoChamarGallery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				pg.setVisibility(View.VISIBLE);
//				dialog.setTitle("Carregando suas fotos...");
//				dialog.setMessage("Por favor aguarde um momento...");
//				dialog.show();
				abreGaleria();
//				dialog.dismiss();
			}
			
		});
		
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		
//		try{
//			logaDiretorioImagens();
//		}catch(Exception e){
//			Log.e(LOG_NAME, e.getLocalizedMessage());
//		}
	    
		final DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imageWidth = (int) (displayMetrics.widthPixels / INITIAL_ITEMS_COUNT);

        mCarouselContainer = (LinearLayout) llLayout.findViewById(R.id.carousel);
//		pg = (ProgressBar) llLayout.findViewById(R.id.photo_pb_andamento_upload);
//		pg.setVisibility(View.INVISIBLE);
		
//		dialog = new ProgressDialog(getContext());
		
		getLocationPassive(endereco);
	    
	    return llLayout; // We must return the loaded Layout
	}//End onCreateView
    
	private void logaDiretorioImagens() {
		File diretorio  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File diretorio2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
		
		StringBuilder sb = new StringBuilder();
		
		if(diretorio != null){
			File[] arquivos = diretorio.listFiles();
			if(arquivos != null){
				for(File arquivo : arquivos){
					sb.append(arquivo.getName());
					Log.e(LOG_NAME, arquivo.getName());
				}
			}
		}
		
		if(diretorio2 != null){
			File[] arquivos2 = diretorio2.listFiles();
			if(arquivos2 != null){
				for(File arquivo : arquivos2){
					sb.append(arquivo.getName());
					Log.e(LOG_NAME, arquivo.getName());
				}
			}
		}
		
	}

	private Bitmap rotateBitmap(Bitmap source){
		float angle = 0;
		try{
			ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
	
			switch(orientation) {
			    case ExifInterface.ORIENTATION_ROTATE_90:
			    	angle = 90;
			        break;
			    case ExifInterface.ORIENTATION_ROTATE_180:
			    	angle = 180;
			        break;
			    // etc.
			}
		}catch(Exception e){
			
		}
		
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	private void abreCamera(int actionCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;
			try {
				f 						     = setUpPhotoFile();
				mCurrentPhotoPath 		     = f.getAbsolutePath();
				caminhoDasFotosNoDispositivo = new Object[]{mCurrentPhotoPath};
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}
	
	private void abreCamera2(int actionCode) {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;
			try {
				f 						     = setUpPhotoFile();
				mCurrentPhotoPath 		     = f.getAbsolutePath();
				caminhoDasFotosNoDispositivo = new Object[]{mCurrentPhotoPath};
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			break;

		default:
			break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}	
	
	private void abreGaleria() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, ACTION_GET_PHOTO_FROM_GALLERY);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		System.gc();

		switch (requestCode) {
		
			case ACTION_TAKE_PHOTO_B:
				if (resultCode == Activity.RESULT_OK) {
//					handleBigCameraPhoto();
					
					Bundle extras = data.getExtras();
					oBitmap = (Bitmap) extras.get("data");
					
					addImageNoCarrossel(data);
				}
				break;
				
			case ACTION_GET_PHOTO_FROM_GALLERY:
				if (resultCode == Activity.RESULT_OK) {
					
					Uri selectedImage = data.getData();
					String[] filePathColumn = { MediaStore.Images.Media.DATA };
					Cursor cursor = super.getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
					cursor.moveToFirst();
					
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);
					cursor.close();
					
					String fn = picturePath; // file name
					ContentResolver cr = getContext().getContentResolver();
					Cursor c = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					           new String[]{
					              BaseColumns._ID
					           }, MediaColumns.DATA + "=?", new String[]{ fn }, null);
					     if(c!=null) {
					        try{
					           if(c.moveToNext()) {
					              long id = c.getLong(0);
					              oBitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
					           }
					        }finally{
					           c.close();
					        }
					     }
					
					
					addImageNoCarrossel(data);
			
					
				}
				break;
		}
		

		habilitaDesabilitaBotaoUpload();
		
//		pg.setVisibility(View.INVISIBLE);
		
//		dialog.dismiss();
		
		System.gc();
	}
	
	private void setPic(ImageView mImageView) {
	    // Get the dimensions of the View
	    int targetW = mImageView.getWidth();
	    int targetH = mImageView.getHeight();

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    oBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
	    mImageView.setImageBitmap(oBitmap);
	}	

	private void habilitaDesabilitaBotaoUpload() {
		
		/**
		 * Sempre que ocorrer uma ação, se houver fotos no carrossel, o botão deve estar habilitado
		 */
		if(mCarouselContainer != null && mCarouselContainer.getChildCount() > 0){
			botaoUpload.setEnabled(Boolean.TRUE);
			botaoUpload.setImageResource(R.drawable.btn_enviar_foto_editado2);
		}else{
//			botaoUpload.setEnabled(Boolean.FALSE);
//			botaoUpload.setImageResource(R.drawable.btn_enviar_foto_desabilitado);
		}
		
	}

	private void handleBigCameraPhoto() {

		if (mCurrentPhotoPath != null) {
			criaBitMap();
			salvaBitMapToFile();
			mCurrentPhotoPath = null;
		}

	}
	
	private void addImageNoCarrossel(final Intent data){
				System.gc();
				final ImageView imageItem = new ImageView(getContext());
	            // Set the shadow background
	            imageItem.setBackgroundResource(R.drawable.shadow);
	            
//	            setPic(imageItem);
	            
	            imageItem.setImageBitmap(oBitmap);
	            
				mCarouselContainer.addView(imageItem);
				
				mCarouselContainer.refreshDrawableState();
				
				System.gc();
	}
	

	
	private File setUpPhotoFile() throws IOException {
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}
	
	@SuppressLint("SimpleDateFormat")
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}
	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d(LOG_NAME, "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
	
	private String getAlbumName() {
		return "photoTeste";
	}

	
	private void criaBitMap() {

		System.gc();
		
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
//		int targetW = mImageView.getWidth();
//		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		final BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		final int photoW = bmOptions.outWidth;
		final int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((imageWidth > 0) ) {
			scaleFactor = Math.min(photoW/imageWidth, photoH/imageWidth);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		oBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);	
		
		 
	}
	
	private void salvaBitMapToFile() {
	    final Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		final File f = new File(mCurrentPhotoPath);
	    final Uri contentUri = Uri.fromFile(f);
	    mediaScanIntent.setData(contentUri);
//	    this.sendBroadcast(mediaScanIntent);
	}
	
	

	
	public void onGroupItemClick(MenuItem item) {
	    // One of the group items (using the onClick attribute) was clicked
	    // The item parameter passed here indicates which item it is
	    // All other menu item clicks are handled by onOptionsItemSelected()
	}
	
	public void getLocation(EditText endereco){
		
		try{
			GPSTracker tracker = new GPSTracker(getContext());
			location = tracker.getLocation();
			
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(getContext(), Locale.getDefault());
	
			addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
	
			String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
			String city = addresses.get(0).getLocality();
			String state = addresses.get(0).getAdminArea();
			String country = addresses.get(0).getCountryName();
			String postalCode = addresses.get(0).getPostalCode();
			String knownName = addresses.get(0).getFeatureName();
			
			endereco.setText(address + " - CEP - " + postalCode);
			
		}catch(Exception e){
			
		}
	}
	
	public void getLocationPassive(final EditText endereco){
		
		LocationManager locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
		String passiveProvider = LocationManager.PASSIVE_PROVIDER;
		
		
		location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        
		long l = 1;
		
		locationManager.requestLocationUpdates(passiveProvider, l, 0.20f, new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}
			
			@Override
			public void onProviderEnabled(String provider) {
			}
			
			@Override
			public void onProviderDisabled(String provider) {
			}
			
			@Override
			public void onLocationChanged(Location location) {
					getAddressByLocation(endereco, location);
			}

		});
		
		if(location != null){
			getAddressByLocation(endereco, location);
		}else{
			getLocation(endereco);
		}
		
	}
	
	private void getAddressByLocation(EditText endereco, Location location) {
		Debug.startMethodTracing("tracing getLocationPassive");
		try {
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(getContext(), Locale.getDefault());
			addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
			String city = addresses.get(0).getLocality();
			String state = addresses.get(0).getAdminArea();
			String country = addresses.get(0).getCountryName();
			String postalCode = addresses.get(0).getPostalCode();
			String knownName = addresses.get(0).getFeatureName();
			endereco.setText(address + " - CEP - " + postalCode);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		Debug.stopMethodTracing();
	}
	
	private File converte(Context context, String fileName, Bitmap bitmap) throws IOException{
		File f = new File(context.getCacheDir(), fileName);
		f.createNewFile();

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
		byte[] bitmapdata = bos.toByteArray();

		//write the bytes in file
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(bitmapdata);
		
		fos.close();
		
		return f;
	}
	
    private File converte(Context context, String filename){
    	File f = null;
    	
    	try{
    		f = new File(context.getCacheDir(), filename);
    		f.createNewFile();
    	}catch(Exception e){
    		Log.e(LOG_NAME, e.getLocalizedMessage());
    	}

    	//Convert bitmap to byte array
    	
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	oBitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
    	byte[] bitmapdata = bos.toByteArray();

    	try{
	    	//write the bytes in file
	    	FileOutputStream fos = new FileOutputStream(f);
	    	fos.write(bitmapdata);
	    	fos.close();
	    	bos.close();
    	}catch(Exception e){
    		Log.e(LOG_NAME, e.getLocalizedMessage());
    	}
    	
    	return f;
    }

	@Override
	public void onDestroy() {
		
	    super.onDestroy();
	}

	@Override
	public void onPause() {
//		dialog.dismiss();
//		if(alerta != null){
//			alerta.dismiss();
//		}
		super.onPause();
	}

	@Override
	public void onStop() {
//		dialog.dismiss();
//		if(alerta != null){
//			alerta.dismiss();
//		}
		super.onStop();
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = super.getFragmentManager();
		
		if(position == 1){
			fragmentManager.beginTransaction().replace(R.id.container,new PhotoActivity())
			.commit();
		}else{
			fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragmentPhoto.newInstance(position + 1))
			.commit();
		}
		
	}
	
	
}