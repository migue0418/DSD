// I_Donacion.java
package i_donacion;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface I_DonacionReplica extends Remote {
    int getNumUsuarios() throws RemoteException;
    boolean usuarioEstaRegistrado(String nombreUsuario) throws RemoteException;
    double getDineroDonadoServidor() throws RemoteException;
    String getNombreServidor() throws RemoteException;
    void addDineroDonacion(double dineroDonado) throws RemoteException;
    I_DonacionReplica getServidorReplica(String red, String nombreServidor) throws RemoteException;
    void addUsuario(String nombreUsuario, String password) throws RemoteException;
    boolean comprobarPassword(String nombreUsuario, String password) throws RemoteException;
    ArrayList<String> getUsuariosServidor() throws RemoteException;
    int getNumDonantesServidor() throws RemoteException;
}