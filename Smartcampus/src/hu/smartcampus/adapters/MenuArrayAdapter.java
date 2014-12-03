package hu.smartcampus.adapters;

import hu.smartcampus.R;
import hu.smartcampus.templates.MainMenuItem;
import hu.smartcampus.templates.MyTextView;
import hu.smartcampus.views.activities.ActivityMain;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class MenuArrayAdapter extends ArrayAdapter<MainMenuItem> implements OnItemClickListener
{
	public static MyTextView text;
	ActivityMain activity;
	
	public MenuArrayAdapter(Context context, int resource, ArrayList<MainMenuItem> menuItems, ActivityMain activity)
	{
		super(context, resource, menuItems);
		this.activity = activity;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = null;
		if (convertView == null)
		{
			LayoutInflater inflater = activity.getLayoutInflater();
			view = inflater.inflate(R.layout.menu_listitem_layout, parent, false);
		} else
		{
			view = convertView;
		}
		text = (MyTextView) view.findViewById(R.id.menu_listitem_text);
		MainMenuItem item = getItem(position);
		if(item.displayName != null) {
			text.setSingleLine(false);
			text.setText(getItem(position).textId);
			text.append(item.displayName);
			text.setTypeface(text.getTypeface(), Typeface.BOLD);
			TypedValue out = new TypedValue();
    		activity.getResources().getValue(R.dimen.textNormalSizeR, out, true);
    		float textNormalSizeR = out.getFloat();
			text.setTextSize(textNormalSizeR);
		}
		else {
			text.setSingleLine(true);
			text.setText(getItem(position).textId);
			text.setTypeface(text.getTypeface(), Typeface.NORMAL);
		}
		if(getItem(position).profilePicture == null) {
			text.setCompoundDrawablesWithIntrinsicBounds(getItem(position).iconId, 0, R.drawable.right_arrow, 0);
		}
		else {
			Drawable profilePicture = getItem(position).profilePicture;
			Drawable rightArrow = activity.getResources().getDrawable(R.drawable.right_arrow);
			rightArrow.setBounds(0, 0, rightArrow.getIntrinsicWidth(), rightArrow.getIntrinsicHeight());
			profilePicture.setBounds(0, 0, profilePicture.getIntrinsicWidth(), profilePicture.getIntrinsicHeight());
			text.setCompoundDrawables(profilePicture, null, rightArrow, null);
		}
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		activity.changePage(getItem(position));
	}
	
}