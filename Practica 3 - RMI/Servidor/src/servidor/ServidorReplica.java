// servidor.java = Programa servidor
package servidor;

import donacion.DonacionReplica;
import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;

public class ServidorReplica {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {            
            DonacionReplica servidorReplica = new DonacionReplica("ServidorReplica", "Servidor");
            Naming.rebind("ServidorReplica", servidorReplica);
            
            System.out.println("Servidor Replica preparado");
        } catch (RemoteException | MalformedURLException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}