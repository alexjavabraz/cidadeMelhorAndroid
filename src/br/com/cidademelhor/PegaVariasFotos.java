package br.com.cidademelhor;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class PegaVariasFotos extends Activity {
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private ImageAdapter imageAdapter;
    private Cursor imagecursor;
    private static final String orderBy = MediaStore.Images.Media._ID;
    private static final String selectionArgs = MediaStore.Images.Media._COUNT + " =  ?";
    private static final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
    
    private static final int IMAGE_VIEW = 10;
    
    private static final String TAG="PegaVariasFotos";
    
    public void onDestroy() {
    	   super.onDestroy();
      	   if(imagecursor != null){
      		   imagecursor.close();
      		   imagecursor = null;
      	   }
      	   
      	 arrPath      = null;
      	 imageAdapter = null;
      	 thumbnailsselection = null;
      	 thumbnails          = null;
      	 imagecursor         = null;
      	 
    }
    
    public void onStop() {
 	   super.onDestroy();
   	   if(imagecursor != null){
   		   imagecursor.close();
   		   imagecursor = null;
   	   }
    }
 
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        if (requestCode == IMAGE_VIEW && resultCode == RESULT_OK && null != data) {
        	constroiCursor();
        }
        
    }
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pega_varias_fotos);
        
        int image_column_index = 0;
        
        	constroiCursor();
        	
            this.count                   = imagecursor.getCount();
            this.thumbnails              = new Bitmap[this.count];
            this.arrPath                 = new String[this.count];
            this.thumbnailsselection     = new boolean[this.count];
            image_column_index           = imagecursor.getColumnIndex(MediaStore.Images.Media._ID);
            
            for (int i = 0; i < this.count; i++) {
                imagecursor.moveToPosition(i);
                final int id = imagecursor.getInt(image_column_index);
                final int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
                thumbnails[i] = MediaStore.Images.Thumbnails.getThumbnail(
                        getApplicationContext().getContentResolver(), id,
                        MediaStore.Images.Thumbnails.MICRO_KIND, null);
                arrPath[i]= imagecursor.getString(dataColumnIndex);
            }
            
            final GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
            imageAdapter = new ImageAdapter();
            imagegrid.setAdapter(imageAdapter);
            imagecursor.close();
            imagecursor = null;

 
        final Button selectBtn = (Button) findViewById(R.id.selectBtn);
        selectBtn.setOnClickListener(new OnClickListener() {
 
            public void onClick(View v) {
                final int len = thumbnailsselection.length;
                int cnt = 0;
                String selectImages = "";
                
                List<String> caminhos = new ArrayList<String>();
                
                for (int i =0; i<len; i++)
                {
                    if (thumbnailsselection[i]){
                        cnt++;
                        selectImages = selectImages + arrPath[i] + "|";
                        caminhos.add(arrPath[i]);
                    }
                }
                
                if (cnt > 0){ 
                    final Intent result = new Intent();
                    result.putExtra("imagens", caminhos.toArray());
                    setResult(RESULT_OK, result);
                    PegaVariasFotos.this.onDestroy();
                    finish();
                }
                
            }
        });
    }
 
    private void constroiCursor() {
//        imagecursor = managedQuery(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, selectionArgs,
//                null, orderBy);
        
        
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon().appendQueryParameter("limit", "100").build();
        
        imagecursor = managedQuery(
                uri, columns, null,
                null, orderBy);
		
	}

	public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
 
        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        public int getCount() {
            return count;
        }
 
        public Object getItem(final int position) {
            return position;
        }
 
        public long getItemId(final int position) {
            return position;
        }
 
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.galleryitem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
//                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
                
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
//            holder.checkbox.setId(position);
            holder.imageview.setId(position);
            
            holder.imageview.setClickable(false);
//            
//            holder.checkbox.setOnClickListener(new OnClickListener() {
// 
//                public void onClick(View v) {
//                    // TODO Auto-generated method stub
//                    CheckBox cb = (CheckBox) v;
//                    int id = cb.getId();
//                    if (thumbnailsselection[id]){
//                        cb.setChecked(false);
//                        thumbnailsselection[id] = false;
//                    } else {
//                        cb.setChecked(true);
//                        thumbnailsselection[id] = true;
//                    }
//                }
//            });
            
            holder.imageview.setOnClickListener(new OnClickListener() {
 
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    int id = v.getId();
//                    Intent intent = new Intent();
//                    intent.setAction(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
//                    startActivityForResult(intent, IMAGE_VIEW);
                    
                    PegaVariasFotos.ViewHolder vh = (PegaVariasFotos.ViewHolder)((android.widget.RelativeLayout)v.getParent()).getTag();
//                    CheckBox cb                = vh.getCheckBox();
                    
//                    Toast.makeText(getApplicationContext(),
//                            "Image selected " + arrPath[id],
//                            Toast.LENGTH_SHORT).show();
                    
                    if (thumbnailsselection[id]){
//                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                        vh.getImageView().setImageBitmap(vh.getImagem());
                    } else {
//                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                        vh.getImageView().getDrawable().setAlpha(80);
                        
                        Resources r = getResources();
                        Drawable[] layers = new Drawable[2];
                        layers[0] = vh.getImageView().getDrawable();
                        layers[1] = r.getDrawable(R.drawable.foto_selecionada);
                        LayerDrawable layerDrawable = new LayerDrawable(layers);
                        vh.getImageView().setImageDrawable(layerDrawable);
                    }
                    
                }
            });
            holder.setImagem(thumbnails[position]);
            holder.imageview.setImageBitmap(thumbnails[position]);
//            holder.checkbox.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
        
    }
	
    public class ViewHolder {
        ImageView imageview;
        Bitmap imagem;
        String caminhoOriginal;
//        CheckBox checkbox;
        int id;
        
//        public CheckBox getCheckBox(){
//        	return checkbox;
//        }
        
        public ImageView getImageView(){
        	return imageview;
        }

		public Bitmap getImagem() {
			return imagem;
		}

		public void setImagem(Bitmap imagem) {
			this.imagem = imagem;
		}

		public ImageView getImageview() {
			return imageview;
		}

		public void setImageview(ImageView imageview) {
			this.imageview = imageview;
		}

		public String getCaminhoOriginal() {
			return caminhoOriginal;
		}

		public void setCaminhoOriginal(String caminhoOriginal) {
			this.caminhoOriginal = caminhoOriginal;
		}
		
    }
}