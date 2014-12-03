package hu.smartcampus.views.fragments;

import hu.smartcampus.R;
import hu.smartcampus.entities.Event;
import hu.smartcampus.entities.EventRankResult;
import hu.smartcampus.entities.RankType;
import hu.smartcampus.functions.ApplicationFunctions;
import hu.smartcampus.threads.BackgroundOperations;
import hu.smartcampus.views.toaster.Toaster;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.List;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.res.StringRes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

@SuppressLint("InflateParams")
@EFragment
public class FragmentRatings extends Fragment implements OnCheckedChangeListener, OnClickListener{
	
	private Event event;
	private LinearLayout lLayout;
	private View view;
	
	@StringRes
	String opinion;
	@StringRes
	String count;
	@Bean
	BackgroundOperations bg;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if(lLayout == null) {
			view = inflater.inflate(R.layout.fragment_ratings, container, false);
			lLayout = (LinearLayout) view.findViewById(R.id.lLayout);
			event = getActivity().getIntent().getParcelableExtra("event");
			if(event != null) {
				bg.getEventRankTypes(event.getEventId(), this);
			}
			return view;
		}
		((ViewGroup)view.getParent()).removeView(view);
		return view;
	}
	
	private void addNewRadioLayout(RankType r) {
        TextView rTitle = (TextView)getActivity().getLayoutInflater().inflate(R.layout.mytextview, null);
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity=Gravity.CENTER;
        rTitle.setLayoutParams(params);
		rTitle.setText(r.getTitle());
		TypedValue out = new TypedValue();
		getResources().getValue(R.dimen.textBigSizeR, out, true);
		float textBigSizeR = out.getFloat();
		Log.d("lol", "textBigSizeR: " + textBigSizeR);
		rTitle.setTextSize(textBigSizeR);
		rTitle.setTextAppearance(getActivity(), R.style.boldText);
		lLayout.addView(rTitle);
		TextView rDesc = (TextView)getActivity().getLayoutInflater().inflate(R.layout.mytextview, null);
		LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		getResources().getValue(R.dimen.radioTopMargin, out, true);
		int radioTopMargin = Integer.parseInt((String) out.coerceToString());
		Log.d("lol", "radioTopMargin: " + radioTopMargin);
		params2.topMargin = radioTopMargin;
		rDesc.setLayoutParams(params2);
		rDesc.setText(r.getDescription());
		getResources().getValue(R.dimen.textNormalSizeR, out, true);
		float textNormalSizeR = out.getFloat();
		Log.d("lol", "textNormalSizeR: " + textNormalSizeR);
		rDesc.setTextSize(textNormalSizeR);
		lLayout.addView(rDesc);
		RadioGroup rg = new RadioGroup(getActivity());
		rg.setOnCheckedChangeListener(this);
		rg.setTag(r.getId());
		RadioButton rb1 = (RadioButton)getActivity().getLayoutInflater().inflate(R.layout.myradiobutton, null);
		rb1.setButtonDrawable(R.drawable.apptheme_btn_radio_holo_light);
		rb1.setText(r.getValues()[0].getDescription());
		rb1.setId(0);
		rb1.setTag(r.getValues()[0].getValue());
		RadioButton rb2 = (RadioButton)getActivity().getLayoutInflater().inflate(R.layout.myradiobutton, null);
		rb2.setButtonDrawable(R.drawable.apptheme_btn_radio_holo_light);
		rb2.setText(r.getValues()[1].getDescription());
		rb2.setId(1);
		rb2.setTag(r.getValues()[1].getValue());
		RadioButton rb3 = (RadioButton)getActivity().getLayoutInflater().inflate(R.layout.myradiobutton, null);
		rb3.setButtonDrawable(R.drawable.apptheme_btn_radio_holo_light);
		rb3.setText(r.getValues()[2].getDescription());
		rb3.setId(2);
		rb3.setTag(r.getValues()[2].getValue());
		rg.addView(rb1);
		rg.addView(rb2);
		rg.addView(rb3);
		lLayout.addView(rg);
		TextView rResult = (TextView)getActivity().getLayoutInflater().inflate(R.layout.mytextview, null);
		LayoutParams params3 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params3.topMargin = radioTopMargin;
		getResources().getValue(R.dimen.radioBottomMargin, out, true);
		int radioBottomMargin = Integer.parseInt((String) out.coerceToString());
		Log.d("lol", "radioBottomMargin: " + radioBottomMargin);
		params3.bottomMargin = radioBottomMargin;
		rResult.setLayoutParams(params3);
		long id = r.getId();
		double rankValue = -10.0;
		for(EventRankResult ev : event.getRankResult()) {
			if(ev.getRankId() == id) {
				rankValue = ev.getRankValue();
				break;
			}
		}
		rResult.setText(opinion + "   ");
		if(rankValue >= 0.0) {
			rResult.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.smile, 0);
		}
		else if(rankValue < 0.0 && rankValue >= -1.0) {
			rResult.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.sad, 0);
		}
		else {
			rResult.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.angry, 0);
		}
		rResult.setTextSize(textNormalSizeR);
		lLayout.addView(rResult);
	}

	private void addNewplus1View(RankType r) {
		TextView pTitle = (TextView)getActivity().getLayoutInflater().inflate(R.layout.mytextview, null);
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        pTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
        pTitle.setLayoutParams(params);
        pTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_plus1, 0, 0, 0);
        TypedValue out = new TypedValue();
        getResources().getValue(R.dimen.plusDrawablePadding, out, true);
		int plusDrawablePadding = Integer.parseInt((String) out.coerceToString());
		Log.d("lol", "plusDrawablePadding: " + plusDrawablePadding);
        pTitle.setCompoundDrawablePadding(plusDrawablePadding);
        getResources().getValue(R.dimen.textNormalSizeR, out, true);
		float textNormalSizeR = out.getFloat();
		Log.d("lol", "plustextNormalSizeR: " + textNormalSizeR);
        pTitle.setTextSize(textNormalSizeR);
        pTitle.setText(r.getTitle());
        pTitle.setTag(r.getId());
        pTitle.setOnClickListener(this);
        pTitle.setBackgroundResource(R.drawable.textview_selector);
		lLayout.addView(pTitle);
		TextView pResult = (TextView)getActivity().getLayoutInflater().inflate(R.layout.mytextview, null);
		pResult.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		LayoutParams params2 = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		getResources().getValue(R.dimen.plusBottomMargin, out, true);
		int plusBottomMargin = Integer.parseInt((String) out.coerceToString());
		Log.d("lol", "plusBottomMargin: " + plusBottomMargin);
		params2.bottomMargin = plusBottomMargin;
        pResult.setLayoutParams(params2);
        pResult.setTextSize(textNormalSizeR);
        long id = r.getId();
		long rankCount = -10;
		for(EventRankResult ev : event.getRankResult()) {
			if(ev.getRankId() == id) {
				rankCount = ev.getCount();
				break;
			}
		}
        pResult.setText(count + " " + rankCount);
		lLayout.addView(pResult);
	}
	
	@UiThread
	public void updateView(List<RankType> eventRankTypes) {
		if(eventRankTypes != null) {
			for(RankType r : eventRankTypes) {
				if(r.getValues().length > 1) {  // radios
					addNewRadioLayout(r);
				}
				else if(r.getValues().length == 1) {
					addNewplus1View(r);
				}
			}
		}
	}

	protected String wifiIpAddress(Context context) {
	    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

	    // Convert little-endian to big-endian if needed
	    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
	        ipAddress = Integer.reverseBytes(ipAddress);
	    }
	    byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();
	    String ipAddressString;
	    try {
	        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
	    } catch (UnknownHostException ex) {
	    	ex.printStackTrace();
	        ipAddressString = null;
	    }
	    return ipAddressString;
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(Toaster.isOnline(getActivity())) {
			RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
		    String sessionId = null;
		    if(ApplicationFunctions.getInstance().getUserFunctions().getLoginSatus()) {
		    	sessionId = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().getSessionId();
		    }
		    String ip = wifiIpAddress(getActivity());
		    bg.rankEvent(sessionId, event.getEventId(), (Long)group.getTag(), (Integer)radioButton.getTag(), ip, group, this);
		}
	}

	

	@UiThread
	public void lockView(boolean success, View v) {
		if(success) {
			if(v instanceof TextView) {
				v.setEnabled(false);
			}
			else if(v instanceof RadioGroup) {
				RadioGroup group = (RadioGroup) v;
				for(int i = 0; i < group.getChildCount(); i++){
				    ((RadioButton)group.getChildAt(i)).setEnabled(false);
				}
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if(Toaster.isOnline(getActivity())) {
		    String sessionId = null;
		    if(ApplicationFunctions.getInstance().getUserFunctions().getLoginSatus()) {
		    	sessionId = ApplicationFunctions.getInstance().getUserFunctions().getLoggedInUser().getSessionId();
		    }
		    String ip = wifiIpAddress(getActivity());
		    bg.rankEvent(sessionId, event.getEventId(), (Long)v.getTag(), 1, ip, v, this);
		}
	}
	
}