package com.cusufcan.javakursprojeodevi.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.cusufcan.javakursprojeodevi.databinding.ArtItemBinding;
import com.cusufcan.javakursprojeodevi.model.Art;
import com.cusufcan.javakursprojeodevi.view.ListFragmentDirections;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {
    
    List<Art> arts;
    
    public RecyclerAdapter(List<Art> arts) {
        this.arts = arts;
    }
    
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ArtItemBinding binding = ArtItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RecyclerHolder(binding);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        holder.binding.artItemText.setText(arts.get(position).name);
        holder.itemView.setOnClickListener(v -> {
            ListFragmentDirections.ActionListFragmentToAddFragment action = ListFragmentDirections.actionListFragmentToAddFragment("old");
            action.setArtId(arts.get(position).id);
            action.setInfo("old");
            Navigation.findNavController(v).navigate(action);
        });
    }
    
    @Override
    public int getItemCount() {
        return arts.size();
    }
    
    public static class RecyclerHolder extends RecyclerView.ViewHolder {
        private final ArtItemBinding binding;
        
        public RecyclerHolder(ArtItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
