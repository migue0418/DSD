// Donacion.java
package donacion;

import i_donacion.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


public class Donacion extends UnicastRemoteObject implements I_Donacion, I_DonacionReplica {
    
    private Map<String, Usuario> usuariosServidor; // Estructura que contiene a los usuarios de este servidor
    private double subtotalDonaciones;       // Donaciones realizadas en este servidor
    private String nombreServidor;          // Nombre del servidor
    private String nombreServidorReplica;   // Nombre del servidor réplica
    private int numDonacionesServidor;   // Nombre del servidor réplica
    
    public Donacion(String servidor, String replica) throws RemoteException{
        this.usuariosServidor = new HashMap();
        this.subtotalDonaciones = 0;
        this.nombreServidor = servidor;
        this.nombreServidorReplica = replica;
        this.numDonacionesServidor = 0;
    }

    // Devuelve el número de usuarios registrados en el servidor
    @Override
    public boolean comprobarPassword(String nombreUsuario, String password) {
        Usuario user = this.usuariosServidor.get(nombreUsuario);
        
        return user.comprobarPassword(password);
    }

    // Devuelve el número de usuarios registrados en el servidor
    @Override
    public int getNumUsuarios() {
        return this.usuariosServidor.size();
    }

    // Devuelve el número de dinreo donado en el servidor
    @Override
    public double getDineroDonadoServidor() {
        return this.subtotalDonaciones;
    }
    
    // Comprueba si existe un usuario con ese nombre en el Map
    @Override
    public boolean usuarioEstaRegistrado(String nombreUsuario) throws RemoteException{
        return usuariosServidor.containsKey(nombreUsuario);
    }
    
    // Se añade el usuario nuevo al Map
    @Override
    public void addUsuario(String nombreUsuario, String password) throws RemoteException{
        this.usuariosServidor.put(nombreUsuario, new Usuario(nombreUsuario, password));
    }
    
    // Se añade al subtotal el dinero de la nueva donación
    @Override
    public void addDineroDonacion(double dineroDonado) throws RemoteException {
        if(dineroDonado > 0){
            this.subtotalDonaciones += dineroDonado;
            this.numDonacionesServidor++;
        }
    }
    
    // Se registra a un usuario nuevo
    @Override
    public boolean registrar(String nombreUsuario, String password) throws RemoteException {
        boolean noExiste = true;
        String servidor = getServidorHost(nombreUsuario);
        I_DonacionReplica servidorReplica = this.getServidorReplica("localhost", this.nombreServidorReplica);
        int numUsuarios, numUsuariosReplica;
        
        if(!servidor.equals("")){
            noExiste = false;   // Usuario ya está registrado en algún servidor
            System.err.println("Ya existe un usuario registrado con el nombre " + nombreUsuario + ".");
        }
        else {
            numUsuarios = this.getNumUsuarios();
            numUsuariosReplica = servidorReplica.getNumUsuarios();
            
            if(numUsuarios <= numUsuariosReplica){
                this.addUsuario(nombreUsuario, password);
            }
            else {
                servidorReplica.addUsuario(nombreUsuario, password);
            }
        }
     
        return noExiste;
    }
    
    // Un usuario registrado, realiza una donación
    @Override
    public void hacerDonacion(String nombreUsuario, double dineroDonado) throws RemoteException{
        if(this.usuarioEstaRegistrado(nombreUsuario)){
            Usuario user = usuariosServidor.get(nombreUsuario);
            user.addDonacion(dineroDonado);
            this.addDineroDonacion(dineroDonado);

            System.out.println("El usuario " + nombreUsuario + " ha donado: " + dineroDonado + "€");
        }
        else {
            System.err.println("El usuario " + nombreUsuario + " no está registrado en el servidor");
        }
    }
    
