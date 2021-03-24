/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arnold
 */
public class Estudiante {
    String nombre_es, cuenta_es, asis_hoy;
    int faltas;
    
    public Estudiante(){
        asis_hoy = "-";
    }
    
    public String getNombre_es() {
        return nombre_es;
    }

    public void setNombre_es(String nombre_es) {
        this.nombre_es = nombre_es;
    }

    public String getCuenta_es() {
        return cuenta_es;
    }

    public void setCuenta_es(String cuenta_es) {
        this.cuenta_es = cuenta_es;
    }

    public String getAsis_hoy() {
        return asis_hoy;
    }

    public void setAsis_hoy(String asis_hoy) {
        this.asis_hoy = asis_hoy;
    }

    public int getFaltas() {
        return faltas;
    }

    public void setFaltas(int faltas) {
        this.faltas = faltas;
    }
}
