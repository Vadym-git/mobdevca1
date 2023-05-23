package ie.mvo.simplecryptocoininfo.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.HashMap;

import ie.mvo.simplecryptocoininfo.CoinDataContainer;
import ie.mvo.simplecryptocoininfo.adapters.FavoritesCoinAdapter;
import ie.mvo.simplecryptocoininfo.dataUtils.DataBase;
import ie.mvo.simplecryptocoininfo.dataUtils.DbHelper;
import ie.mvo.simplecryptocoininfo.databinding.LayoutFavoriteCoinsFragmentBinding;
import ie.mvo.simplecryptocoininfo.webUtils.Binance;
import ie.mvo.simplecryptocoininfo.webUtils.CoinGecko;

public class FragmentFavoriteCoins extends Fragment {
    private LayoutFavoriteCoinsFragmentBinding binding;
    private FavoritesCoinAdapter adapter;
    private DbHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FavoritesCoinAdapter(requireContext());
        dbHelper = DbHelper.getInstance(DataBase.getInstance(requireContext()).getWritableDatabase());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutFavoriteCoinsFragmentBinding.inflate(inflater, container, false);
        binding.favoritesCoinList.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.favoritesCoinList.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDb();
        if (dbHelper.getRowCount() > 0){
            binding.favoriteHelpText.setVisibility(View.GONE);
        }else {
            binding.favoriteHelpText.setVisibility(View.VISIBLE);
        }
    }

    private void updateDb() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DbHelper dbHelper = DbHelper.getInstance(DataBase.getInstance(getContext()).getWritableDatabase());
                for (HashMap<String, String> coin : dbHelper.getAllCoins()) {
                    String name = coin.get("symbol");
                    String coinRequest = Binance.getCoin(name);
                    CoinDataContainer coinData = Binance.parseBaseInfoCoin(coinRequest);
                    if (coinData == null){
                        name = coin.get("coin_id");
                        coinRequest = CoinGecko.getCoin(name);
                        coinData = CoinGecko.parseBaseInfoCoin(coinRequest);
                    }
                    dbHelper.updateCoinInfo(coin.get("coin_id"), "price", coinData.get("price"));
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dbHelper.notifySubscribers();
                    }
                });
            }
        }).start();
    }
}