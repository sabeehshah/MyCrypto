package sabeeh.shah.mycrypto;

public class Coin extends CoinId {

    public String coin_name;
    public String exchange_name;
    public String user_id;
    public int amount;

    public Coin(){

    }

    public Coin(String coin_name, String ex_name, String user_id, int amount) {
        this.coin_name = coin_name;
        this.exchange_name = ex_name;
        this.user_id = user_id;
        this.amount = amount;
    }

    public String getCoin_name() {
        return coin_name;
    }

    public void setCoin_name(String coin_name) {
        this.coin_name = coin_name;
    }

    public String getExchange_name() {
        return exchange_name;
    }

    public void setExchange_name(String exchange_name) {
        this.exchange_name = exchange_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
