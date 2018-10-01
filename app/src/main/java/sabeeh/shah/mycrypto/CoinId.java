package sabeeh.shah.mycrypto;

import com.google.firebase.firestore.Exclude;

import io.reactivex.annotations.NonNull;

public class CoinId {
    @Exclude
    public String CoinId;

    public <T extends CoinId> T withId(@NonNull final String id){
        this.CoinId = id;
        return (T) this;
    }
}
