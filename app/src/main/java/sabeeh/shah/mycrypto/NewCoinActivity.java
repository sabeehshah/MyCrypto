package sabeeh.shah.mycrypto;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NewCoinActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    private String current_user_id;

    private Toolbar newCoinToolbar;
    private EditText exchangeName;
    private EditText coinAmount;
    private Spinner coinList;
    private Button addCoinBtn;
    private TextView testText;
    private ProgressBar newCoinProgress;

    private RequestQueue mQueue;

    private ArrayList coins;
    private ArrayList<String> coinNames;

    String coin_name;
    String exchange_name;
    int amount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_coin);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        current_user_id = mAuth.getCurrentUser().getUid();

        newCoinToolbar = findViewById(R.id.new_coin_toolbar);
        setSupportActionBar(newCoinToolbar);
        getSupportActionBar().setTitle("Add new coin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        exchangeName = findViewById(R.id.exchange_name);
        coinAmount = findViewById(R.id.coin_amount);
        coinList = findViewById(R.id.coin_list);
        addCoinBtn = findViewById(R.id.add_coin_btn);
        newCoinProgress = findViewById(R.id.new_coin_progress);

        coins = new ArrayList<JSONObject>();
        coinNames = new ArrayList<>();



        setupCoinList();

        addCoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exchange_name = exchangeName.getText().toString();
                String c_amount = coinAmount.getText().toString();

                if(!TextUtils.isEmpty(exchange_name) && !TextUtils.isEmpty(c_amount) && !TextUtils.isEmpty(coin_name)){

                    newCoinProgress.setVisibility(View.VISIBLE);

                    Map<String, Object> coinMap = new HashMap<>();
                    coinMap.put("amount", Integer.parseInt(c_amount));
                    coinMap.put("exchange_name", exchange_name);
                    coinMap.put("coin_name", coin_name);
                    coinMap.put("created_by", current_user_id);
                    coinMap.put("created_date", FieldValue.serverTimestamp());

                    firebaseFirestore.collection("Coins").add(coinMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(NewCoinActivity.this, "Coin Added", Toast.LENGTH_LONG).show();
                                Intent mainIntent = new Intent(NewCoinActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();

                            }else{

                            }

                            newCoinProgress.setVisibility(View.INVISIBLE);

                        }
                    });



                }
            }
        });









    }

    public void setupCoinList(){

        mQueue = Volley.newRequestQueue(this);

        String url = "https://api.coinmarketcap.com/v1/ticker/?limit=10";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    // Log.d("response", response.toString());

                    for(int i = 0; i < response.length(); i++){
                        JSONObject obj = response.getJSONObject(i);
                        coinNames.add(obj.getString("name"));
                        Log.d("size",String.valueOf(coinNames.size()));

                        Log.d("obj", obj.getString("name").toString());
                    }



                    ArrayAdapter<String> adp = new ArrayAdapter<String>(NewCoinActivity.this,android.R.layout.simple_spinner_dropdown_item, coinNames);
                    adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    coinList.setAdapter(adp);
                    // coinList.setVisibility(View.VISIBLE);
                    coinList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            coin_name = coinList.getSelectedItem().toString();
                            Toast.makeText(NewCoinActivity.this,coinList.getSelectedItem().toString(),Toast.LENGTH_LONG).show();



                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);


    }
}
