/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arnold
 */
public class seccion {
    private String numseccion, fecha_i, fecha_f, id_asig, dias;
    private int id_catedratico, faltas_cated , hi, hf, semana;

    public int getFaltas_cated() {
        return faltas_cated;
    }

    public void setFaltas_cated(int faltas_cated) {
        this.faltas_cated = faltas_cated;
    }
    public String getFecha_i() {
        return fecha_i;
    }

    public int getSemana() {
        return semana;
    }

    public void setSemana(int semana) {
        this.semana = semana;
    }

    public void setFecha_i(String fecha_i) {
        this.fecha_i = fecha_i;
    }

    public String getFecha_f() {
        return fecha_f;
    }

    public void setFecha_f(String fecha_f) {
        this.fecha_f = fecha_f;
    }
    public String getNumseccion() {
        return numseccion;
    }

    public void setNumseccion(String numseccion) {
        this.numseccion = numseccion;
    }

    public String getId_asig() {
        return id_asig;
    }

    public void setId_asig(String id_asig) {
        this.id_asig = id_asig;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public int getId_catedratico() {
        return id_catedratico;
    }

    public void setId_catedratico(int id_catedratico) {
        this.id_catedratico = id_catedratico;
    }

    public int getHi() {
        return hi;
    }

    public void setHi(int hi) {
        this.hi = hi;
    }

    public int getHf() {
        return hf;
    }

    public void setHf(int hf) {
        this.hf = hf;
    }
    
}
