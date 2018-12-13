package com.example.daniel.proyectobiblioteca;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class PreferenciasCompartidas {
    private Context context;
    SharedPreferences pref;
    SharedPreferences.Editor editor;



    public PreferenciasCompartidas(Context context) {
        this.context = context;
    }

    public void guardarPreferencias(String usuario, String pass) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
        editor.putString("credenciales", usuario +"-"+ pass);

        editor.apply();
    }

    public String getSesion() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String v = pref.getString("credenciales", "null");
        return v;
    }

    public void eliminarPreferencias(){
if (getSesion()!=null) {
    pref.edit().clear().apply();
}
        System.out.println("PREFERENCIAS borradas");
    }

    @JavascriptInterface
    public void sendData(String usuario, String pass) { //enviamos el user y la pass
        guardarPreferencias(usuario, pass);
        System.out.println("Guardamos en preferencias");
    }

}


