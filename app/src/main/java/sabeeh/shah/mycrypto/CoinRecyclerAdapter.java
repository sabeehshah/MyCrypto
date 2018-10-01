package sabeeh.shah.mycrypto;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoinRecyclerAdapter extends RecyclerView.Adapter<CoinRecyclerAdapter.ViewHolder> {

    public List<Coin> coin_list;
    public List<JSONObject> all_coins;

    private FirebaseFirestore firebaseFirestore;

    private RequestQueue mQueue;

    public CoinRecyclerAdapter(List<Coin> coin_list, List<JSONObject> allCoins){
        this.coin_list = coin_list;
        this.all_coins = allCoins;


        Log.d("SIZE",String.valueOf(all_coins.size()));


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        firebaseFirestore = FirebaseFirestore.getInstance();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coin_list_item, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.setIsRecyclable(false);


        final String coinID = coin_list.get(position).CoinId;
        String coin_name_data = coin_list.get(position).getCoin_name();
        String exchange_data = coin_list.get(position).getExchange_name();
        int coin_amount = coin_list.get(position).getAmount();


        for(JSONObject obj : this.all_coins){
            try{
                if(obj.getString("name").equals(coin_name_data)){
                    String p = obj.getString("price_usd");
                    Double priceUSD = (Double.parseDouble(p));
                    String price_usd = String.format("%.2f",priceUSD);

                    String b = obj.getString("price_btc");
                    Double priceBTC = Double.parseDouble(b);
                    String price_btc = String.format("%.9f",priceBTC);

                    Double totalUSD = priceUSD * coin_amount;
                    String total_usd = String.format("%.2f", totalUSD);

                    String c = obj.getString("percent_change_24h");
                    Double change = Double.parseDouble(c);



                    holder.setPriceUSD("$" + price_usd);
                    holder.setPriceBTC(price_btc);
                    holder.setTotalUSD("$" + total_usd);
                    holder.setChange24hr(change);
                }
            }catch(JSONException e){
                Log.d("JSONException", e.getMessage());
            }
        }







        holder.setCoinName(coin_name_data);
        holder.setExchangeName(exchange_data);
        holder.setAmount(coin_amount);





        holder.coinDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseFirestore.collection("Coins").document(coinID).delete().addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                coin_list.remove(position);
                                CoinRecyclerAdapter.this.notifyDataSetChanged();
                                Log.d("Deleted","Coin deleted");



                            }
                        }
                );
            }
        });



    }

    public void updateCoinListItems(List<Coin> coins) {
        final CoinDiffCallBack diffCallback = new CoinDiffCallBack(this.coin_list, coins);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.coin_list.clear();
        this.coin_list.addAll(coins);
        diffResult.dispatchUpdatesTo(this);
    }





    @Override
    public int getItemCount() {
        return coin_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private TextView coinNameView;
        private TextView exchangeNameView;
        private TextView amountView;
        private TextView priceUSDView;
        private TextView priceBTCView;
        private TextView totalUSDView;
        private TextView change24hrView;

        private Button coinDeleteBtn;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            coinDeleteBtn = mView.findViewById(R.id.coin_delete_btn);
        }

        public void setCoinName(String coinName){
            coinNameView = mView.findViewById(R.id.lbl_coinName);
            coinNameView.setText(coinName);
        }

        public void setAmount(int amount){
            amountView = mView.findViewById(R.id.val_totalAmount);
            amountView.setText(String.valueOf(amount));
        }

        public void setExchangeName(String exchangeName){
            exchangeNameView = mView.findViewById(R.id.val_exchangeName);
            exchangeNameView.setText(exchangeName.toUpperCase());
        }

        public void setPriceUSD(String price){
            priceUSDView = mView.findViewById(R.id.val_priceUSD);
            priceUSDView.setText(price);
        }

        public void setPriceBTC(String price){
            priceBTCView = mView.findViewById(R.id.val_priceBTC);
            priceBTCView.setText(price);
        }

        public void setTotalUSD(String total){
            totalUSDView = mView.findViewById(R.id.val_total_usd);
            totalUSDView.setText(total);
        }

        public void setChange24hr(Double change){
            change24hrView = mView.findViewById(R.id.val_24hr);
            if(change < 0 ){
                change24hrView.setTextColor(Color.RED);
            }else{
                change24hrView.setTextColor(Color.GREEN);
            }
            change24hrView.setText(String.valueOf(change));


        }




    }

}
