package in.abmulani.weatherapp;

import in.abmulani.weatherapp.commonutils.CommonData;
import in.abmulani.weatherapp.commonutils.WeatherData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	// OPenWeatherMap_API_KEY
	private String _SELECTED_CITY;
	private final String _API_KEY = "2679acaf9b3b7110dd911446ab6bd927";
	String _CURRENT_CITY = "";

	ActionBar actionBar;
	private String EndDate;
	private String StartDate;
	private EditText searchCityEditTxt;
	ArrayAdapter<String> citySearchAdaptor;
	private ListView searchSuggestionListView, mainListview;
	CityNameAutocomplete currentCityNameAsync;
	ArrayList<String> CityNameSuggestion = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		actionBar = getActionBar();
		actionBar.show();
		// RefreshDataByCity("mumbai");
		// GetCurrentCityName();
		searchCityEditTxt = (EditText) findViewById(R.id.city_search_edittext);
		searchCityEditTxt.addTextChangedListener(searchCityWatcher);
		searchSuggestionListView = (ListView) findViewById(R.id.suggestion_listView);
		mainListview = (ListView) findViewById(R.id.main_listview);
		searchSuggestionListView.setOnItemClickListener(itemClickListner);

	}

	OnItemClickListener itemClickListner = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			_SELECTED_CITY = CityNameSuggestion.get(position);
			searchSuggestionListView.setVisibility(View.GONE);
			RefreshDataByCity(_SELECTED_CITY,false);
		}
	};

	
	public void UpdateStatus(String city,boolean fromGps){
		ActionBar actionBar=getActionBar();
		if(fromGps){
			actionBar.setSubtitle("From Current Location..");
		}else{
			actionBar.setSubtitle("User Selection..");
		}
		actionBar.setTitle(city);
	}
	
	TextWatcher searchCityWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (searchCityEditTxt.getText().toString().trim().length() > 2) {
				if (currentCityNameAsync == null) {
					currentCityNameAsync = new CityNameAutocomplete(
							searchCityEditTxt.getText().toString().trim()
									.replaceAll(" ", "%20"));
					currentCityNameAsync.execute();
				} else {
					currentCityNameAsync.cancel(true);
					currentCityNameAsync = new CityNameAutocomplete(
							searchCityEditTxt.getText().toString().trim()
									.replaceAll(" ", "%20"));
					currentCityNameAsync.execute();
				}
			} else {
				searchSuggestionListView.setVisibility(View.GONE);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	private void GetCurrentCityName() {
		Location location = GetLastKnownLocation();
		if(location!=null){
		String latitude = location.getLatitude() + "";
		String longitude = location.getLongitude() + "";
		// String latitude = "18.520510";
		// String longitude = "73.856733";
		String UrlStr = "http://api.openweathermap.org/data/2.5/find?lat="
				+ latitude + "&lon=" + longitude + "&APPID=" + _API_KEY
				+ "&cnt=1";
		new FindCityName(UrlStr).execute();
		}else{
			Toast.makeText(MainActivity.this,
					"FAILED TO READ YOUR CURRENT LOCATION..", Toast.LENGTH_LONG)
					.show();
		}
	}

	private Location GetLastKnownLocation() {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location location = null;
		List<String> providers = manager.getProviders(true);
		for (String provider : providers) {
			location = manager.getLastKnownLocation(provider);
			if (location != null)
				return location;
		}
		return null;
	}

	private void RefreshDataByCity(String city,boolean fromGps) {
		// String UrlStr =
		// "http://api.openweathermap.org/data/2.5/history/city?q="
		// + city + "&APPID=" + _API_KEY;
		//
		// String UrlStr =
		// "http://api.openweathermap.org/data/2.5/history/city?q="
		// + city + "&APPID=" + _API_KEY+"&type=day&cnt=30";
		// String UrlStr =
		// "http://api.openweathermap.org/data/2.5/history/city?q="
		// + city + "&APPID="+ _API_KEY+"&cnt=30&type=day";
		UpdateTimeStamps();
		String UrlStr = "http://api.openweathermap.org/data/2.5/history/city?q="
				+ city
				+ "&APPID="
				+ _API_KEY
				+ "&type=day&start="
				+ StartDate
				+ "&end=" + EndDate + "&cnt=30";

		// String UrlStr =
		// "http://api.openweathermap.org/data/2.5/history/city?q="
		// + city + "&APPID=" + _API_KEY + "&type=day";

		new PostToServer(UrlStr,city,fromGps).execute();
	}

	private void UpdateTimeStamps() {
		Calendar now = Calendar.getInstance();
		String tempDate = (now.get(Calendar.YEAR) + "-"
				+ (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE)
				+ " " + now.get(Calendar.HOUR_OF_DAY) + ":"
				+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));
		EndDate = CommonData.GenerateTimeStamp(tempDate);
		now.add(Calendar.DATE, -30);
		tempDate = (now.get(Calendar.YEAR) + "-"
				+ (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DATE)
				+ " " + (now.get(Calendar.HOUR_OF_DAY) - 2) + ":"
				+ now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND));
		StartDate = CommonData.GenerateTimeStamp(tempDate);
	}

	class PostToServer extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog PG = null;
		private String RESULT_STRING;
		String URL;
		private String City;
		boolean fromGps;
		
		public PostToServer(String URL, String City,boolean fromGps) {
			this.URL = URL;
			this.City = City;
			this.fromGps = fromGps;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RESULT_STRING = null;
			PG = new ProgressDialog(MainActivity.this);
			PG.setCancelable(false);
			PG.setMessage("Loading..");
			PG.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			PG.show();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(URL);
				Log.d("REQUEST: ", URL);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity httpEntity = response.getEntity();
				RESULT_STRING = EntityUtils.toString(httpEntity);
				Log.d("RESPONSE: ", RESULT_STRING);
				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			PG.dismiss();
			if (result) {
				CommonData.ReceivedData.clear();
				UpdateStatus(City, fromGps);
				try {
					JSONObject jObjMain = new JSONObject(RESULT_STRING);
					JSONArray jArr_List = jObjMain.getJSONArray("list");
					Toast.makeText(MainActivity.this,
							"Length: " + jArr_List.length(), Toast.LENGTH_LONG)
							.show();
					for (int i = jArr_List.length() - 1; i > 0; i = i - 24) {
						CommonData.ReceivedData.add(CreateWeatherObj(jArr_List
								.getJSONObject(i)));
					}
					AdapterClass mainAdapter = new AdapterClass(
							MainActivity.this);
					mainListview.setAdapter(mainAdapter);
				} catch (JSONException e) {
					Toast.makeText(MainActivity.this,
							"Exception in Json Parsing", Toast.LENGTH_LONG)
							.show();
					e.printStackTrace();
				}
			} else {
				Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_LONG)
						.show();
			}
		}

		public WeatherData CreateWeatherObj(JSONObject innerObj)
				throws JSONException {
			WeatherData tempWeatherData = new WeatherData();
			JSONArray weatherArray = innerObj.getJSONArray("weather");
			JSONObject weatherObj = weatherArray.getJSONObject(0);
			tempWeatherData.setID(weatherObj.getString("id"));
			tempWeatherData.setMain(weatherObj.getString("main"));
			tempWeatherData.setDesc(weatherObj.getString("description"));
			JSONObject mainObj = innerObj.getJSONObject("main");
			tempWeatherData.setMin_Temp(mainObj.getString("temp_min"));
			tempWeatherData.setMax_Temp(mainObj.getString("temp_max"));
			tempWeatherData.setHumidity(mainObj.getString("humidity"));
			tempWeatherData.setTimeStamp(innerObj.getString("dt"));
			return tempWeatherData;
		}

	}

	class FindCityName extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog PG = null;
		private String RESULT_STRING;
		String URL;
		FindCityName currentAsync;

		public FindCityName(String URL) {
			this.URL = URL;
			currentAsync = this;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			RESULT_STRING = null;
			PG = new ProgressDialog(MainActivity.this);
			PG.setCancelable(true);
			PG.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					currentAsync.cancel(true);
				}
			});
			PG.setMessage("Retrieving Current City..");
			PG.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			PG.show();
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(URL);
				Log.d("REQUEST: ", URL);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity httpEntity = response.getEntity();
				RESULT_STRING = EntityUtils.toString(httpEntity);
				Log.d("RESPONSE: ", RESULT_STRING);
				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			PG.dismiss();
			if (result) {
				try {
					JSONObject jObj = new JSONObject(RESULT_STRING);
					_CURRENT_CITY = jObj.getJSONArray("list").getJSONObject(0)
							.getString("name");
					RefreshDataByCity(_CURRENT_CITY,true);
				} catch (JSONException e) {
					Toast.makeText(MainActivity.this, "Exception..",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			} else {
				Toast.makeText(MainActivity.this,
						"Error Retrieving Current City..", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	class CityNameAutocomplete extends AsyncTask<Void, Void, Boolean> {
		private String RESULT_STRING;
		String URL;

		public CityNameAutocomplete(String city) {
			URL = "http://api.openweathermap.org/data/2.5/find?q=" + city
					+ "&type=like";
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(URL);
				Log.d("REQUEST: ", URL);
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity httpEntity = response.getEntity();
				RESULT_STRING = EntityUtils.toString(httpEntity);
				Log.d("RESPONSE: ", RESULT_STRING);
				return true;
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				CityNameSuggestion.clear();
				try {
					searchCityEditTxt.setTextColor(Color.BLACK);
					JSONObject jObj = new JSONObject(RESULT_STRING);
					JSONArray jArr = jObj.getJSONArray("list");
					if (jArr.length() > 0) {
						int count = jArr.length() > 10 ? 10 : jArr.length();
						for (int i = 0; i < count; i++) {
							CityNameSuggestion.add(jArr.getJSONObject(i)
									.getString("name"));
						}
						searchSuggestionListView.setVisibility(View.VISIBLE);
						citySearchAdaptor = new ArrayAdapter<String>(
								MainActivity.this,
								android.R.layout.simple_list_item_single_choice,
								CityNameSuggestion);
						searchSuggestionListView.setAdapter(citySearchAdaptor);
						if (CityNameSuggestion.size() == 1) {
							if (CityNameSuggestion.get(0).equals(
									searchCityEditTxt.getText().toString()
											.trim())) {
								searchCityEditTxt.setTextColor(Color.GREEN);
								searchSuggestionListView
										.setVisibility(View.GONE);
								_SELECTED_CITY = CityNameSuggestion.get(0);
							}
						}
					} else {
						searchCityEditTxt.setTextColor(Color.RED);
						searchSuggestionListView.setVisibility(View.GONE);
					}
				} catch (JSONException e) {
					Toast.makeText(MainActivity.this, "Exception..",
							Toast.LENGTH_LONG).show();
					searchCityEditTxt.setTextColor(Color.RED);
					searchSuggestionListView.setVisibility(View.GONE);
					e.printStackTrace();
				}
			} else {
				Toast.makeText(MainActivity.this,
						"Error Retrieving Current City..", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_gpslocation:
			Toast.makeText(getApplicationContext(),
					"Weather of Current Location..", Toast.LENGTH_LONG).show();
			GetCurrentCityName();
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
