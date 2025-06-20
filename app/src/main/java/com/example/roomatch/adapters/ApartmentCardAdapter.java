package com.example.roomatch.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomatch.R;

import java.util.List;
import java.util.Map;

public class ApartmentCardAdapter extends RecyclerView.Adapter<ApartmentCardAdapter.ApartmentViewHolder> {

    public interface OnApartmentClickListener {
        void onViewApartmentClick(Map<String, Object> apartment);
        void onEditApartmentClick(Map<String, Object> apartment);
    }

    private final List<Map<String, Object>> apartments;
    private final OnApartmentClickListener listener;

    public ApartmentCardAdapter(List<Map<String, Object>> apartments, OnApartmentClickListener listener) {
        this.apartments = apartments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApartmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartment_card, parent, false);
        return new ApartmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApartmentViewHolder holder, int position) {
        Map<String, Object> apartment = apartments.get(position);

        // שליפת שדות בודדים במקום כתובת אחת
        String city = apartment.get("city") != null ? apartment.get("city").toString() : "לא זמין";
        String street = apartment.get("street") != null ? apartment.get("street").toString() : "לא זמין";
        String houseNumber = apartment.get("houseNumber") != null ? apartment.get("houseNumber").toString() : "לא זמין";
        String price = apartment.get("price") != null ? apartment.get("price").toString() + " ₪" : "לא זמין";

        holder.city.setText("עיר: " + city);
        holder.street.setText("רחוב: " + street);
        holder.houseNumber.setText("מספר בית: " + houseNumber);
        holder.price.setText("מחיר: " + price);

        holder.buttonViewApartment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewApartmentClick(apartment);
            }
        });

        holder.buttonEditApartment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditApartmentClick(apartment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apartments.size();
    }

    static class ApartmentViewHolder extends RecyclerView.ViewHolder {
        TextView city, street, houseNumber, price;
        Button buttonViewApartment, buttonEditApartment;

        public ApartmentViewHolder(@NonNull View itemView) {
            super(itemView);
            city = itemView.findViewById(R.id.textViewApartmentCity);
            street = itemView.findViewById(R.id.textViewApartmentStreet);
            houseNumber = itemView.findViewById(R.id.textViewApartmentHouseNumber);
            price = itemView.findViewById(R.id.textViewApartmentPrice);
            buttonViewApartment = itemView.findViewById(R.id.buttonViewApartment);
            buttonEditApartment = itemView.findViewById(R.id.buttonEditApartment);
        }
    }
}
