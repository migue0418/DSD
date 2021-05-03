// Usuario.java
package donacion;


// Un usuario del sistema tiene los atributos nombre, numDonaciones y dineroDonado.
//   Nos servirá para poder hacer los cálculos y consultas más rápido.
public class Usuario {
    private String nombre;
    private int numDonaciones;
    private double dineroDonado;
    private String password;
    
    Usuario(String nombre, String password) {
        this.nombre = nombre;
        this.numDonaciones = 0;
        this.dineroDonado = 0;
        this.password = password;
    }
    
    String getNombre() {
        return nombre;
    }
    
    int getNumDonaciones() {
        return numDonaciones;
    }
    
    double getDineroDonado() {
        return dineroDonado;
    }
    
    public boolean comprobarPassword(String pass){
        boolean correcto = false;
        if(this.password.equals(pass)){
            correcto = true;
        }
        
        return correcto;
    }
    
    void addDonacion(double cantidad){
        this.numDonaciones++;
        this.dineroDonado += cantidad;
    }
}
