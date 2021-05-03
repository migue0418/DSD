// servidor.java = Programa servidor
package servidor;

import donacion.Donacion;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;

public class Servidor {
    
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            // Crea una instancia de donacion
            Registry reg = LocateRegistry.createRegistry(1099);
            
            Donacion servidor = new Donacion("Servidor", "ServidorReplica");
            Naming.rebind("Servidor", servidor);
            
            System.out.println("Servidor preparado");
        } catch (RemoteException | MalformedURLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}