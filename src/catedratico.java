/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arnold
 */
public class catedratico {
    private int catedraticoid;
    private String nombre;
    private String correo;

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public int getCatedraticoid() {
        return catedraticoid;
    }

    public void setCatedraticoid(int id_cated) {
        this.catedraticoid = id_cated;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre_cated) {
        this.nombre= nombre_cated;
    }
}
