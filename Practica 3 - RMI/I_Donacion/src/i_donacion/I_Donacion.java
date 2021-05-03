// I_Donacion.java
package i_donacion;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface I_Donacion extends Remote {
    boolean registrar(String nombreUsuario, String password) throws RemoteException;
    void hacerDonacion(String nombreUsuario, double cantidad) throws RemoteException;
    double getTotalDinero(String nombreUsuario) throws RemoteException;
    boolean iniciarSesion(String nombreUsuario, String password) throws RemoteException;
    double getSubtotalUsuario(String nombreUsuario) throws RemoteException;
    int getNumDonacionesUsuario(String nombreUsuario) throws RemoteException;
    String getServidorHost(String nombreUsuario) throws RemoteException;
    String getNombreServidor() throws RemoteException;
}