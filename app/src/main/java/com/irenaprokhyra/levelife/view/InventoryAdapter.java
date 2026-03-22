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
import java.util.ArrayList;
import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> {

    private List<Furniture> inventoryList = new ArrayList<>();
    private final OnFurniturePlaceClickListener listener;

    public interface OnFurniturePlaceClickListener {
        void onPlaceClick(Furniture furniture);
    }

    public InventoryAdapter(OnFurniturePlaceClickListener listener) {
        this.listener = listener;
    }

    public void setInventoryList (List<Furniture> list) {
        this.inventoryList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reutilizamos el diseño visual del item de la tienda, pero cambiaremos su comportamiento
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_furniture, parent, false);
        return new InventoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryViewHolder holder, int position) {
        Furniture furniture = inventoryList.get(position);
        holder.bind(furniture, listener);
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }


    static class InventoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView ivIcon;
        Button btnAction;

        public InventoryViewHolder (@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFurnitureName);
            tvPrice = itemView.findViewById(R.id.tvFurniturePrice);
            ivIcon = itemView.findViewById(R.id.ivFurnitureIcon);
            btnAction = itemView.findViewById(R.id.btnBuyFurniture);
        }

        public void bind (Furniture furniture, OnFurniturePlaceClickListener listener) {
            tvName.setText(furniture.getName());
            tvPrice.setVisibility(View.GONE);

            // Cambiamos el texto del boton de "Comprar" a "Colocar"
            btnAction.setText(itemView.getContext().getString(R.string.action_place_furniture));
            btnAction.setEnabled(true);

            btnAction.setOnClickListener(v -> listener.onPlaceClick(furniture));
        }
    }
}