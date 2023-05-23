package ie.mvo.simplecryptocoininfo.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import ie.mvo.simplecryptocoininfo.screens.FragmentFavoriteCoins;
import ie.mvo.simplecryptocoininfo.screens.FragmentSearchCoin;

public class TabViewAdapter extends FragmentStateAdapter {
    public TabViewAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FragmentFavoriteCoins();
            case 1:
                return new FragmentSearchCoin();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
