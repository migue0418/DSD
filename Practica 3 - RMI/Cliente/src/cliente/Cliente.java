// cliente.java
package cliente;

import i_donacion.I_Donacion;
import java.rmi.registry.LocateRegistry;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        String host = "";
        System.out.println ("Escriba el nombre o IP del servidor: ");
        
        Scanner teclado = new Scanner(System.in);
        do {
            host = teclado.nextLine();
        }while(host.isEmpty());
        // Crea e instala el gestor de seguridad
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            // Crea el stub para el cliente especificando el nombre del servidor
            Registry mireg = LocateRegistry.getRegistry(host, 1099);
            I_Donacion servidor = (I_Donacion)mireg.lookup("Servidor");
            
            System.out.println("--- Bienvenido a la página de DSD sin Fronteras ---");
            boolean continuar = true, inicioSesion = false;
            String opcionAnonimo, opcionUsuario, nombreUsuario = null, password, passwordNueva, servidorUsuario;
            double cantidad = 0;
            
            do {
                System.out.println("No has iniciado sesión, es necesario para poder donar!");
                System.out.println("\t- Registrarse (R)\n\t- Iniciar Sesión (I)\n\t- Cerrar Programa (E)");
                do {
                    opcionAnonimo = teclado.nextLine();
                }while(opcionAnonimo.isEmpty());
                opcionAnonimo = opcionAnonimo.toUpperCase();
                
                switch(opcionAnonimo){
                    case "R":
                        System.out.println("-- Registrar Nuevo Usuario --");
                        System.out.println("Introduzca su nombre de usuario:");
                        do {
                            nombreUsuario = teclado.nextLine();
                        }while(nombreUsuario.isEmpty());
                        System.out.println("Introduzca su password:");
                        do {
                            password = teclado.nextLine();
                        }while(password.isEmpty());
                        if(servidor.registrar(nombreUsuario, password)){
                            System.out.println("El usuario " + nombreUsuario + " ha sido registrado en el servidor " + servidor.getServidorHost(nombreUsuario));
                        }
                        else {
                            System.out.println("Error! No se ha podido crear el usuario");
                        }
                        break;
                        
                    case "I":
                        System.out.println("-- Iniciar Sesión --");
                        System.out.println("Introduzca su nombre de usuario:");
                        do {
                            nombreUsuario = teclado.nextLine();
                        }while(nombreUsuario.isEmpty());
                        System.out.println("Introduzca su password:");
                        do {
                            password = teclado.nextLine();
                        }while(password.isEmpty());
                        if(servidor.iniciarSesion(nombreUsuario, password)){
                            // cambiamos de servidor en caso de que sea distinto
                            servidorUsuario = servidor.getServidorHost(nombreUsuario);
                            if(!servidorUsuario.equals(servidor.getNombreServidor())){
                                servidor = (I_Donacion)mireg.lookup(servidor.getServidorHost(nombreUsuario));
                            }
                            inicioSesion = true;
                            System.out.println("\nBIENVENIDO " + nombreUsuario);
                        }
                        else {
                            System.out.println("Error! No se ha podido iniciar sesion, usuario y/o password incorrectas");
                            inicioSesion = false;
                        }
                        break;
                        
                    case "E":
                        System.out.println("-- Cerrando Programa --");
                        continuar = inicioSesion = false;
                        break;
                    
                    default:
                        System.out.println("Selecciona una opcion correcta!!\n");
                        break;
                }
                
                while(inicioSesion){
                    System.out.println("\t- Donar a la causa (D)\n"+
                            "\t- Consultar mi numero de donaciones (N)\n"+
                            "\t- Consultar dinero donado por mi (M)\n"+
                            "\t- Consultar numero total de donantes (U)\n"+
                            "\t- Consultar total donado a la causa (T)\n"+
                            "\t- Cambiar Password (P)\n"+
                            "\t- Cerrar Sesion (C)\n"+
                            "\t- Cerrar Programa (E)");
                    do {
                        opcionUsuario = teclado.nextLine();
                    }while(opcionUsuario.isEmpty());
                    opcionUsuario = opcionUsuario.toUpperCase();
                    
                    switch(opcionUsuario){
                        case "D":
                            System.out.println("-- Donar a la causa --");
                            System.out.println("Introduce la cantidad que quieres donar:");
                            do {
                                try {
                                    cantidad = Double.parseDouble(teclado.nextLine());
                                } 
                                catch(NumberFormatException e){
                                    System.err.println("ERROR! No puedes introducir un input que no sea un numero: " +  e.getMessage());
                                }
                                if (cantidad <= 0){
                                    System.out.println("Introduzca una cantidad valida");
                                }
                            }while(cantidad <= 0);
                            servidor.hacerDonacion(nombreUsuario, cantidad);
                            break;
                            
                        case "N":
                            System.out.println("-- Consultar mi numero de donaciones realizadas --");
                            System.out.println("Has realizado " + servidor.getNumDonacionesUsuario(nombreUsuario) + " donaciones.");
                            break;
                            
                        case "M":
                            System.out.println("-- Consultar dinero donado por mi --");
                            System.out.println("Has donado un total de " + servidor.getSubtotalUsuario(nombreUsuario) + "€.");
                            break;
                            
                        case "U":
                            System.out.println("-- Consultar numero total de donantes --");
                            System.out.println("Un total de " + servidor.getTotalDonantes(nombreUsuario) + " personas han donado a la causa.");
                            break;
                            
                        case "T":
                            System.out.println("-- Consultar total donado a la causa --");
                            if(servidor.getSubtotalUsuario(nombreUsuario) == 0){
                                System.err.println("Para consultar tienes que donar primero!");
                            }
                            else{
                                System.out.println("Un total de " + servidor.getTotalDinero(nombreUsuario) + "€ ha sido donado a la causa.");
                            }
                            break;
                        
                        case "P":
                            System.out.println("-- Cambiar Password --");
                            System.out.println("Introduce la password antigua:");
                            do {
                                password = teclado.nextLine();
                            }while(password.isEmpty());
                            
                            System.out.println("Introduce la password nueva:");
                            do {
                                passwordNueva = teclado.nextLine();
                            }while(passwordNueva.isEmpty());
                            
                            if(servidor.cambiarPassword(nombreUsuario, password, passwordNueva)){
                                System.out.println("Password modificada con exito");
                            }
                            else {
                                System.err.println("ERROR! Password incorrecta");
                            }
                            break;
                            
                        case "C":
                            System.out.println("-- Cerrando Sesion --");
                            inicioSesion = false;
                            break;
                            
                        case "E":
                            System.out.println("-- Cerrando Programa --");
                            continuar = inicioSesion = false;
                            break;
                    
                        default:
                            System.out.println("Selecciona una opcion correcta!!\n");
                            break;
                    }
                }
                
            } while(continuar);
            
        } catch(NotBoundException | RemoteException e) {
            System.err.println("Exception del sistema: " + e);
        }
        System.exit(0);
    }
}