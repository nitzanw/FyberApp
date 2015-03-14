package com.nitzandev.fyberapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.gson.Gson;
import com.nitzandev.fyberapp.utils.AeSimpleSHA1;
import com.nitzandev.fyberapp.utils.Response;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;


public class MainActivity extends Activity implements IDownloadListener {

    private static final String API_URL = "http://api.sponsorpay.com/feed/v1/offers.json?";
    private static final String REQUEST = "appid=2070&google_ad_id=%s&google_ad_id_limited_tracking_enabled=%s&ip=109.235.143.113&locale=de&offer_types=112&os_version=%s&uid=spiderman&timestamp=%s&hashkey=%s";
    private static final String PRE_HASHED_KEY = "appid=2070&google_ad_id=%s&google_ad_id_limited_tracking_enabled=%s&ip=109.235.143.113&locale=de&offer_types=112&os_version=%s&timestamp=%s&uid=spiderman&1c915e3b5d42d05136185030892fbb846c278927";

    private Response.Offer[] offers = new Response.Offer[0];
    private OfferAdapter mAdapter;
    private RecyclerView recList;
    private TextView emptyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        new AsyncDownloader(this).download(this);
    }

    private void initView() {
        recList = (RecyclerView) findViewById(R.id.offerList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        emptyView = (TextView) findViewById(R.id.empty);
        mAdapter = new OfferAdapter();
        recList.setAdapter(mAdapter);
    }

    private void showEmptyView() {
        recList.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void downloadCompleted(Response.Offer[] data) {
        if (data == null || data.length == 0) {
            showEmptyView();
        } else {
            offers = data;
            mAdapter.notifyDataSetChanged();
        }
    }

    //Wrapper class that holds the Downloading ApiRequestTask
    static public class AsyncDownloader {
        private IDownloadListener downloadListener;

        public AsyncDownloader(IDownloadListener downloadListener) {
            this.downloadListener = downloadListener;
        }

        //cause the actual download process to start
        public void download(Context context) {
            new ApiRequestTask(context).execute();

        }

        public class ApiRequestTask extends AsyncTask<String, Void, Response.Offer[]> {
            final public CountDownLatch signal = new CountDownLatch(1);

            private Context context;

            ApiRequestTask(Context context) {
                this.context = context;
            }

            @Override
            protected Response.Offer[] doInBackground(String... params) {
                String request = createApiRequest();
                return getData(request);
            }

            @Override
            protected void onPostExecute(Response.Offer[] data) {
                downloadListener.downloadCompleted(data);
            }

            public Response.Offer[] getData(String request) {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(request).openConnection();
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    if (code != 200) {
                        Log.e("Request Failed: ", String.valueOf(code));
                        Log.e("Request: ", request);
                        return null;
                    }

                    InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                    BufferedReader br = new BufferedReader(reader);
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        total.append(line);
                    }
                    Log.d("result: ", total.toString());

                    Gson gson = new Gson();
                    Response response = gson.fromJson(total.toString(), Response.class);
                    return response.getOffers();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            //creates the api request according to the Api rules
            public String createApiRequest() {
                try {
                    String googleAdId = AdvertisingIdClient.getAdvertisingIdInfo(context).getId();
                    boolean isLimitedTrackingEnabled = AdvertisingIdClient.getAdvertisingIdInfo(context).isLimitAdTrackingEnabled();
                    String timeStamp = String.valueOf(System.currentTimeMillis() / 1000L);
                    String stringToHash = String.format(PRE_HASHED_KEY, googleAdId, String.valueOf(isLimitedTrackingEnabled), Build.VERSION.RELEASE, timeStamp);
                    String hashKey = AeSimpleSHA1.SHA1(stringToHash);
                    return API_URL + String.format(REQUEST, googleAdId, String.valueOf(isLimitedTrackingEnabled), Build.VERSION.RELEASE, timeStamp, hashKey);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }


    public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferViewHolder> {
        @Override
        public OfferViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.offer_item, viewGroup, false);

            return new OfferViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(OfferViewHolder offerViewHolder, int i) {

            Response.Offer offer = offers[i];
            offerViewHolder.vTitle.setText(offer.getTitle());
            offerViewHolder.vTeaser.setText(offer.getTeaser());
            offerViewHolder.vPayout.setText(getString(R.string.payout) + " " + offer.getPayout());
            Uri uri = Uri.parse(offer.getThumbnail().getHires());
            Picasso.with(MainActivity.this).load(uri).into(offerViewHolder.vThumbnail);
        }

        @Override
        public int getItemCount() {
            return offers.length;
        }

        public class OfferViewHolder extends RecyclerView.ViewHolder {

            protected TextView vTitle;
            protected TextView vTeaser;
            protected ImageView vThumbnail;
            protected TextView vPayout;

            public OfferViewHolder(View itemView) {
                super(itemView);
                vTitle = (TextView) itemView.findViewById(R.id.title);
                vTeaser = (TextView) itemView.findViewById(R.id.teaser);
                vThumbnail = (ImageView) itemView.findViewById(R.id.image);
                vPayout = (TextView) itemView.findViewById(R.id.payout);

            }
        }
    }
}
