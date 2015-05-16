package com.omnius.coby.mapasv2extas2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Principal extends FragmentActivity {

    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        //verificacion de googleplay services
        int codigoResultado = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.d("GooglePlayServicesUtil", "Checando Google Play Services");
        if(codigoResultado != ConnectionResult.SUCCESS)
        {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(codigoResultado, this, 6);
            if(dialog != null)
            {
                dialog.show();
            }
            else
            {
                muestraDialogoConBotonOK(this, "Error. Por favor asegurate de que tienes Google Play Instalado y tienes conexion a internet.");
            }
        }
        Log.d("GooglePlayServicesUtil", "Verificacion de Google Play Services (SUCCESS = 0) : " + codigoResultado);
        Toast.makeText(getApplicationContext(), "Verificacion de Google Play Services (SUCCESS = 0) : " + codigoResultado, Toast.LENGTH_LONG).show();


        //Obtencion de la referencia del mapa con versiones de android que son
        //compatibles con el componente fragment
        FragmentManager myFragmentManager = getSupportFragmentManager();
        SupportMapFragment mySupportMapFragment = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.map);
        googleMap = mySupportMapFragment.getMap();

        if (googleMap != null) {
            //tipo de mapa, los tipos son:
			/*
			1) MAP_TYPE_NONE – No base map tiles.
			2) MAP_TYPE_NORMAL – Basic maps
			3) MAP_TYPE_SATELLITE – Satellite maps with no labels.
			4) MAP_TYPE_HYBRID – Satellite maps with a transparent layer of major streets.
			5) MAP_TYPE_TERRAIN – Terrain maps.
			*/

            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            //Mostrar el boton de mi ubicacion
            googleMap.setMyLocationEnabled(true);
        }


        //Obtenemos una referencia al LocationManager, la clase que provee acesso a los
        //servicios de localizacion del sistema.
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Obtenemos la ultima posicion conocida por el GPS_PROVIDER
        Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Mostramos, inicialmente, la ultima posicion conocida
        if (loc!=null){
            mostrarLocalizacion(loc.getLatitude(), loc.getLongitude());
        }

        //Creamos un nuevo objeto de tipo listener un LocationListener, que respondera
        //a los eventos relacionados con el GPS, incluyendo el cambio de ubicacion
        LocationListener locListener = new LocationListener() {
            //Metodo callback que se llama automaticamente cuando el GPS registró un cambio de ubicación
            public void onLocationChanged(Location location) {
                // se crea un objeto de la clase CameraUpdate que se encarga de mover
                //la camara o el punto de vista del mapa de acuerdo con los parámetros que especifiquemos,
                //en este caso: recibe un objeto de tipo LatLng a partir de la latitud y longitud del
                // parametro location, el objeto LatLng es creado por el metodo estatico newLatLng de la
                // clase CameraUpdateFactory.
                CameraUpdate center= CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                // se le indica al mapa que se mueva la camara con los datos del objeto CameraUpdate.
                googleMap.moveCamera(center);
                // se llama a un metodo que muestra un marcador en la nueva ubicación
                mostrarLocalizacion(location.getLatitude(),location.getLongitude());
            }
            public void onProviderDisabled(String provider){
            }
            public void onProviderEnabled(String provider){
            }
            public void onStatusChanged(String provider, int status, Bundle extras){
            }
        };

//		  // Una ves que se creo el LocationListener, activaremos el proveedor de localización y
//		  //suscribiremos la aplicacion a sus eventos, lo cual se realiza mediante una
//		  //llamada al método requestLocationUpdates(), al que deberemos pasar 4 parámetros distintos:
//		 //  -> Nombre del proveedor de localización al que nos queremos suscribir. (En este caso el GPS)
//		 //  -> Tiempo mínimo entre actualizaciones, en milisegundos. (2000 milisegundos)
//		 //  -> Distancia mínima entre actualizaciones, en metros. (0)
//		 //  -> Instancia de un objeto LocationListener, el que definimos anteriormente  (locListener).
//		    locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locListener);


		 /*
		  *
		  * Criteria
		  *
		  *
		  */

        Criteria criteriosUbicacion = new Criteria();
        //Criterio de precisión fina
        criteriosUbicacion.setAccuracy(Criteria.ACCURACY_FINE);
        //Criterio de altitud requerida
        criteriosUbicacion.setAltitudeRequired(true);
        //Criterio de No permitir costos monetarios
        criteriosUbicacion.setCostAllowed(false);
        //Criterio de velocidad requerida.
        criteriosUbicacion.setSpeedRequired(true);
        //se obtiene el proveedor  que mejor cumpla con los criterios dados.
        String provider = locManager.getBestProvider(criteriosUbicacion, true);
        if (provider != null){
            //se activan las notificaciones de ubicacion con el proveedor obtenido,
            //en intervalos de 1000 milisegundos, con una distancia minima de 70 metros entre
            //cada actualizacion, y el objeto listener definido anteriormente
            locManager.requestLocationUpdates(provider, 1000, 70, locListener);
        }

    }

    //Metodo que agrega un marcador en el mapa en la latitud y longitus recibidas...

    private void mostrarLocalizacion(double lat, double lng){
        // se mandan mensajes de depuracion con las latitudes y longitudes obtenidas
        //por el objeto LocationListener...
        Log.i("Loc", "Nueva Ubicacion :" + lat +" - "+lng);
        // se Agrega un marcador a mapa en las latitudes y longitudes obtenidas
        //por el objeto LocationListener...
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title("Mi ubicacion..."));
    }

    public static void muestraDialogoConBotonOK(Context context, String messageText)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(messageText);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
