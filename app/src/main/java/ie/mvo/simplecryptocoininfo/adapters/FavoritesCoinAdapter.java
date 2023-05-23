package ie.mvo.simplecryptocoininfo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import ie.mvo.simplecryptocoininfo.CoinDataContainer;
import ie.mvo.simplecryptocoininfo.MainActivity;
import ie.mvo.simplecryptocoininfo.R;
import ie.mvo.simplecryptocoininfo.dataUtils.DataBase;
import ie.mvo.simplecryptocoininfo.dataUtils.DbHelper;
import ie.mvo.simplecryptocoininfo.databinding.LayoutSingleItemBinding;
import ie.mvo.simplecryptocoininfo.intergaces.CoinListener;
import ie.mvo.simplecryptocoininfo.screens.FragmentSelectedCoin;
import ie.mvo.simplecryptocoininfo.webUtils.CoinGecko;

public class FavoritesCoinAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CoinListener, View.OnClickListener {
    private DbHelper dbHelper;

    public FavoritesCoinAdapter(Context context) {
        this.dbHelper = DbHelper.getInstance(DataBase.getInstance(context).getWritableDatabase());
        dbHelper.addListener(this);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutSingleItemBinding binding = LayoutSingleItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        binding.getRoot().setOnClickListener(this);
        binding.singleItemMore.setOnClickListener(this);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        CoinDataContainer coin_data = dbHelper.selectCoinByPosition(position);
        viewHolder.name.setText(coin_data.get("name"));
        viewHolder.price.setText(coin_data.get("price"));
        viewHolder.itemView.setTag(coin_data);
        if (coin_data.get("image") != null) {
            Glide.with(viewHolder.icon)
                    .load(coin_data.get("image"))
                    .circleCrop()
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(viewHolder.icon);
        } else {
            viewHolder.icon.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    @Override
    public int getItemCount() {
        return dbHelper.getRowCount();
    }

    @Override
    public void updateInfo() {
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        MainActivity context = (MainActivity) v.getContext();
        CoinDataContainer coinData = (CoinDataContainer) v.getTag();
        ProgressBar progressBar = context.findViewById(R.id.progressBarFavoriteCoins);
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String coin = CoinGecko.getCoin(coinData.get("coin_id"));
                if (coin.isEmpty()) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            MainActivity.showAlert(context, "Sorry, the server is to busy\n" +
                                    "Try again later");
                        }
                    });
                    return;
                }
                CoinDataContainer coinInfo = CoinGecko.parseBaseInfoCoin(coin);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        context.getSupportFragmentManager()
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.fragmentMainHolder, FragmentSelectedCoin.newInstance(coinInfo))
                                .commit();
                    }
                });
            }
        }).start();
        // may be to do PorUp menu
//        if (v.getId() == R.id.singleItemMore) {
//            showPopupMenu(v);
//        } else {
//        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenu().add(0, 1, Menu.NONE, "Move Up");
        popupMenu.getMenu().add(0, 2, Menu.NONE, "Move Down");
        popupMenu.getMenu().add(0, 3, Menu.NONE, "Delete");
        popupMenu.show();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView price, name;
        public ImageView icon;
        public ImageButton more;

        // we have to initialize fields for adapter
        public ViewHolder(LayoutSingleItemBinding itemBinding) {
            super(itemBinding.getRoot());
            icon = itemBinding.singleItemIcon;
            name = itemBinding.singleItemName;
            price = itemBinding.singleItemPrice;
            more = itemBinding.singleItemMore;
        }
    }
}
