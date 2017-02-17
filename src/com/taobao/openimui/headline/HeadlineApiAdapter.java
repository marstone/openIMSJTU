package com.taobao.openimui.headline;

/**
 * kanged from http://www.androidhive.info/2012/02/android-custom-listview-with-image-and-text/
 * Created by marstone on 2/16/17.
 */


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.openIMUISJTU.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HeadlineApiAdapter extends BaseAdapter {

    private ArrayList<Map<String,String>> data;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    private String mUserId;

    private static final String API_NEWS = "http://202.121.178.197/dang/news?userid=%s";
    private static final String API_PREF = "http://202.121.178.197/dang/preference/%s/news/%d";
    static final String KEY_SONG = "song"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_TITLE = "title";
    static final String KEY_ARTIST = "artist";
    static final String KEY_DURATION = "duration";
    static final String KEY_THUMB_URL = "thumb_url";


    public HeadlineApiAdapter(LayoutInflater layoutInflater, String userId) throws IOException, JSONException {
        /*
        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(URL); // getting XML from URL
        Document doc = parser.getDomElement(xml); // getting DOM element

        NodeList nl = doc.getElementsByTagName(KEY_SONG);
        // looping through all song nodes <song>
        for (int I = 0; I < nl.getLength(); i++) {
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            Element e = (Element) nl.item(i);
            // adding each child node to HashMap key => value
            map.put(KEY_ID, parser.getValue(e, KEY_ID));
            map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
            map.put(KEY_ARTIST, parser.getValue(e, KEY_ARTIST));
            map.put(KEY_DURATION, parser.getValue(e, KEY_DURATION));
            map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));

            // adding HashList to ArrayList
            songsList.add(map);
        }
        */
        mUserId = userId;
        inflater = layoutInflater;
        imageLoader = new ImageLoader(layoutInflater.getContext().getApplicationContext());
    }

    String loadURI(String uri)  throws IOException, JSONException {
        Log.i("loadURI", "URI:" + uri);
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        // read the response
        Log.i("loadURI", "Response Code: " + conn.getResponseCode());
        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response=new StringBuilder();
        for(String line;(line=br.readLine())!=null;)response.append(line+"\n");
        return response.toString();
    }

    public String updateFavorite(int id)  throws IOException, JSONException {
        String response = this.loadURI(String.format(API_PREF, mUserId, id));
        Log.i("UpdateFavorite", response);
        JSONObject news = new JSONObject(response);
        return news.getString("status");
    }

    Map<String, String> newsToMap(JSONObject news) throws JSONException {
        HashMap<String, String> map = new HashMap<>();
        /**
         * id : 26
         title : "“两学一做”系列：习近平为什么提出要重温这篇著作"
         content : "【学习进行时】最近，习近平总书记就学习毛泽东同志《党委会的工作方法》作出重要批示，对各级党委（党组）领导班子成员特别是主要负责同志重温这篇著作提出明确要求。中组部也发出《通知》，把这篇文章纳入“两学一做”的重要内容。那么，《党委会的工作方法》是怎样一篇文章，总书记为何如此重视？新华网《学习进行时》原创品牌栏目“讲习所”今日推出《习近平为什么提出要重温这篇著作》,为您解析。"
         imageurl : "http://cms-bucket.nosdn.127.net/catchpic/8/8C/8CECD293910EAB9F2A7F90551CBA49BA.jpg"
         time : "2017-01-20 11:13:07"
         */
        // adding each child node to HashMap key => value
        map.put(KEY_ID, Integer.toString(news.getInt("id")));
        map.put(KEY_TITLE, news.getString("title"));
        map.put(KEY_ARTIST, news.getString("content"));
        map.put(KEY_DURATION, news.getString("time"));
        map.put(KEY_THUMB_URL, news.getString("imageurl"));
        return  map;
    }

    public ArrayList<Map<String,String>> loadHeadlines() throws IOException, JSONException {
        String response = this.loadURI(String.format(API_NEWS, mUserId));

        JSONObject news = new JSONObject(response);
        String status = news.getString("status");
        data = new ArrayList<>();
        if(!"ok".equalsIgnoreCase(status)) {
            Map<String, String> map = new HashMap<>();
            // adding each child node to HashMap key => value
            map.put(KEY_ID, "0");
            map.put(KEY_TITLE, "Headlines loading failed.");
            data.add(map);
        } else {
            JSONArray array = news.getJSONArray("news_recommended");
            if(null != array && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    data.add(this.newsToMap(array.getJSONObject(i)));
                }
            }
            array = news.getJSONArray("news_more");
            if(null != array && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    data.add(this.newsToMap(array.getJSONObject(i)));
                }
            }

            if(data.isEmpty()) {
                Map<String, String> map = new HashMap<>();
                // adding each child node to HashMap key => value
                map.put(KEY_ID, "0");
                map.put(KEY_TITLE, "No headline for you right now.");
                data.add(map);
            }
        }
        return data;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return this.data.get(position);
    }

    public long getItemId(int position) {
        return Long.parseLong(this.data.get(position).get(KEY_ID));
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.sjtu_headline_row, null);

        TextView title = (TextView) vi.findViewById(R.id.headline_title); // title
        TextView artist = (TextView) vi.findViewById(R.id.headline_artist); // artist name
        TextView duration = (TextView) vi.findViewById(R.id.headline_duration); // duration
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.headline_list_image); // thumb image

        Map<String, String> song = data.get(position);
        vi.setId(Integer.parseInt(song.get(HeadlineApiAdapter.KEY_ID)));
        // Setting all values in listview
        title.setText(song.get(HeadlineApiAdapter.KEY_TITLE));
        artist.setText(song.get(HeadlineApiAdapter.KEY_ARTIST));
        duration.setText(song.get(HeadlineApiAdapter.KEY_DURATION));
        imageLoader.DisplayImage(song.get(HeadlineApiAdapter.KEY_THUMB_URL), thumb_image);
        return vi;

    }

}

