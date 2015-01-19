package com.jookershop.linefriend.lucky;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.jookershop.linefriend.Constants;
import com.jookershop.linefriend.util.AccountUtil;
import com.jookershop.linefriend.util.AdUtil;
import com.jookershop.linefriend.util.Message;
import com.jookershop.linefriend3en.R;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.nostra13.universalimageloader.core.DisplayImageOptions;


/**
 * A placeholder fragment containing a simple view.
 */
public class WinnerLuckyFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String ARG_SECTION_NUMBER = "section_number";

	private ListView gridView;
	DisplayImageOptions options;
	private ProgressBar progressBar1;
	private TextView noUser;

	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static WinnerLuckyFragment newInstance() {
		WinnerLuckyFragment fragment = new WinnerLuckyFragment();
		return fragment;
	}

	public WinnerLuckyFragment() {
		options = new DisplayImageOptions.Builder()
//		.showImageOnLoading(R.drawable.emptyhead)
		.showImageForEmptyUri(R.drawable.noimage)
		.showImageOnFail(R.drawable.noimage)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.resetViewBeforeLoading(true)
		.build();		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final SharedPreferences sp = this.getActivity().getSharedPreferences("linefriend", Context.MODE_APPEND);
		
		View rootView = inflater.inflate(R.layout.fragment_winner_lucky, container,
				false);
		progressBar1 = (ProgressBar)rootView.findViewById(R.id.progressBar1);

		final AdView adView = (AdView) rootView.findViewById(R.id.adView);
		AdUtil.showAD(this.getActivity(), adView);
		noUser = (TextView) rootView.findViewById(R.id.textView1);
		
		gridView = (ListView) rootView
				.findViewById(R.id.grid_view);


		gridView.setAdapter(new WinnerLuckyAdapter(this.getActivity(),
				new ArrayList<LuckyWinnerItem>(), options, this));
		
		loadItmes(true);		
		return rootView;
	}

	public void loadItmes(final boolean first) {
		String uid = URLEncoder.encode(AccountUtil.getUid(this.getActivity()));
		String url = Constants.BASE_URL + "lucky/winner/list?uid=" + uid;
		Log.d(Constants.TAG, "finish lucky url " + url );
		AsyncHttpGet ahg = new AsyncHttpGet(url);
		AsyncHttpClient.getDefaultInstance().executeJSONArray(ahg, new AsyncHttpClient.JSONArrayCallback() {
		    @Override
		    public void onCompleted(Exception e, AsyncHttpResponse response, final JSONArray result) {
				if(WinnerLuckyFragment.this != null && WinnerLuckyFragment.this.getActivity()!= null)
					WinnerLuckyFragment.this.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						progressBar1.setVisibility(View.INVISIBLE);
				        if(first && result != null && result.length() == 0) noUser.setVisibility(View.VISIBLE);
				        else noUser.setVisibility(View.INVISIBLE);
					}
				});
				
		    	if (e != null) {
		            e.printStackTrace();
		            return;
		        }
		    	


				final ArrayList<LuckyWinnerItem> res = new ArrayList<LuckyWinnerItem>();
				for (int index = 0; index < result.length(); index++) {
					try {

						JSONObject jo = result.getJSONObject(index);
						res.add(LuckyWinnerItem.genPostItem(jo));
					} catch (JSONException e1) {
						e1.printStackTrace();
					}
				}
				if(WinnerLuckyFragment.this != null && WinnerLuckyFragment.this.getActivity()!= null)
					WinnerLuckyFragment.this.getActivity().runOnUiThread(new Runnable() {
					public void run() {
						WinnerLuckyAdapter ia = (WinnerLuckyAdapter) gridView.getAdapter();
						for(int index = 0; index < res.size(); index ++)
						ia.add(res.get(index));
						ia.notifyDataSetChanged();
					}
				});
		    }
		});		
	}
}
