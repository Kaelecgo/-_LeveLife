package com.irenaprokhyra.levelife.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Furniture;
import com.irenaprokhyra.levelife.util.FurnitureDrawableResolver;
import java.util.ArrayList;
import java.util.List;

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.FurnitureViewHolder> {

    private List<Furniture> furnitureList = new ArrayList<>();
    private final OnFurnitureBuyClickListener listener;

    private int currentBalance = 0;

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

    public void setCurrentBalance(int balance) {
        this.currentBalance = balance;
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
        holder.bind(furniture, listener, currentBalance, ownedFurnitureIds);
    }

    @Override
    public int getItemCount() {
        return furnitureList.size();
    }

    static class FurnitureViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView ivIcon;
        Button btnBuy;

        public FurnitureViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFurnitureName);
            tvPrice = itemView.findViewById(R.id.tvFurniturePrice);
            ivIcon = itemView.findViewById(R.id.ivFurnitureIcon);
            btnBuy = itemView.findViewById(R.id.btnBuyFurniture);
        }

        public void bind(Furniture furniture, OnFurnitureBuyClickListener listener, int balance, List<Integer> ownedIds) {
            tvName.setText(furniture.getName());
            String priceText = itemView.getContext().getString(R.string.shop_item_price, furniture.getPrice());
            tvPrice.setText(priceText);

            if (ownedIds != null && ownedIds.contains(furniture.getId())) {
                btnBuy.setEnabled(false);
                btnBuy.setText(R.string.shop_item_owned);
            } else if (balance >= furniture.getPrice()) {
                btnBuy.setEnabled(true);
                btnBuy.setText(R.string.common_action_buy);
            } else {
                btnBuy.setEnabled(false);
                btnBuy.setText(R.string.shop_item_no_money);
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