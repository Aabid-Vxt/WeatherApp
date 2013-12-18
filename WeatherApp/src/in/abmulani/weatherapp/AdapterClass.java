package in.abmulani.weatherapp;

import in.abmulani.weatherapp.commonutils.CommonData;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AdapterClass extends BaseAdapter {
	Context mContext; // ADD THIS to keep a context
	private LayoutInflater inflater;

	public AdapterClass(Context context) {
		this.mContext = context;
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return CommonData.ReceivedData.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class ViewHolder {
		TextView date;
		TextView main;
		TextView desc;
		TextView min_temp;
		TextView max_temp;
		TextView humidity;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		try {
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.list_inflater, null);
				holder.date = (TextView) convertView.findViewById(R.id.li_date);
				holder.main = (TextView) convertView.findViewById(R.id.li_main);
				holder.desc = (TextView) convertView.findViewById(R.id.li_desc);
				holder.min_temp = (TextView) convertView.findViewById(R.id.li_minTemp);
				holder.max_temp = (TextView) convertView.findViewById(R.id.li_maxTemp);
				holder.humidity = (TextView) convertView.findViewById(R.id.li_humidity);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			holder.date.setText(CommonData.ConvertTimestamp(CommonData.ReceivedData.get(position).getTimeStamp()));
			holder.main.setText(CommonData.ReceivedData.get(position).getMain());
			holder.desc.setText(CommonData.ReceivedData.get(position).getDesc());
			holder.min_temp.setText(CommonData.ReceivedData.get(position).getMin_Temp());
			holder.max_temp.setText(CommonData.ReceivedData.get(position).getMax_Temp());
			holder.humidity.setText(CommonData.ReceivedData.get(position).getHumidity());
				
		} catch (Exception e) {
			Log.e("StakeHolders/getView()", e.toString());
			e.printStackTrace();
		}
		return convertView;
	}

}
