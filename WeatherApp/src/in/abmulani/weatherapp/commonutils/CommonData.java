package in.abmulani.weatherapp.commonutils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class CommonData {

	public static String ConvertTimestamp(String timestamp) {
		long dv = Long.valueOf(timestamp) * 1000;
		Date df = new java.util.Date(dv);
		return new SimpleDateFormat("dd-MM-yyyy").format(df);
	}
	
	public static ArrayList<WeatherData> ReceivedData = new ArrayList<WeatherData>();

	public static String GenerateTimeStamp(String dateStr) {
		Log.d("Previous: ",dateStr);
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date date = (Date)formatter.parse(dateStr); 
			Log.d("Answer:", date.getTime()/1000+"");
			return (date.getTime()/1000)+"";
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;

	}

}
