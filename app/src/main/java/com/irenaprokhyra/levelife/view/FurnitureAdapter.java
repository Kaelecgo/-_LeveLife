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

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.FurnitureViewHolder> {

    private List<Furniture> furnitureList = new ArrayList<>();
    private final OnFurnitureBuyClickListener listener;

    // >_ Variable para conocer el saldo actual del usuario _<
    private int currentBalance = 0;

    // >_ Memoria de articulos ya comprados _<
    private List<Integer> ownedFurnitureIds = new ArrayList<>();


    public interface OnFurnitureBuyClickListener {
        void onBuyClick(Furniture furniture);
    }

    public FurnitureAdapter(OnFurnitureBuyClickListener listener) {
        this.listener = listener;
    }

    public void setFurnitureList(List<Furniture> list) {
        this.furnitureList = list;
        notifyDataSetChanged();
    }

    // >_ Metodo para actualizar el saldo desde la Activity _<
    public void setCurrentBalance(int balance) {
        this.currentBalance = balance;
        notifyDataSetChanged(); // Refresca la lista para actualizar los botones
    }

    // >_ Actualiza la lista de IDs que el usuario ya tiene _<
    public void setOwnedFurnitureIds(List<Integer> ownedIds) {
        this.ownedFurnitureIds = ownedIds;
        notifyDataSetChanged();
    }

    // >_ Añade un solo ID al comprar en tiempo real _<
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
        // Pasamos el balance actual a la vista
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
            String priceText = itemView.getContext().getString(R.string.furniture_price_format, furniture.getPrice());
            tvPrice.setText(priceText);

            // >_ MEJORA UX | Validación Visual de Fondos _<
            if (ownedIds.contains(furniture.getId())) {
                // Ya lo tiene comprado
                btnBuy.setEnabled(false);
                btnBuy.setText(R.string.furniture_owned);
            } else if (balance >= furniture.getPrice()) {
                btnBuy.setEnabled(true);
                btnBuy.setText(R.string.btn_buy);
            } else {
                // No tiene dinero - botón deshabilitado
                btnBuy.setEnabled(false);
                btnBuy.setText(R.string.btn_buy);
            }

            // Más adelante cargaremos la imagen real basada en furniture.getImageRef()
            // ivIcon.setImageResource(...);

            btnBuy.setOnClickListener(v -> listener.onBuyClick(furniture));
        }
    }
}