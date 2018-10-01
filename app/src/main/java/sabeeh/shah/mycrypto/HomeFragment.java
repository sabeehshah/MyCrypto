package sabeeh.shah.mycrypto;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView coin_list_view;
    private List<Coin> coin_list;
    private List<JSONObject> allCoins;

    private RequestQueue mQueue;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private CoinRecyclerAdapter coinRecyclerAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        coin_list = new ArrayList<>();
        allCoins = new ArrayList<>();
        coin_list_view = view.findViewById(R.id.coin_list_view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();



        if(mAuth.getCurrentUser() != null){
            mQueue = Volley.newRequestQueue(this.getActivity());

            final String user_id = mAuth.getCurrentUser().getUid().toString();
            String url = "https://api.coinmarketcap.com/v1/ticker/?limit=10";

            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        // Log.d("response", response.toString());

                        for(int i = 0; i < response.length(); i++){
                            JSONObject obj = response.getJSONObject(i);
                            allCoins.add(obj);
                            Log.d("OBJECT", obj.toString());
                        }

                        coinRecyclerAdapter = new CoinRecyclerAdapter(coin_list, allCoins);
                        coin_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
                        coin_list_view.setAdapter(coinRecyclerAdapter);

                        Query query = firebaseFirestore.collection("Coins").whereEqualTo("created_by", mAuth.getCurrentUser().getUid().toString());

                        query.addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                                if(mAuth.getCurrentUser() != null){
                                    if(!documentSnapshots.isEmpty()){
                                        for(DocumentChange doc: documentSnapshots.getDocumentChanges()){
                                            if(doc.getType() == DocumentChange.Type.ADDED){

                                                String coinId = doc.getDocument().getId();
                                                Coin coin = doc.getDocument().toObject(Coin.class).withId(coinId);
                                                coin_list.add(coin);

                                                coinRecyclerAdapter.notifyDataSetChanged();

                                            }
                                            if(doc.getType() == DocumentChange.Type.REMOVED){
                                                String coinId = doc.getDocument().getId();
                                                Coin coin = doc.getDocument().toObject(Coin.class).withId(coinId);
                                                int index = coin_list.indexOf(coin);
                                                coin_list.remove(coin);
                                                coinRecyclerAdapter.notifyDataSetChanged();
                                                Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_LONG).show();


                                            }
                                        }
                                    }
                                }


                            }
                        });

                        // Log.d("SIZE", String.valueOf(allCoins.size()));




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


















        // Inflate the layout for this fragment
        return view;
    }

}
