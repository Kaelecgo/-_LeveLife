package com.irenaprokhyra.levelife.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private final OnFurniturePlaceClickListener listener;

    public interface OnFurniturePlaceClickListener {
        void onPlaceClick(Furniture furniture);
    }

    public InventoryAdapter(OnFurniturePlaceClickListener listener) {
        this.listener = listener;
    }

    public void setInventoryList(List<Furniture> list) {
        this.inventoryList = (list != null) ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setPlacedFurnitureSlots(Map<Integer, String> placedFurnitureSlots) {
        this.placedFurnitureSlots = placedFurnitureSlots != null
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

        public InventoryViewHolder (@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFurnitureName);
            tvMeta = itemView.findViewById(R.id.tvInventoryFurnitureMeta);
            tvPlacementStatus = itemView.findViewById(R.id.tvInventoryPlacementStatus);
            ivIcon = itemView.findViewById(R.id.ivFurnitureIcon);
            btnAction = itemView.findViewById(R.id.btnPlaceFurniture);
        }

        public void bind(
                Furniture furniture,
                Map<Integer, String> placedFurnitureSlots,
                OnFurniturePlaceClickListener listener
        ) {
            tvName.setText(furniture.getName());
            int drawableResId = FurnitureDrawableResolver.resolveDrawableResId(
                    itemView.getContext(),
                    furniture.getImageRef()
            );
            ivIcon.setImageResource(drawableResId);

            String meta = furniture.getCategory();
            if (meta == null || meta.trim().isEmpty()) {
                tvMeta.setVisibility(View.VISIBLE);
                tvMeta.setText(R.string.inventory_item_meta_fallback);
            } else {
                tvMeta.setVisibility(View.VISIBLE);
                tvMeta.setText(meta);
            }

            String placedSlotLabel = placedFurnitureSlots.get(furniture.getId());
            boolean isPlaced = placedSlotLabel != null && !placedSlotLabel.trim().isEmpty();

            if (isPlaced) {
                tvPlacementStatus.setVisibility(View.VISIBLE);
                tvPlacementStatus.setText(
                        itemView.getContext().getString(
                                R.string.inventory_item_placed_in_slot,
                                placedSlotLabel
                        )
                );
                btnAction.setText(itemView.getContext().getString(R.string.inventory_action_move));
            } else {
                tvPlacementStatus.setVisibility(View.GONE);
                btnAction.setText(itemView.getContext().getString(R.string.inventory_action_place));
            }

            btnAction.setEnabled(true);

            btnAction.setOnClickListener(v -> listener.onPlaceClick(furniture));
        }
    }
}
