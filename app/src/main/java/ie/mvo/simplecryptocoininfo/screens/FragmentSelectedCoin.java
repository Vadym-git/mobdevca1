package ie.mvo.simplecryptocoininfo.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import ie.mvo.simplecryptocoininfo.CoinDataContainer;
import ie.mvo.simplecryptocoininfo.R;
import ie.mvo.simplecryptocoininfo.dataUtils.DataBase;
import ie.mvo.simplecryptocoininfo.dataUtils.DbHelper;
import ie.mvo.simplecryptocoininfo.databinding.LayoutSelectedCoinFragmentBinding;

public class FragmentSelectedCoin extends MainFragment {
    private LayoutSelectedCoinFragmentBinding binding;
    private CoinDataContainer coinData = null;
    private DbHelper dbHelper;

    boolean isCoinInDb = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            coinData = getArguments().getParcelable("coinData");
        }
        this.dbHelper = DbHelper.getInstance(DataBase.getInstance(requireContext()).getWritableDatabase());
        isCoinInDb = dbHelper.isCoinInDataBase(coinData.get("coin_id"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutSelectedCoinFragmentBinding.inflate(inflater, container, false);
        if (coinData != null) {
            if (coinData.get("image") != null) {
                Glide.with(binding.coinIcon)
                        .load(coinData.get("image"))
                        .circleCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(binding.coinIcon);
            } else {
                binding.coinIcon.setImageResource(R.drawable.ic_launcher_foreground);
            }
            binding.coinPrice.setText(coinData.get("price"));
            binding.coinSymbol.setText(coinData.get("symbol"));
            binding.coinName.setText(coinData.get("name"));
            binding.coinHigh24hData.setText(coinData.get("high24h"));
            binding.coin24HChangeData.setText(coinData.get("change24h"));
            binding.coinLow24HData.setText(coinData.get("low24h"));
            binding.coinVolumeData.setText(coinData.get("volume"));
            binding.coinMarketCapData.setText(coinData.get("marketCap"));
            binding.coinRankData.setText(coinData.get("rank"));
        }
        binding.buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        binding.buttonSaveCoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCoinPressed();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isCoinInDb) {
            binding.buttonSaveCoin.setImageResource(R.drawable.delete_icon);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    dbHelper.updateCoinInfo(coinData.get("coin_id"), "price", coinData.get("price"));
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

    private void saveCoinPressed() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!isCoinInDb) {
                    String name = coinData.get("name");
                    String symbol = coinData.get("symbol");
                    String coinId = coinData.get("coin_id");
                    String image = coinData.get("image");
                    String price = coinData.get("price");
                    String change24h = coinData.get("change24h");
                    String high24h = coinData.get("high24h");
                    String low24h = coinData.get("low24h");
                    String volume = coinData.get("volume");
                    String marketCap = coinData.get("marketCap");
                    String rank = coinData.get("rank");
                    dbHelper.insertInToCoins(name, symbol, coinId, image, price, change24h, high24h, low24h,
                            volume, marketCap, rank, dbHelper.maxPosition() + 1);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.buttonSaveCoin.setImageResource(R.drawable.delete_icon);
                        }
                    });
                } else {
                    dbHelper.deleteCoin(coinData.get("coin_id"));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.buttonSaveCoin.setImageResource(R.drawable.baseline_favorite_border_24);
                        }
                    });
                }
                isCoinInDb = dbHelper.isCoinInDataBase(coinData.get("coin_id"));
            }
        }).start();
    }

    public static FragmentSelectedCoin newInstance(CoinDataContainer coinDataContainer) {
        Bundle args = new Bundle();
        args.putParcelable("coinData", coinDataContainer);
        FragmentSelectedCoin fragment = new FragmentSelectedCoin();
        fragment.setArguments(args);
        return fragment;
    }
}
