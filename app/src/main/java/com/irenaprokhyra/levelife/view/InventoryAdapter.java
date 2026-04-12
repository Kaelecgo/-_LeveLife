package com.irenaprokhyra.levelife.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.irenaprokhyra.levelife.util.FurnitureDrawableResolver;
import com.irenaprokhyra.levelife.R;
import com.irenaprokhyra.levelife.model.Furniture;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<Furniture> inventoryList = new ArrayList<>();
    private Map<Integer, String> placedFurnitureSlots = new HashMap<>();
    private final OnFurnitureInteractionListener listener;

    public interface OnFurnitureInteractionListener {
        void onPlaceClick(Furniture furniture);
        void onDeleteClick(Furniture furniture);
    }

    public InventoryAdapter(OnFurnitureInteractionListener listener) {
        this.listener = listener;
    }

    public void setInventoryList(List<Furniture> list) {
        this.inventoryList = (list != null) ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setPlacedFurnitureSlots(Map<Integer, String> placedFurnitureSlots) {
        this.placedFurnitureSlots = (placedFurnitureSlots != null)
                ? new HashMap<>(placedFurnitureSlots)
                : new HashMap<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory_furniture, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Furniture furniture = inventoryList.get(position);
        holder.bind(furniture, placedFurnitureSlots, listener);
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMeta, tvPlacementStatus;
        ImageView ivIcon;
        MaterialButton btnAction;
        ImageButton btnDelete;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFurnitureName);
            tvMeta = itemView.findViewById(R.id.tvInventoryFurnitureMeta);
            tvPlacementStatus = itemView.findViewById(R.id.tvInventoryPlacementStatus);
            ivIcon = itemView.findViewById(R.id.ivFurnitureIcon);
            btnAction = itemView.findViewById(R.id.btnPlaceFurniture);
            btnDelete = itemView.findViewById(R.id.btnDeleteFurniture);
        }

        public void bind(Furniture furniture, Map<Integer, String> placedFurnitureSlots, OnFurnitureInteractionListener listener) {
            tvName.setText(furniture.getName());
            ivIcon.setImageResource(FurnitureDrawableResolver.resolveDrawableResId(itemView.getContext(), furniture.getImageRef()));

            boolean isPlaced = placedFurnitureSlots.containsKey(furniture.getId());

            if (isPlaced) {
                tvPlacementStatus.setVisibility(View.VISIBLE);
                tvPlacementStatus.setText("Ya en la habitación");
                btnAction.setText("Move");
            } else {
                tvPlacementStatus.setVisibility(View.GONE);
                btnAction.setText("Place");
            }

            btnAction.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlaceClick(furniture);
                }
            });

            if (btnDelete != null) {
                btnDelete.setVisibility(isPlaced ? View.VISIBLE : View.GONE);
                btnDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(furniture);
                    }
                });
            }
        }
    }
}