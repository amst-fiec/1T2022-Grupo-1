package com.example.transporte_alimentos_amst_g1;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.HashMap;

public class IngresoSesion extends AppCompatActivity {

    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_sesion);

        //Inicializacion de las variables para el ingreso por Google
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


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

            //Cerramos el actual
            finish();

            //Se inicializa el siguiente
            Intent intent = new Intent(this, MenuPrincipal.class);
            intent.putExtra("info_user", info_user);
            startActivity(intent);
        }else {
            System.out.println("sin registrarse");
        }
    }

    //metodo de inicio de sesion
    public void iniciarSesion(View view) {
        resultLauncher.launch(new Intent(mGoogleSignInClient.getSignInIntent()));
    }

    //metodo para cerrar sesion
    private void cerrarSesion() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                task -> ingresoUsuario(null));
    }
}