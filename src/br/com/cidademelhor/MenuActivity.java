package br.com.cidademelhor;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;

public class MenuActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;
	
	private int O_CIDADE_MELHOR       = 0;
	private int COMO_FUNCIONA         = 1;
	private int ONDE_PRECISA_MELHORAR = 2;
	private int MELHORE_JA            = 3;
	

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	
	private static final int DRAWER_DELAY = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getString(R.string.app_name);

//		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#61A1BC")));
		
		final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		
		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, drawer);
		
		
		ViewTreeObserver vto = drawer.getViewTreeObserver();
	    if (vto != null) vto.addOnPreDrawListener(new ShouldShowListener(drawer));
	    
	    new Handler().postDelayed(new Runnable() {
	    	
			@Override
			public void run() {
				drawer.openDrawer(Gravity.LEFT);
				
			}
		}, DRAWER_DELAY);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getSupportFragmentManager();
		
		if(position == O_CIDADE_MELHOR){

			fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragmentPhoto.newInstance(position + 1))
			.commit();
			
		}else if( position == COMO_FUNCIONA){
			
			fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragmentPhoto.newInstance(position + 1))
			.commit();
			
		}else if (position == ONDE_PRECISA_MELHORAR){
			
			Bundle arguments = new Bundle();
			arguments.putString(ProblemaDetailFragment.ARG_ITEM_ID,
					getIntent().getStringExtra(ProblemaDetailFragment.ARG_ITEM_ID));
			ProblemaDetailFragment fragment = new ProblemaDetailFragment();
			fragment.setArguments(arguments);
			
			fragmentManager.beginTransaction().replace(R.id.container, fragment)
			.commit();
			
		}else if (position == MELHORE_JA){
			fragmentManager.beginTransaction().replace(R.id.container,new PhotoActivity())
			.commit();
		}
		
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
//			mTitle = getString(R.string.app_name);
			break;
		case 2:
//			mTitle = getString(R.string.app_name);
			break;
		case 3:
//			mTitle = getString(R.string.app_name);
			break;
		case 4:
//			mTitle = getString(R.string.app_name);
			break;			
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.menu, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MenuActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragmentPhoto extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragmentPhoto newInstance(int sectionNumber) {
			PlaceholderFragmentPhoto fragment = new PlaceholderFragmentPhoto();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragmentPhoto() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MenuActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}
	
	
	private static class ShouldShowListener implements OnPreDrawListener {

	    private final DrawerLayout drawerLayout;

	    private ShouldShowListener(DrawerLayout drawerLayout) {
	        this.drawerLayout= drawerLayout;
	    }

	    @Override
	    public boolean onPreDraw() {
	        if (drawerLayout != null) {
	            ViewTreeObserver vto = drawerLayout.getViewTreeObserver();
	            if (vto != null) {
	                vto.removeOnPreDrawListener(this);
	            }
	        }

	        drawerLayout.openDrawer(Gravity.LEFT);
	        return true;
	    }
	}
	

}
