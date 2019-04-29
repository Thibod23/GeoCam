package com.example.a533.geocam;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<Bitmap> mImages = new ArrayList<>();
    private ArrayList<String> mPositions = new ArrayList<>();
    private ArrayList<String> mImageCompleteName = new ArrayList<>();
    private Context mContext;
    FirebaseFirestore db;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mImageNames, ArrayList<Bitmap> mImages, ArrayList<String> mPositions, ArrayList<String> mImageCompleteName) {
        this.mImageNames = mImageNames;
        this.mImages = mImages;
        this.mContext = mContext;
        this.mPositions = mPositions;
        this.mImageCompleteName = mImageCompleteName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.titre.setText(mImageNames.get(position));
        holder.position.setText(mPositions.get(position));
        holder.image.setImageBitmap(mImages.get(position));
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView titre;
        TextView position;
        RelativeLayout parentLayout;
        ImageView imageViewDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            titre = itemView.findViewById(R.id.titre);
            position = itemView.findViewById(R.id.position);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            imageViewDelete = itemView.findViewById(R.id.imageView_Delete);
            imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog ConfirmDeleteDialog = ConfirmDelete();
                    ConfirmDeleteDialog.show();
                }
            });

        }

        private void deletePicture(final String name) {
            db = FirebaseFirestore.getInstance();
            db.collection("Pictures")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getString("name").equals(name)) {
                                        document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mImageCompleteName.remove(getAdapterPosition());
                                                mImageNames.remove(getAdapterPosition());
                                                mPositions.remove(getAdapterPosition());
                                                mImages.remove(getAdapterPosition());
                                                notifyDataSetChanged();
                                                toastMessage("Photo supprimée.");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        toastMessage("Erreur lors de la suppression :(");
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    });
        }

        private AlertDialog ConfirmDelete() {
            AlertDialog ConfirmDeleteDialog = new AlertDialog.Builder(mContext)
                    .setTitle("Supprimer")
                    .setMessage("Voulez-vous vraiment supprimer la photo?")
                    //.setIcon(R.drawable.delete)

                    .setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            deletePicture(mImageCompleteName.get(getAdapterPosition()));
                            dialog.dismiss();
                        }
                    })

                    .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            return ConfirmDeleteDialog;

        }

        private void toastMessage(String message) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

}
