package com.cusufcan.javakursprojeodevi.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.cusufcan.javakursprojeodevi.adapter.RecyclerAdapter;
import com.cusufcan.javakursprojeodevi.databinding.FragmentListBinding;
import com.cusufcan.javakursprojeodevi.db.ArtDao;
import com.cusufcan.javakursprojeodevi.db.ArtDatabase;
import com.cusufcan.javakursprojeodevi.model.Art;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListFragment extends Fragment {
    private final CompositeDisposable disposable = new CompositeDisposable();
    
    private FragmentListBinding binding;
    
    private ArtDao dao;
    
    public ListFragment() {
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ArtDatabase db = Room.databaseBuilder(requireContext(), ArtDatabase.class, "Arts").build();
        dao = db.artDao();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);
        getData();
    }
    
    public void getData() {
        disposable.add(dao.getArtsOnlyIdAndName().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(ListFragment.this::handleResponse, e -> System.out.println(e.getLocalizedMessage())));
    }
    
    private void handleResponse(List<Art> arts) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        RecyclerAdapter adapter = new RecyclerAdapter(arts);
        binding.recyclerView.setAdapter(adapter);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        binding = null;
        disposable.clear();
    }
}