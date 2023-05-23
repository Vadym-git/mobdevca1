package ie.mvo.simplecryptocoininfo.screens;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import ie.mvo.simplecryptocoininfo.CoinDataContainer;
import ie.mvo.simplecryptocoininfo.MainActivity;
import ie.mvo.simplecryptocoininfo.R;
import ie.mvo.simplecryptocoininfo.databinding.LayoutSearchCoinFragmentBinding;
import ie.mvo.simplecryptocoininfo.webUtils.CoinGecko;

public class FragmentSearchCoin extends Fragment {
    private LayoutSearchCoinFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LayoutSearchCoinFragmentBinding.inflate(inflater, container, false);
        binding.searchCoinField.requestFocus();
        binding.searchCoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPressed();
            }
        });
        return binding.getRoot();
    }

    private void searchPressed() {
        String coinNameRequest = binding.searchCoinField.getText().toString().toLowerCase();
        if (coinNameRequest.isEmpty()){
            binding.searchCoinField.setError("Type coin name");
            return;
        }
        binding.progressBarSearch.setVisibility(View.VISIBLE);
        binding.searchCoinButton.setEnabled(false);
        // starting run new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                String coin = CoinGecko.getCoin(coinNameRequest);
                CoinDataContainer coinInfo = CoinGecko.parseBaseInfoCoin(coin);
                if (coinInfo == null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.searchCoinButton.setEnabled(true);
                            binding.progressBarSearch.setVisibility(View.INVISIBLE);
                            MainActivity.showAlert(requireActivity(), "Coin is not found");
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.searchCoinButton.setEnabled(true);
                            binding.searchCoinField.setText("");
                            binding.progressBarSearch.setVisibility(View.INVISIBLE);
                        }
                    });
                    getParentFragmentManager()
                            .beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.fragmentMainHolder, FragmentSelectedCoin.newInstance(coinInfo))
                            .commit();
                }
            }
        }).start();
    }
}
