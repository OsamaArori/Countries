package com.example.countries;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListClass l = new ListClass();
        listView = findViewById(R.id.list);
        l.getData(this,listView);

    }
}
class adp extends ArrayAdapter<String> {
    public Context context;
    ArrayList<String> name;
    ArrayList<String> currency;
    ArrayList<String> pic;
    public adp(Context c, ArrayList<String> name, ArrayList<String> currency , ArrayList<String> pic) {
        super(c, R.layout.list_countries, R.id.name, name);
        this.context = c;
        this.name = name;
        this.currency = currency;
        this.pic=pic;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row = inf.inflate(R.layout.list_countries, parent, false);
        TextView name1 = (TextView) row.findViewById(R.id.name);
        TextView currency1 = (TextView) row.findViewById(R.id.currency);
        ImageView pic1=(ImageView)row.findViewById(R.id.pic) ;
        name1.setText(name.get(position));
        currency1.setText(currency.get(position));
        GlideToVectorYou
                .init()
                .with(context)
                .withListener(new GlideToVectorYouListener() {
                    @Override
                    public void onLoadFailed() {
                        Toast.makeText(context, "Load failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResourceReady() {
                        Toast.makeText(context, "Image ready", Toast.LENGTH_SHORT).show();
                    }
                })
                .load(Uri.parse(pic.get(position)), pic1);
        return row;
    }
}
class ListClass extends AppCompatActivity {
    adp ad;
    ArrayList<String> name=new ArrayList<String>();
    ArrayList<String> currency=new ArrayList<String>();
    ArrayList<String> pic=new ArrayList<String>();
    public void getData(Context c, ListView listView) {
        RequestQueue rq = Volley.newRequestQueue(c);
        StringRequest jsonObjectRequest = new StringRequest
                (Request.Method.POST, "https://countriesnow.space/api/v0.1/countries/info?returns=currency,flag,unicodeFlag,dialCode", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONArray UserDetailArray = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < UserDetailArray.length(); i++) {
                                name.add(UserDetailArray.getJSONObject(i).getString("name"));
                                if(!UserDetailArray.getJSONObject(i).optString("currency").equals("")){
                                    currency.add(UserDetailArray.getJSONObject(i).getString("currency"));
                                }else
                                    currency.add("none");
                                if(!UserDetailArray.getJSONObject(i).optString("flag").equals("")){
                                    pic.add(UserDetailArray.getJSONObject(i).getString("flag"));
                                }else
                                    pic.add("none");
                            }
                            ad=new adp(c,name,currency,pic);
                            listView.setAdapter(ad);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast.makeText(c,"Error Response 102",Toast.LENGTH_SHORT).show();
                    }
                }
                ){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String>params=new HashMap<String, String>();
                return  params;
            }
        };
        rq.add(jsonObjectRequest);
    }
}