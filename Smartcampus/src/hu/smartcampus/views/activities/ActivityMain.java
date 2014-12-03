package hu.smartcampus.views.activities;

import hu.smartcampus.R;
import hu.smartcampus.adapters.MenuArrayAdapter;
import hu.smartcampus.functions.ApplicationFunctions;
import hu.smartcampus.functions.SystemFunctions;
import hu.smartcampus.templates.CustomActionBarActivity;
import hu.smartcampus.templates.MainMenuItem;
import hu.smartcampus.threads.BackgroundOperations;
import hu.smartcampus.views.dialogs.DialogProfilePicture;
import hu.smartcampus.views.dialogs.DialogProfilePicture_;
import hu.smartcampus.views.fragments.FragmentLogin_;
import hu.smartcampus.views.fragments.FragmentNearby_;
import hu.smartcampus.views.fragments.FragmentPersonalData_;
import hu.smartcampus.views.fragments.FragmentWelcome_;

import java.util.ArrayList;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;

@SuppressLint("InflateParams")
@EActivity
public class ActivityMain extends CustomActionBarActivity {

	public static MainMenuItem top;
	public static Fragment fragment;
	private static String topMenuName = "";
	public static ArrayList<MainMenuItem> menuItems;
	private static MainMenuItem activePage = null;
	private MenuArrayAdapter adapter;
	private DrawerLayout drawerLayout;
	private ViewGroup menuContentFrame;
	private ActionBarDrawerToggle drawerToggle;
	private FragmentManager supportFragmentManager;

	@Bean
	BackgroundOperations bg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initDrawer();
		supportFragmentManager = getSupportFragmentManager();
		LayoutInflater inflater = getLayoutInflater();
		menuContentFrame = (ViewGroup) drawerLayout.findViewById(R.id.menuContentFrame);
		menuContentFrame.addView(inflater.inflate(R.layout.menu_drawer_layout, null));
		ListView menuListView = (ListView) menuContentFrame.findViewById(R.id.menuListview);
		if (menuItems == null) {
			menuItems = new ArrayList<MainMenuItem>();
			MainMenuItem top = new MainMenuItem(R.string.notLogged, FragmentLogin_.class, R.drawable.user);
			top.displayName = "";
			menuItems.add(top);
			menuItems.add(new MainMenuItem(R.string.titleEvents, FragmentWelcome_.class, R.drawable.binocular));
			menuItems.add(new MainMenuItem(R.string.nearby, FragmentNearby_.class, R.drawable.radar));
		}
		adapter = new MenuArrayAdapter(getApplicationContext(), R.layout.menu_listitem_layout, menuItems, this);
		menuListView.setAdapter(adapter);
		menuListView.setOnItemClickListener(adapter);

		// először indul az app
		if (savedInstanceState == null) {
			if (SystemFunctions.isOnline(this)) {
				bg.getSessionInfoAtStart(this);
			}
			initFragment();
		}
	}

	public void showDialog() {
		DialogProfilePicture dialog = new DialogProfilePicture_();
		dialog.show(getSupportFragmentManager(), "dialog"); // fragmentnél
															// fagyott onsave
															// után nem lehet
															// aztirta
	}

	private void initFragment() {
		if (activePage == null) { // ha még sosem volt megynitva az app
			activePage = MainMenuItem.defaultPage;
		}
		try {
			fragment = activePage.fragmentClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		FragmentTransaction transaction = supportFragmentManager.beginTransaction();
		transaction.add(R.id.mainContentFrame, fragment);
		transaction.commit();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		drawerToggle.syncState(); // Ez kell, hogy az ikon ki-be csússzon, de
									// nem kötelező sztem.
		super.onPostCreate(savedInstanceState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Ezzel kerül az action bar-on a klikk esemény átadásra a
		// drawerToggle-nek.
		// Ha igazzal tér vissza, akkor az app ikonja lett megynova.
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@UiThread
	public void loginOrPersonal() {
		if (!ApplicationFunctions.getInstance().getUserFunctions().getLoginSatus()) // Ha
																					// nincs
																					// bejelentkezve
		{
			showLogin();
		} else {
			showPersonal();
		}
	}

	private void showPersonal() {
		if (SystemFunctions.isOnline(getApplicationContext()) && ApplicationFunctions.getInstance().getUserFunctions().getLoginSatus()) {
			if (!(menuItems.get(0).fragmentClass.equals(FragmentPersonalData_.class))) {
				menuItems.remove(0);
				top = new MainMenuItem(R.string.logged, FragmentPersonalData_.class, R.drawable.user);
				Bitmap profilePic = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().getProfilePicture();
				if (profilePic != null) {
					top.profilePicture = new BitmapDrawable(getResources(), profilePic);
				}
				top.displayName = topMenuName;
				menuItems.add(0, top);
				adapter.notifyDataSetChanged();
			}
		}
	}

	private void showLogin() {
		if (!(menuItems.get(0).fragmentClass.equals(FragmentLogin_.class))) {
			menuItems.remove(0);
			MainMenuItem top = new MainMenuItem(R.string.notLogged, FragmentLogin_.class, R.drawable.user);
			top.displayName = "";
			menuItems.add(0, top);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onBackPressed() {
		if (drawerLayout.isDrawerVisible(Gravity.LEFT)) {
			drawerLayout.closeDrawers();
			return;
		}
		if (!activePage.equals(MainMenuItem.defaultPage)) {
			loginOrPersonal();

			changePage(MainMenuItem.defaultPage);
		} else {
			super.onBackPressed();
		}
	}

	public void changePage(MainMenuItem page) {
		drawerLayout.closeDrawers();

		if (activePage.equals(page)) {
			return;
		}

		Fragment fragNormalPD = getSupportFragmentManager().findFragmentById(R.id.fragNormalPD);
		Fragment fragNormalNearby = getSupportFragmentManager().findFragmentById(R.id.fragNormalNearby);
		if (fragNormalPD != null || fragNormalNearby != null) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			if (fragNormalPD != null) {
				transaction.remove(fragNormalPD);
			}
			if (fragNormalNearby != null) {
				transaction.remove(fragNormalNearby);
			}
			transaction.commit();
		}

		try {
			fragment = page.fragmentClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		FragmentTransaction transaction = supportFragmentManager.beginTransaction();
		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		transaction.replace(R.id.mainContentFrame, fragment, fragment.getClass().getSimpleName());
		transaction.commit();
		activePage = page;
	}

	private void initDrawer() {
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout); // ilyen
																		// drawerLayoutnak
																		// kell
																		// lennie
																		// a
																		// gyökér
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START); // ha
																						// nincs
																						// árnyék,
																						// nem
																						// nagy
																						// gond

		// ez valósítja meg a navigation drawer ki-be csúsztatását az appikonnal
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.openDrawer, // teljesen
																												// mindegy
																												// milyen
																												// string
				R.string.closeDrawer);
		drawerLayout.setDrawerListener(drawerToggle);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
	}

	public static void setTopMenuName(String topMenuName) {
		ActivityMain.topMenuName = topMenuName;
	}

	public MenuArrayAdapter getAdapter() {
		return adapter;
	}

}