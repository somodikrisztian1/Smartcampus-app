package hu.smartcampus.adapters;

import hu.smartcampus.R;
import hu.smartcampus.entities.Event;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventAdapter<T> extends ArrayAdapter<Event> {

	private Context context;

	public EventAdapter(Context context, int resource, List<Event> events) {
		super(context, resource, events);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		TextView text = (TextView) view;
		if (getItem(position).getMarkerType() != -1) {
			text.setTextAppearance(context, R.style.MyTextView2);
			return text;
		} else {
			text.setTextAppearance(context, R.style.MyTextView);
			return text;
		}
	}

}