    @Override
    public boolean iniciarSesion(String nombreUsuario, String password) throws RemoteException{
        boolean sesionIniciada = true;
        String servidorHost = this.getServidorHost(nombreUsuario);
        
        if(servidorHost.equals("")){
            sesionIniciada = false;
            System.out.println("El usuario " + nombreUsuario + " no está registrado en ningún servidor");
        }
        else {
            if(servidorHost.equals(this.nombreServidor)){
                System.out.println("El usuario " + nombreUsuario + " esta alojado en el servidor " + this.nombreServidor);
                sesionIniciada = this.comprobarPassword(nombreUsuario, password);
            }
            else if(servidorHost.equals(this.nombreServidorReplica)) {
                System.out.println("El usuario " + nombreUsuario + " esta alojado en el servidor " + this.nombreServidorReplica);
                I_DonacionReplica servidorReplica = this.getServidorReplica("localhost", servidorHost);
                sesionIniciada = servidorReplica.comprobarPassword(nombreUsuario, password);
            }
            else{
                sesionIniciada = false;
                System.out.println("No se ha podido encontrar el servidor en el que esta " + nombreUsuario);
            }
        }
        
        return sesionIniciada;
    }
    
    // Devuelve el dinero donado por un usuario específico
    @Override
    public double getSubtotalUsuario(String nombreUsuario) throws RemoteException{
        double subtotalDonado = 0;
        
        if(usuariosServidor.containsKey(nombreUsuario)){
            subtotalDonado = usuariosServidor.get(nombreUsuario).getDineroDonado();
        }
        
        return subtotalDonado;
    }
    
    // Devuelve el número total de donaciones realizadas por el usuario
    @Override
    public int getNumDonacionesUsuario(String nombreUsuario) throws RemoteException{
        int numDonaciones = 0;
        
        if(usuariosServidor.containsKey(nombreUsuario)){
            numDonaciones = usuariosServidor.get(nombreUsuario).getNumDonaciones();        
        }
        
        return numDonaciones;
    }
    
    // Devuelve el dinero total recaudado juntando los servidores
    @Override
    public double getTotalDinero(String nombreUsuario) throws RemoteException{
        double dineroTotal = 0;
        I_DonacionReplica servidorReplica = this.getServidorReplica("localhost", this.nombreServidorReplica);

        dineroTotal += servidorReplica.getDineroDonadoServidor();
        dineroTotal += this.getDineroDonadoServidor();
        
        return dineroTotal;
    }
    
    // Devuelve el servidor réplica
    @Override
    public I_DonacionReplica getServidorReplica(String red, String nombreServidor) throws RemoteException {
        I_DonacionReplica servidorReplica = null;
        
        try {
            Registry reg = LocateRegistry.getRegistry(red, 1099);
            servidorReplica = (I_DonacionReplica)reg.lookup(nombreServidor);
        }
        catch (NotBoundException | RemoteException e) {
            System.err.println("Exception: " + e.getMessage());
        }
        
        return servidorReplica;
    }
    
    // Devuelve el nombre del servidor donde está almacenado ese usuario
    @Override
    public String getServidorHost(String nombreUsuario) throws RemoteException {
        String servidorHost = "";
        
        if(this.usuarioEstaRegistrado(nombreUsuario)){
            servidorHost = this.nombreServidor;
        }
        else {
            I_DonacionReplica servidorReplica = this.getServidorReplica("localhost", this.nombreServidorReplica);
            if(servidorReplica.usuarioEstaRegistrado(nombreUsuario)){
                servidorHost = servidorReplica.getNombreServidor();
            }
        }
        return servidorHost;
    }

    // Devuelve el nombre del servidor
    @Override
    public String getNombreServidor() throws RemoteException {
        return this.nombreServidor;
    }

    // Devuelve un array con los nombres de los usuarios de ese servidor
    @Override
    public ArrayList<String> getUsuariosServidor() throws RemoteException {
        return new ArrayList<String>(this.usuariosServidor.keySet());
    }
}