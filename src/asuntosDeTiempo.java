
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
                case "L":
                    dias_int.add(1);
                    break;       
                case "Ma":
                    dias_int.add(2);
                    break; 
                case "Mi":
                    dias_int.add(3);
                    break; 
                case "J":
                    dias_int.add(4);
                    break; 
                case "V":
                    dias_int.add(5);
                    break; 
                case "S":
                    dias_int.add(6);
                    break;
            }
        }
        for(int dia : dias_int){
            if (localDate.getDayOfWeek().getValue() == dia){
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
                case "L":
                    dias_int.add(1);
                    break;       
                case "Ma":
                    dias_int.add(2);
                    break; 
                case "Mi":
                    dias_int.add(3);
                    break; 
                case "J":
                    dias_int.add(4);
                    break; 
                case "V":
                    dias_int.add(5);
                    break; 
                case "S":
                    dias_int.add(6);
                    break;
            }
        }
        switch (evaluar){
                case "L":
                    evaluar_int = 1;
                    break;       
                case "Ma":
                    evaluar_int = 2;
                    break; 
                case "Mi":
                    evaluar_int = 3;
                    break; 
                case "J":
                    evaluar_int = 4;
                    break; 
                case "V":
                    evaluar_int = 5;
                    break; 
                case "S":
                    evaluar_int = 6;
                    break;
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
                return "L";
            case 2:
                return "Ma";
            case 3:
                return "Mi";
            case 4:
                return "J";
            case 5:
                return "V";
            case 6:
                return "S";
        }
        return "";
    }
    
}
