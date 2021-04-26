
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arnold
 */
public class asuntosDeTiempo {
    ArrayList<Integer> dias_int;
    Calendar Cl;
    LocalDateTime localDate;
    String [] dias_string;
    
    public asuntosDeTiempo(){
        this.Cl = new GregorianCalendar();
        this.localDate = LocalDateTime.now();
    }
    public boolean siToca(String dias){
        dias_int = new ArrayList();
        dias_string = dias.split(",");
        for(int i=0;i<dias_string.length;i++){
            switch (dias_string[i]){
                case "Lu":
                    dias_int.add(1);
                    break;       
                case "Ma":
                    dias_int.add(2);
                    break; 
                case "Mi":
                    dias_int.add(3);
                    break; 
                case "Ju":
                    dias_int.add(4);
                    break; 
                case "Vi":
                    dias_int.add(5);
                    break; 
                case "Sa":
                    dias_int.add(6);
                    break;
                case "Do":
                    dias_int.add(7);
            }
        }
        for(int dia : dias_int){
            if (localDate.getDayOfWeek().getValue() == dia){
                return true;
            }
        }
        return false;
    }
    
    public boolean siToca2(String dias, LocalDate diasem){
        dias_int = new ArrayList();
        dias_string = dias.split(",");
        for(int i=0;i<dias_string.length;i++){
            switch (dias_string[i]){
                case "Lu":
                    dias_int.add(1);
                    break;       
                case "Ma":
                    dias_int.add(2);
                    break; 
                case "Mi":
                    dias_int.add(3);
                    break; 
                case "Ju":
                    dias_int.add(4);
                    break; 
                case "Vi":
                    dias_int.add(5);
                    break; 
                case "Sa":
                    dias_int.add(6);
                    break;
                case "Do":
                    dias_int.add(7);
            }
        }
        for(int dia : dias_int){
            if (diasem.getDayOfWeek().getValue() == dia){
                return true;
            }
        }
        return false;
    }
    
    public boolean siToca_faltas(String dias, String evaluar){
        dias_int = new ArrayList();
        int evaluar_int = 0;
        dias_string = dias.split(",");
        for(int i=0;i<dias_string.length;i++){
            switch (dias_string[i]){
                case "Lu":
                    dias_int.add(1);
                    break;       
                case "Ma":
                    dias_int.add(2);
                    break; 
                case "Mi":
                    dias_int.add(3);
                    break; 
                case "Ju":
                    dias_int.add(4);
                    break; 
                case "Vi":
                    dias_int.add(5);
                    break; 
                case "Sa":
                    dias_int.add(6);
                    break;
                case "Do":
                    dias_int.add(7);
            }
        }
        switch (evaluar){
                case "Lu":
                    evaluar_int = 1;
                    break;       
                case "Ma":
                    evaluar_int = 2;
                    break; 
                case "Mi":
                    evaluar_int = 3;
                    break; 
                case "Ju":
                    evaluar_int = 4;
                    break; 
                case "Vi":
                    evaluar_int = 5;
                    break; 
                case "Sa":
                    evaluar_int = 6;
                    break;
                case "Do":
                    evaluar_int = 7;
            }
        for(int dia : dias_int){
            if (evaluar_int == dia){
                return true;
            }
        }
        return false;
    }
    public String dia_letra(int dia_num){
        switch(dia_num){
            case 1:
                return "Lu";
            case 2:
                return "Ma";
            case 3:
                return "Mi";
            case 4:
                return "Ju";
            case 5:
                return "Vi";
            case 6:
                return "Sa";
            case 7:
                return "Do";
        }
        return "";
    }
    
}
