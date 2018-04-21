package com.edwinbustamante.gruposcochalos.ImagenFull;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.edwinbustamante.gruposcochalos.Objetos.FirebaseReferences;
import com.edwinbustamante.gruposcochalos.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;

import uk.co.senab.photoview.PhotoViewAttacher;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class FulImagen extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener mAuthListener;//se encarga de poder guardar la sesion iniciada sino se cierra la sesion
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabase;
    private int CAMERA_REQUEST_CODE = 0;
    PhotoViewAttacher mAttacher;//Para hacer Zoom en el imagen
    private static String APP_DIRECTORY = "GruposCochalos/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "GruposCochalosImages";
    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 100;
    private final int SELECT_PICTURE = 200;
    private ImageView iconoPerfil;
    private Button mOptionButton;
    private RelativeLayout mRlView;
    private String mPath;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);//una vez que se inicia la actividad verificara que si el usuario esta logueado
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ful_imagen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();//INSTANCIAMOS
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(FirebaseReferences.USERS_REFERENCE);//hacemos referencia a la base de datos usuario tabla que tenemops como referencia en otra clase

        iconoPerfil = (ImageView) findViewById(R.id.imagenfull);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        iconoPerfil.setMaxHeight(height);
        iconoPerfil.setMaxWidth(width);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {//verificamos que el usuario este logueado

                    mDatabase.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {//obteniendo el identificador de usuario accedemos a su informacionn del usuario
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                         /*
                            ANIADIENDO LOS ATRIBUTOS DESDE LA BASE DE DATOS AL XML DE LA CUENTA DE UN USUARIO
                            * */

                            String imageUrl = dataSnapshot.child("perfil").getValue().toString();
                            if (!imageUrl.equals("default") || TextUtils.isEmpty(imageUrl)) {


                                Glide.with(FulImagen.this)
                                        .load(imageUrl)
                                        .fitCenter()
                                        // .skipMemoryCache(true)//Almacenando en cache
                                        .centerCrop()
                                        .into(iconoPerfil);
                                // otro tipo Picasso.with(CuentaUsuario.this).load(Uri.parse(dataSnapshot.child("perfil").getValue().toString())).into(cuenta_perfil);
                            } else {
                                Uri uriImage = Uri.parse("android.resource://" + getPackageName() + "/" + R.drawable.perfilmusic);
                                iconoPerfil.setImageURI(uriImage);
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };

        //hace que la imagen sea expansible
        mAttacher = new PhotoViewAttacher(iconoPerfil);

        // mOptionButton = (Button) findViewById(R.id.show_options_button);
        //mRlView = (RelativeLayout) findViewById(R.id.rl_view);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_full_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_perfil:
                if (mayRequestStoragePermission()) {
                    showOptions();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean mayRequestStoragePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED))
            return true;

        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))) {
            Snackbar.make(mRlView, "Los permisos son necesarios para poder usar la aplicación",
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            });
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    private void showOptions() {
        final CharSequence[] option = {"Tomar foto", "Elegir de galeria", "Cancelar"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(FulImagen.this);
        builder.setTitle("Elige una opción");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (option[which] == "Tomar foto") {
                    openCamera();
                } else if (option[which] == "Elegir de galeria") {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Selecciona app de imagen"), SELECT_PICTURE);
                } else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();

        if (!isDirectoryCreated)
            isDirectoryCreated = file.mkdirs();

        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis() / 1000;
            String imageName = timestamp.toString() + ".jpg";

            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY
                    + File.separator + imageName;

            File newFile = new File(mPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this,
                            new String[]{mPath}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> Uri = " + uri);
                                }
                            });


                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    iconoPerfil.setImageBitmap(rotateImage(bitmap, 90));
                    break;
                case SELECT_PICTURE:

                    final ProgressDialog subiendoProgres = new ProgressDialog(FulImagen.this);
                    subiendoProgres.setMessage("Subiendo foto....");
                    subiendoProgres.show();
                    final Uri uri = data.getData();
                    if (uri == null) {
                        subiendoProgres.dismiss();
                        return;
                    }
                    if (mStorageRef == null) {
                        mStorageRef = FirebaseStorage.getInstance().getReference();
                    }
                    if (mDatabase == null) {
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                    }
                    final StorageReference filepath = mStorageRef.child("fotosperfil").child(getRandomString());
                    final DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                    currentUserDB.child("perfil").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String perfilAntiguo = dataSnapshot.getValue().toString();
                            if (!perfilAntiguo.equals("default") && !perfilAntiguo.isEmpty()) {
                                Task<Void> task = FirebaseStorage.getInstance().getReferenceFromUrl(perfilAntiguo).delete();
                                task.addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(FulImagen.this, "Eliminado Exitosamente", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(FulImagen.this, "Fallo al eliminar la imagen", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            currentUserDB.child("perfil").removeEventListener(this);
                            filepath.putFile(uri).addOnSuccessListener(FulImagen.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Uri downloadUri = taskSnapshot.getDownloadUrl();

                                    DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                                    currentUserDB.child("perfil").setValue(downloadUri.toString());
                                    Toast.makeText(FulImagen.this, "todo listo esta", Toast.LENGTH_SHORT).show();
                                    subiendoProgres.dismiss();
                                }
                            }).addOnFailureListener(FulImagen.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    subiendoProgres.dismiss();
                                    Toast.makeText(FulImagen.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // iconoPerfil.setImageURI(path);
                    break;

            }
        }
    }

    private String getRandomString() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS) {
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FulImagen.this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                mOptionButton.setEnabled(true);
            }
        } else {
            showExplanation();
        }
    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FulImagen.this);
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "esty destruido", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "estoy stop", Toast.LENGTH_SHORT).show();
    }

}



