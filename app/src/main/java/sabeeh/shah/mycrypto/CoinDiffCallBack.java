package sabeeh.shah.mycrypto;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class CoinDiffCallBack extends DiffUtil.Callback{

    private final List<Coin> mOldCoinList;
    private final List<Coin> mNewCoinList;

    public CoinDiffCallBack(List<Coin> oldCoinList, List<Coin> newCoinList){
        this.mOldCoinList = oldCoinList;
        this.mNewCoinList = newCoinList;
    }

    @Override
    public int getOldListSize() {
        return mOldCoinList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewCoinList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldCoinList.get(oldItemPosition).getCoin_name() == mNewCoinList.get(newItemPosition).getCoin_name();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Coin oldCoin = mOldCoinList.get(oldItemPosition);
        final Coin newCoin = mNewCoinList.get(newItemPosition);

        return oldCoin.getCoin_name().equals(newCoin.getCoin_name());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
