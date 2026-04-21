package com.irenaprokhyra.levelife.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Furniture;
import com.irenaprokhyra.levelife.util.FurnitureDrawableResolver;
import java.util.ArrayList;
import java.util.List;

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.FurnitureViewHolder> {

    private List<Furniture> furnitureList = new ArrayList<>();
    private final OnFurnitureBuyClickListener listener;
    private int currentBerryBalance = 0;
    private int currentEcoBalance = 0;

    private List<Integer> ownedFurnitureIds = new ArrayList<>();


    public interface OnFurnitureBuyClickListener {
        void onBuyClick(Furniture furniture);
    }

    public FurnitureAdapter(OnFurnitureBuyClickListener listener) {
        this.listener = listener;
    }

    public void setFurnitureList(List<Furniture> list) {
        this.furnitureList = (list != null) ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setBalances(int berryBalance, int ecoBalance) {
        this.currentBerryBalance = berryBalance;
        this.currentEcoBalance = ecoBalance;
        notifyDataSetChanged();
    }

    public void setOwnedFurnitureIds(List<Integer> ownedIds) {
        this.ownedFurnitureIds = (ownedIds != null) ? new ArrayList<>(ownedIds) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void markAsOwned(int furnitureId) {
        if (!this.ownedFurnitureIds.contains(furnitureId)) {
            this.ownedFurnitureIds.add(furnitureId);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public FurnitureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_furniture, parent, false);
        return new FurnitureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FurnitureViewHolder holder, int position) {
        Furniture furniture = furnitureList.get(position);
        holder.bind(furniture, listener, currentBerryBalance, currentEcoBalance, ownedFurnitureIds);
    }

    @Override
    public int getItemCount() {
        return furnitureList.size();
    }

    static class FurnitureViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvCategory, tvCurrencyHint;
        ImageView ivIcon;
        Button btnBuy;

        public FurnitureViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFurnitureName);
            tvPrice = itemView.findViewById(R.id.tvFurniturePrice);
            tvCategory = itemView.findViewById(R.id.tvFurnitureCategory);
            tvCurrencyHint = itemView.findViewById(R.id.tvFurnitureCurrencyHint);
            ivIcon = itemView.findViewById(R.id.ivFurnitureIcon);
            btnBuy = itemView.findViewById(R.id.btnBuyFurniture);
        }

        public void bind(
                Furniture furniture,
                OnFurnitureBuyClickListener listener,
                int berryBalance,
                int ecoBalance,
                List<Integer> ownedIds
        ) {
            tvName.setText(furniture.getName());
            tvCategory.setText(furniture.getCategory());

            boolean usesEcoCoins = furniture.isEcoCurrency();
            int availableBalance = usesEcoCoins ? ecoBalance : berryBalance;
            String priceText = itemView.getContext().getString(
                    usesEcoCoins ? R.string.shop_item_price_eco : R.string.shop_item_price_berries,
                    furniture.getPrice()
            );
            tvPrice.setText(priceText);
            tvPrice.setTextColor(ContextCompat.getColor(
                    itemView.getContext(),
                    usesEcoCoins ? R.color.game_eco : R.color.game_berries
            ));
            tvCurrencyHint.setText(usesEcoCoins
                    ? R.string.shop_item_currency_eco
                    : R.string.shop_item_currency_berries);

            if (ownedIds != null && ownedIds.contains(furniture.getId())) {
                btnBuy.setEnabled(false);
                btnBuy.setText(R.string.shop_item_owned);
            } else if (availableBalance >= furniture.getPrice()) {
                btnBuy.setEnabled(true);
                btnBuy.setText(R.string.common_action_buy);
            } else {
                btnBuy.setEnabled(false);
                btnBuy.setText(usesEcoCoins ? R.string.shop_item_no_eco : R.string.shop_item_no_berries);
            }

            int drawableResId = FurnitureDrawableResolver.resolveDrawableResId(
                    itemView.getContext(),
                    furniture.getImageRef()
            );
            ivIcon.setImageResource(drawableResId);

            btnBuy.setOnClickListener(v -> listener.onBuyClick(furniture));
        }
    }
}
