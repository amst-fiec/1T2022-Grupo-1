package com.example.transporte_alimentos_amst_g1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.transporte_alimentos_amst_g1.Clases.ClaseUsando;
import com.example.transporte_alimentos_amst_g1.Clases.usuario;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class IngresoSesion extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    GoogleSignInClient mGoogleSignInClient;
    EditText usuario,contra;
    String datoUser, datoPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_sesion);

        usuario=findViewById(R.id.logUser);
        contra = findViewById(R.id.logPass);


        //Inicializacion de las variables para el ingreso por Google
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //Se inicializa la base de datos
        inicializarFirebase();

        //En caso de que se cierre sesion en la aplicacion
        Intent intent = getIntent();
        String msg = intent.getStringExtra("msg");
        if(msg != null) {
            if (msg.equals("cerrarSesion")) {
                cerrarSesion();
            }
        }
    }

    ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            if (account != null) firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            Log.w("TAG", "Fallo el inicio de sesión con google.", e);
                        }
                    }

                }
            });

    //Metodo de autenticación para google.
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this,task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                ingresoUsuario(user);
            }else{
                System.out.println("error");
                ingresoUsuario(null);
            }
        });
    }

    //metodo para definir los parametros que se pasaran al siguiente intent e inicializarlo.
    private void ingresoUsuario(FirebaseUser user) {
        if (user != null) {
            HashMap<String, String> info_user = new HashMap<String, String>();
            //Se coloca la informacion mas importante dentro dle hashmap para pasarlos al siguiente intent
            info_user.put("user_name", user.getDisplayName());
            info_user.put("user_email", user.getEmail());
            info_user.put("user_photo", String.valueOf(user.getPhotoUrl()));

            System.out.println("Llego");

            //Se inicializa el siguiente
            Intent intent = new Intent(this, MenuPrincipal.class);
            intent.putExtra("info_user", info_user);
            startActivity(intent);

        }else {
            System.out.println("sin registrarse");
        }
    }

    //metodo de inicio de sesion con google
    public void iniciarSesionGoogle(View view) {
        resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }

    //metodo para cerrar sesion
    private void cerrarSesion() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> ingresoUsuario(null));
    }

    //metodo de inicio de sesion normalmente
    public void iniciarSesion(View view) {
        boolean resultadoValid = validarIngresos();
        if (resultadoValid) {
           databaseReference.child("UsersRegis").child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if (snapshot.hasChild(datoUser) ) {
                       System.out.println("Correcto");
                       usuario user =  snapshot.child(datoUser).getValue(usuario.class);
                       String userContra = user.getClave();
                       String userClass = user.getClase();
                       if (userContra.equals(datoPass)) {
                           usuario.setText("");
                           contra.setText("");
                           ClaseUsando.usuarioUsando = user;
                           if (userClass.equals("Administrador")){
                               Intent i = new Intent(IngresoSesion.this, MenuPrincipalAdmin.class);
                               startActivity(i);
                           }else if(userClass.equals("Conductor")){
                               Intent i = new Intent(IngresoSesion.this, MenuPrincipalConductor.class);
                               startActivity(i);
                           }

                       }else{
                           Toast.makeText(IngresoSesion.this, "Contraseña incorrecta",
                                   Toast.LENGTH_SHORT).show();
                       }

                   }else{
                       Toast.makeText(IngresoSesion.this, "No existe dicho Usuario",
                               Toast.LENGTH_SHORT).show();
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
        }

    }

    //metodo de inicio de sesion con google
    public void registro(View view) {
        Intent intent = new Intent(this, Registro.class);
        startActivity(intent);
    }

    private void inicializarFirebase(){
        FirebaseApp.initializeApp(this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }

    private boolean validarIngresos(){
        datoUser= usuario.getText().toString().trim().toLowerCase();
        datoPass= contra.getText().toString().trim();
        if(!datoUser.equals("") && !datoPass.equals("")) {
            return true;
        }else{
            Toast.makeText(IngresoSesion.this, "IngreseCorrectamente los datos", Toast.LENGTH_SHORT).show();
            if (datoUser.equals("")) {
                usuario.setError("Se requiere el Usuario");
            }
            if (datoPass.equals("")) {
                contra.setError("Se requiere la Contraseña");
            }
            return false;
        }
    }

}