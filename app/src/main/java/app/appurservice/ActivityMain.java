package app.appurservice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.appurservice.data.AppConfig;
import app.appurservice.data.Constant;
import app.appurservice.data.DatabaseHandler;
import app.appurservice.data.SharedPref;
import app.appurservice.fragment.FragmentCategory;
import app.appurservice.model.Category;
import app.appurservice.model.Place;
import app.appurservice.sigin.DownloadImage;
import app.appurservice.sigin.FormDetails;
import app.appurservice.sigin.UserSessionManager;
import app.appurservice.utils.PermissionUtil;
import app.appurservice.utils.Tools;

public class ActivityMain extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_OBJ = "app.appurservice.EXTRA_OBJ";
    //for ads
    private InterstitialAd mInterstitialAd;

    private ImageLoader imgloader = ImageLoader.getInstance();

    public ActionBar actionBar;
    public Toolbar toolbar;
    private int cat[];
    private FloatingActionButton fab;
    private SearchView searchView;
    private MenuItem searchItem;
    private NavigationView navigationView;
    private DatabaseHandler db;
    private SharedPref sharedPref;
    private RelativeLayout nav_header_lyt;

    static ActivityMain activityMain;

     /*MAPS */

    private ClusterManager<Place> mClusterManager;
    private View parent_view;
    private PlaceMarkerRenderer placeMarkerRenderer;

    // for single place
    private Place ext_place = null;
    private boolean isSinglePlace;
    HashMap<String, Place> hashMapPlaces = new HashMap<>();

    // id category
    private int cat_id = -1;
    private Category cur_category;


    // view for custom marker
    private ImageView icon, marker_bg;
    private View marker_view;

    private GoogleMap mMap;

    UserSessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityMain = this;
        parent_view = findViewById(android.R.id.content);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        marker_view = inflater.inflate(R.layout.maps_marker, null);
        icon = (ImageView) marker_view.findViewById(R.id.marker_icon);
        marker_bg = (ImageView) marker_view.findViewById(R.id.marker_bg);

        ext_place = (Place) getIntent().getSerializableExtra(EXTRA_OBJ);
        isSinglePlace = (ext_place != null);
        if (!imgloader.isInited()) Tools.initImageLoader(this);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        db = new DatabaseHandler(this);
        sharedPref = new SharedPref(this);

        session = new UserSessionManager(getApplicationContext());

        session.checkLogin();

        prepareAds();
        initToolbar();
        initDrawerMenu();
        prepareImageLoader();
        cat = getResources().getIntArray(R.array.id_category);
        //map viewing
            SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
        // first drawer view
        //onItemSelected(R.id.nav_all);
        actionBar.setTitle(getString(R.string.title_nav_all));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toogleSearchView(searchItem.isVisible());

            }
        });

            Tools.cekConnection(getApplicationContext(), findViewById(R.id.frame_content));

        // for system bar in lollipop
        Tools.systemBarLolipop(this);
    }

            private void initToolbar() {
                toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
                Tools.setActionBarColor(this, actionBar);
                }

            private void initDrawerMenu() {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
                        @Override
                        public void onDrawerOpened(View drawerView) {
                            updateFavoritesCounter(navigationView, R.id.nav_favorites, db.getFavoritesSize());
                            toogleSearchView(true);
                            showInterstitial();
                            super.onDrawerOpened(drawerView);
                            }
                        };
                drawer.setDrawerListener(toggle);
                toggle.syncState();


            navigationView = (NavigationView) findViewById(R.id.nav_view);
                View hView = navigationView.getHeaderView(0);

                //viewing name/username for navigation
                TextView nav_user = (TextView) hView.findViewById(R.id.user_name);
                String userneym = getIntent().getStringExtra("username");
                nav_user.setText(userneym);

                    if (userneym == null) {
                        Bundle inBundle = getIntent().getExtras();
                        String name = inBundle.get("name").toString();
                        String surname = inBundle.get("surname").toString();
                        String imageUrl = inBundle.get("imageUrl").toString();

                            nav_user.setText(name + " " + surname);
                         new DownloadImage((ImageView) hView.findViewById(R.id.user_image)).execute(imageUrl);

                            Intent ii = new Intent();
                            ii.putExtra("firstname", name);
                            ii.putExtra("lastname", surname);
                            ii.setClass(this, FormDetails.class);

                        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                actionBar.setTitle(item.getTitle().toString());
                return onItemSelected(item.getItemId());
                }
                });

        // navigation header
            View nav_header = navigationView.getHeaderView(0);
            nav_header_lyt = (RelativeLayout) nav_header.findViewById(R.id.nav_header_lyt);
            nav_header_lyt.setBackgroundColor(Tools.colorBrighter(sharedPref.getThemeColorInt()));
            (nav_header.findViewById(R.id.menu_nav_setting)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            Intent i = new Intent(getApplicationContext(), ActivitySetting.class);
                                startActivity(i);
                                }
                            });



