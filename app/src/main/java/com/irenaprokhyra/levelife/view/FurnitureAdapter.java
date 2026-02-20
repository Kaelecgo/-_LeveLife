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

    @NonNull
    @Override
    public FurnitureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Asumimos que crearás un layout llamado item_furniture.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_furniture, parent, false);
        return new FurnitureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FurnitureViewHolder holder, int position) {
        Furniture furniture = furnitureList.get(position);
        holder.bind(furniture, listener);
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
            // Asegúrate de usar estos IDs cuando crees tu XML
            tvName = itemView.findViewById(R.id.tvFurnitureName);
            tvPrice = itemView.findViewById(R.id.tvFurniturePrice);
            ivIcon = itemView.findViewById(R.id.ivFurnitureIcon);
            btnBuy = itemView.findViewById(R.id.btnBuyFurniture);
        }

        public void bind(Furniture furniture, OnFurnitureBuyClickListener listener) {
            tvName.setText(furniture.getName());
            String priceText = itemView.getContext().getString(R.string.furniture_price_format, furniture.getPrice());
            tvPrice.setText(priceText);


            // Más adelante cargaremos la imagen real basada en furniture.getImageRef()
            // ivIcon.setImageResource(...);

            btnBuy.setOnClickListener(v -> listener.onBuyClick(furniture));
        }
    }
}