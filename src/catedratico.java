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
    private int id_cated;
    private String nombre_cated;
    private String usuario, correo;

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public int getId_cated() {
        return id_cated;
    }

    public void setId_cated(int id_cated) {
        this.id_cated = id_cated;
    }

    public String getNombre_cated() {
        return nombre_cated;
    }

    public void setNombre_cated(String nombre_cated) {
        this.nombre_cated = nombre_cated;
    }
     public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