//                (nav_header.findViewById(R.id.menu_nav_map)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(), ActivityMaps.class);
//                startActivity(i);
//                    }
//                    });
//

            (nav_header.findViewById(R.id.menu_nav_edit)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            Intent i = new Intent(getApplicationContext(), FormDetails.class);
                                startActivity(i);
                            }
                            });
            }

                    @Override
                    public void onBackPressed() {
                        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                        if (!drawer.isDrawerOpen(GravityCompat.START)) {
                                drawer.openDrawer(GravityCompat.START);
                        } else {
                            doExitApp();
                                }
                    }

                    private void toogleSearchView(boolean open) {
                        if (open) {
                            searchItem.setVisible(false);
                            searchView.onActionViewCollapsed();
                            fab.setImageResource(R.drawable.abc_ic_search_api_mtrl_alpha);
                            hideKeyboard();
                        } else {
                            searchItem.setVisible(true);
                            searchView.onActionViewExpanded();
                            fab.setImageResource(R.drawable.abc_ic_clear_mtrl_alpha);
                            }
                    }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.menu_activity_main, menu);
                searchItem = menu.findItem(R.id.action_search);
                searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
                searchView.setIconified(false);
                searchView.setQueryHint(getString(R.string.search_toolbar_hint));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                try {
                    FragmentCategory.filterAdapter(s);
                } catch (Exception e) {
                }
                return true;
            }
            });
                searchView.onActionViewCollapsed();
                searchItem.setVisible(false);
                return true;
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                Intent i = new Intent(getApplicationContext(), ActivitySetting.class);
                    startActivity(i);
            } else if (id == R.id.action_rate) {
                Tools.rateAction(ActivityMain.this);
            } else if (id == R.id.action_about) {
                Tools.aboutAction(ActivityMain.this);
            } else if (id == R.id.action_logout) { AlertDialog.Builder builder =new AlertDialog.Builder(ActivityMain.this);
                builder.setTitle(getString(R.string.dialog_confirm_title));
                builder.setMessage(getString(R.string.message_logout));
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        session.logoutUser();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();

            }

                return super.onOptionsItemSelected(item);
            }

            public boolean onItemSelected(int id) {
            // Handle navigation view item clicks here.
            Fragment fragment = null;
            Bundle bundle = new Bundle();
            CameraUpdate location;
            switch (id) {
            //sub menu
            case R.id.nav_all:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, -1);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlace());
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;

            // favorites
            case R.id.nav_favorites:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, -2);


                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllFavorites());
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
            //carpenter
            case R.id.nav_carp:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, 1);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlaceByCategory(1));
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
            //driver
            case R.id.nav_driver:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, 2);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlaceByCategory(2));
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
            //electrician
            case R.id.nav_elect:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, 3);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlaceByCategory(3));
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
            //gardener
            case R.id.nav_gard:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, 4);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlaceByCategory(4));
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
            //house helper
            case R.id.nav_househ:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, 5);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlaceByCategory(5));
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
            //laundry helper
            case R.id.nav_laundryh:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, 6);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlaceByCategory(6));
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
            //mechanic
            case R.id.nav_mech:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, 7);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlaceByCategory(7));
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
            //painter
            case R.id.nav_paint:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, 8);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlaceByCategory(8));
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
            //Photographer
            case R.id.nav_photo:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, 9);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlaceByCategory(9));
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
            //plumber
            case R.id.nav_plum:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, 10);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlaceByCategory(10));
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
            //technician
            case R.id.nav_tech:
                fragment = new FragmentCategory();
                bundle.putInt(FragmentCategory.TAG_CATEGORY, 11);

                mMap.clear();
                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                mClusterManager = new ClusterManager<>(this, mMap);
                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                mClusterManager.setRenderer(placeMarkerRenderer);
                mMap.setOnCameraChangeListener(mClusterManager);
                loadClusterManager(db.getAllPlaceByCategory(11));
                //mMap.animateCamera(location);
                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Place place;
                        if (hashMapPlaces.get(marker.getId()) != null) {
                            place = hashMapPlaces.get(marker.getId());
                        } else {
                            place = ext_place;
                        }
                        ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                    }
                });

                enableMyLocation();
                break;
                default:
                break;

        }
            fragment.setArguments(bundle);

                    if (fragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame_content, fragment);
                            fragmentTransaction.commit();
                            initToolbar();
                        }

                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    return true;
                }

                    private void hideKeyboard() {
                            View view = this.getCurrentFocus();
                                if (view != null) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }

                private long exitTime = 0;

                    public void doExitApp() {
                        if ((System.currentTimeMillis() - exitTime) > 2000) {
                            Toast.makeText(this, R.string.press_again_exit_app, Toast.LENGTH_SHORT).show();
                            exitTime = System.currentTimeMillis();
                        } else {
                            finish();
                        }
                    }

                        private void prepareImageLoader() {
                            Tools.initImageLoader(this);
                        }
                        @Override
                        protected void onResume() {
                            if (!imgloader.isInited()) Tools.initImageLoader(this);
                            updateFavoritesCounter(navigationView, R.id.nav_favorites, db.getFavoritesSize());
                            if (actionBar != null) {
                                Tools.setActionBarColor(this, actionBar);
                                // for system bar in lollipop
                                Tools.systemBarLolipop(this);
                            }
                            if (nav_header_lyt != null) {
                                nav_header_lyt.setBackgroundColor(Tools.colorBrighter(sharedPref.getThemeColorInt()));
                            }
                            super.onResume();
                        }

                        private void updateFavoritesCounter(NavigationView nav, @IdRes int itemId, int count) {
                            TextView view = (TextView) nav.getMenu().findItem(itemId).getActionView().findViewById(R.id.counter);
                            view.setText(String.valueOf(count));
                        }

                        private void prepareAds() {
                            // Create the InterstitialAd and set the adUnitId.
                            mInterstitialAd = new InterstitialAd(this);
                            // Defined in res/values/strings.xml
                            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
                            //prepare ads
                            AdRequest adRequest2 = new AdRequest.Builder().build();
                            mInterstitialAd.loadAd(adRequest2);
                        }

                        /**
                         * show ads
                         */
                        public void showInterstitial() {
                            // Show the ad if it's ready
                            if (AppConfig.ENABLE_ADSENSE && mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            }
                        }

                        public static ActivityMain getInstance() {
                            return activityMain;
                        }

                        @Override
                        public boolean onKeyDown(int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                //start camera view again
                                exitBy2Click();

                                return false;
                            } else if (keyCode == KeyEvent.KEYCODE_MENU) {
                                Toast.makeText(getApplicationContext(), " ", Toast.LENGTH_LONG).show();
                                return false;
                            }
                            return super.onKeyDown(keyCode, event);
                        }

                        /**
                         * double click to exit
                         */
                        private static Boolean isExit = false;

                        private void exitBy2Click() {
                            Timer tExit = null;
                            if (isExit == false) {
                                isExit = true; // ready to exit
                                Toast.makeText(this, "double tap to exit", Toast.LENGTH_SHORT).show();
                                tExit = new Timer();
                                tExit.schedule(new TimerTask() {
                                    public void run() {
                                        isExit = false; // exit canceled
                                    }
                                }, 2000); // if user did't click again within 2 seconds, the process inside will be canceled
                            } else {
                                System.exit(0);
                            }
                        }

                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = Tools.configActivityMaps(googleMap);
                            CameraUpdate location;
                            if (isSinglePlace) {
                                marker_bg.setColorFilter(getResources().getColor(R.color.marker_secondary));
                                MarkerOptions markerOptions = new MarkerOptions().title(ext_place.name).position(ext_place.getPosition());
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Tools.createBitmapFromView(ActivityMain.this, marker_view)));
                                mMap.addMarker(markerOptions);
                                location = CameraUpdateFactory.newLatLngZoom(ext_place.getPosition(), 11);
                                actionBar.setTitle(ext_place.name);
                            } else {
                                //mMap.setOnMyLocationChangeListener(myLocationChangeListener);
                                //location = CameraUpdateFactory.newLatLngZoom(new LatLng(Constant.city_lat, Constant.city_lng), 11);
                                mClusterManager = new ClusterManager<>(this, mMap);
                                placeMarkerRenderer = new PlaceMarkerRenderer(this, mMap, mClusterManager);
                                mClusterManager.setRenderer(placeMarkerRenderer);
                                mMap.setOnCameraChangeListener(mClusterManager);
                                loadClusterManager(db.getAllPlace());
                            }
                            //mMap.animateCamera(location);
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {
                                    Place place;
                                    if (hashMapPlaces.get(marker.getId()) != null) {
                                        place = hashMapPlaces.get(marker.getId());
                                    } else {
                                        place = ext_place;
                                    }
                                    ActivityPlaceDetail.navigate(ActivityMain.this, parent_view, place);
                                }
                            });

                            enableMyLocation();
                        }


                        private void enableMyLocation() {
                            if (PermissionUtil.isGroupPermissionGrantedd(this, Constant.PERMISSIONS_LOCATION)) {


                                // Enable / Disable my location button
                                mMap.setOnMyLocationChangeListener(myLocationChangeListener);
                                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                                mMap.setMyLocationEnabled(true);
                                mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                                    @Override
                                    public boolean onMyLocationButtonClick() {
                                        try {
                                            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                                    showAlertDialogGps();
                                                } else {
                                                    Location loc = manager.getLastKnownLocation(manager.getBestProvider(new Criteria(), false));
                                                    CameraUpdate myCam = CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 12);
                                                    mMap.animateCamera(myCam);
                                            }
                                        } catch (Exception e) {
                                        }
                                            return true;
                                        }
                                });
                            }
                        }

                        private void loadClusterManager(List<Place> places) {
                            mClusterManager.clearItems();
                            mClusterManager.addItems(places);
                        }

                        private class PlaceMarkerRenderer extends DefaultClusterRenderer<Place> {
                            public PlaceMarkerRenderer(Context context, GoogleMap map, ClusterManager<Place> clusterManager) {
                                super(context, map, clusterManager);
                            }

                            @Override
                            protected void onBeforeClusterItemRendered(Place item, MarkerOptions markerOptions) {
                                if (cat_id == -1) { // all place
                                    icon.setImageResource(R.drawable.round_shape);
                                } else {
                                    icon.setImageResource(cur_category.icon);
                                }
                                marker_bg.setColorFilter(getResources().getColor(R.color.marker_primary));
                                markerOptions.title(item.name);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Tools.createBitmapFromView(ActivityMain.this, marker_view)));
                                if (ext_place != null && ext_place.place_id == item.place_id) {
                                    markerOptions.visible(false);
                                }
                            }

                            @Override
                            protected void onClusterItemRendered(Place item, Marker marker) {
                                hashMapPlaces.put(marker.getId(), item);
                                super.onClusterItemRendered(item, marker);
                            }
                        }
                        private void showAlertDialogGps() {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage(R.string.dialog_content_gps);
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            });
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                            final AlertDialog alert = builder.create();
                            alert.show();
                        }
    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
            //mMarker = mMap.addMarker(new MarkerOptions().position(loc));
            if(mMap != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
            }
        }
    };


}



