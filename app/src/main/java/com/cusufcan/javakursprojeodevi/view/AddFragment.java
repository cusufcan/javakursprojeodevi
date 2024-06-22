package com.cusufcan.javakursprojeodevi.view;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.cusufcan.javakursprojeodevi.databinding.FragmentAddBinding;
import com.cusufcan.javakursprojeodevi.db.ArtDao;
import com.cusufcan.javakursprojeodevi.db.ArtDatabase;
import com.cusufcan.javakursprojeodevi.helper.AppHelper;
import com.cusufcan.javakursprojeodevi.model.Art;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddFragment extends Fragment {
    private final CompositeDisposable disposable = new CompositeDisposable();
    
    private FragmentAddBinding binding;
    
    private ArtDao dao;
    
    private ActivityResultLauncher<Intent> activityLauncher;
    private ActivityResultLauncher<String> permissionLauncher;
    
    private Bitmap selectedImage;
    
    private Art artFromMain;
    
    public AddFragment() {
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLauncher();
        
        ArtDatabase artDatabase = Room.databaseBuilder(requireContext(), ArtDatabase.class, "Arts").build();
        dao = artDatabase.artDao();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        requireActivity().openOrCreateDatabase("Arts", MODE_PRIVATE, null);
        
        String info;
        if (getArguments() != null) {
            info = AddFragmentArgs.fromBundle(getArguments()).getInfo();
        } else {
            info = "new";
        }
        
        binding.saveButton.setOnClickListener(AddFragment.this::saveArt);
        binding.artImageView.setOnClickListener(AddFragment.this::pickImage);
        binding.deleteButton.setOnClickListener(AddFragment.this::deleteArt);
        
        if (info.equals("new")) {
            binding.artNameEditText.setText("");
            binding.artistNameEditText.setText("");
            binding.artYearEditText.setText("");
            binding.saveButton.setVisibility(View.VISIBLE);
            binding.deleteButton.setVisibility(View.GONE);
        } else {
            int id = AddFragmentArgs.fromBundle(getArguments()).getArtId();
            binding.saveButton.setVisibility(View.GONE);
            binding.deleteButton.setVisibility(View.VISIBLE);
            
            disposable.add(dao.getArtsById(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(AddFragment.this::handleResponseWithOldArt, e -> System.out.println(e.getLocalizedMessage())));
        }
    }
    
    private void handleResponseWithOldArt(Art art) {
        artFromMain = art;
        binding.artNameEditText.setText(art.name);
        binding.artistNameEditText.setText(art.artistName);
        binding.artYearEditText.setText(art.year);
        
        if (art.image == null) return;
        Bitmap bitmap = BitmapFactory.decodeByteArray(art.image, 0, art.image.length);
        binding.artImageView.setImageBitmap(bitmap);
    }
    
    public void registerLauncher() {
        activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            if (o.getResultCode() == Activity.RESULT_OK) {
                Intent resultIntent = o.getData();
                if (resultIntent == null) return;
                Uri imageData = resultIntent.getData();
                if (imageData == null) return;
                try {
                    if (Build.VERSION.SDK_INT >= 28) {
                        ImageDecoder.Source source = ImageDecoder.createSource(requireActivity().getContentResolver(), imageData);
                        selectedImage = ImageDecoder.decodeBitmap(source);
                    } else {
                        selectedImage = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageData);
                    }
                    binding.artImageView.setImageBitmap(selectedImage);
                } catch (IOException e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        });
        
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), o -> {
            if (o) {
                AppHelper.toGallery(activityLauncher);
            } else {
                Toast.makeText(requireActivity(), "Permission needed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    public void saveArt(View view) {
        String artName = binding.artNameEditText.getText().toString();
        String artistName = binding.artistNameEditText.getText().toString();
        String artYear = binding.artYearEditText.getText().toString();
        
        Bitmap artImage = AppHelper.imageSmaller(selectedImage, 300, true);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        artImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] bytes = outputStream.toByteArray();
        
        Art art = new Art(artName, artistName, artYear, bytes);
        
        disposable.add(dao.insert(art).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(AddFragment.this::handleResponse, e -> System.out.println(e.getLocalizedMessage())));
    }
    
    public void deleteArt(View view) {
        disposable.add(dao.delete(artFromMain).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(AddFragment.this::handleResponse, e -> System.out.println(e.getLocalizedMessage())));
    }
    
    private void handleResponse() {
        NavDirections action = AddFragmentDirections.actionAddFragmentToListFragment();
        Navigation.findNavController(requireView()).navigate(action);
    }
    
    public void pickImage(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(view, Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            requestPermission(view, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }
    
    public void requestPermission(View view, String permission) {
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), permission)) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", v -> permissionLauncher.launch(permission)).show();
            } else {
                permissionLauncher.launch(permission);
            }
        } else {
            AppHelper.toGallery(activityLauncher);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        binding = null;
        disposable.clear();
    }
}