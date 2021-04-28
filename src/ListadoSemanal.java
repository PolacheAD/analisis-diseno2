
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arnold
 */
public class ListadoSemanal extends javax.swing.JFrame {
    XSSFWorkbook libro;
    XSSFSheet sheet;
    XSSFRow fila;
    XSSFCell celda;
    XSSFCellStyle style, styleN, styleS, styleX;
    XSSFColor rojo, verde, gris;
    LocalDate fecha, lunes, sabado;
    java.util.Date fecha_cal;
    java.sql.Date fecha_sql;
    ArrayList <LocalDate> semana;
    int diasemana;
    File abrir;
    JFileChooser file;
    DefaultTableModel modelo;
    int filainicial;
    Date hoy;
    Calendar calendario;
    asuntosDeTiempo verificar;

    catedratico sesion;
    seccion tempsecc;
    ArrayList <seccion> seccs;
    libreria_sql.Libreria_sql con;
    ResultSet regreso;
    asignatura tempasign;
    ArrayList <asignatura> asigns;
    String sent, temporal_string, nsec, coldias;
    XSSFColor blanco;
    XSSFFont fuente;
    /**
     * Creates new form ListadoSemanal
     */
    public ListadoSemanal(catedratico sesion) {
        initComponents();
        this.sesion = sesion;
        con = new libreria_sql.Libreria_sql();
        verificar = new asuntosDeTiempo();
        hoy = Date.valueOf(LocalDate.now());
        jDateChooser1.setDateFormatString("yyyy-MM-dd");
        jDateChooser1.setDate(hoy);
        jPanel2.setVisible(false);
        modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);
        jTable1.setModel(modelo);
    }
    
    public void llenar_combo(){
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Elija una Clase...");
        for (seccion secc : seccs) {
            for(asignatura asign : asigns){
                if(secc.getId_asig().equals(asign.getCodigo_asig()) /*&& secc.getId_catedratico() == sesion.getCatedraticoid()*/){
                   temporal_string = asign.getNombre_asig();
                   this.jComboBox1.addItem(secc.getNumseccion() + " - " + temporal_string);
                   break;
                }
            }   
        }
    }
    
    public void addSeccs(){
        seccs = new ArrayList();
        con.conectar();
        sent = "select * from InfoSeccion where CatedraticoID = '"+sesion.getCatedraticoid()+"'";
        regreso = con.seleccionar(sent);
        try {
            while(regreso.next()){
                tempsecc = new seccion();
                tempsecc.setNumseccion(regreso.getString("SeccionID"));
                tempsecc.setId_asig(regreso.getString("AsignaturaID"));
                tempsecc.setId_catedratico(regreso.getInt("CatedraticoID"));
                tempsecc.setHi(regreso.getInt("Horai"));
                tempsecc.setHf(regreso.getInt("Horaf"));
                tempsecc.setFecha_i(LocalDate.parse(regreso.getString("Fechai")));
                tempsecc.setFecha_f(LocalDate.parse(regreso.getString("Fechaf")));
                tempsecc.setDias(regreso.getString("Dias"));
                tempsecc.setFaltas_cated(regreso.getInt("DiasSinClase"));
                seccs.add(tempsecc);
            }
            System.out.println(seccs.size());
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            con.cerrar();
        }
    }
    public void addAsigns(){
        asigns = new ArrayList();
        con.conectar();
        sent ="select AsignaturaID, Nombre from Asignatura ";
        regreso = con.seleccionar(sent);
        try {
            while(regreso.next()){
                tempasign = new asignatura();
                tempasign.setCodigo_asig(regreso.getString("AsignaturaID"));
                tempasign.setNombre_asig(regreso.getString("Nombre"));
                asigns.add(tempasign);
            }
            llenar_combo();
        } catch (SQLException ex) {
            Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            con.cerrar();
        }
    }
    
    public void defsemana(){
        semana = new ArrayList();
        fecha_cal=jDateChooser1.getDate();
        fecha_sql = new java.sql.Date(fecha_cal.getTime());
        fecha = fecha_sql.toLocalDate();
        for(int i=0; i<8; i++){
            diasemana = fecha.getDayOfWeek().getValue();
            if (diasemana == 1){
                lunes = fecha;
                break;
            }
            if (diasemana == 7){
                lunes = fecha.minusDays(6);
                break;
            }
            fecha = fecha.minusDays(1);
        }
        
        fecha_cal=jDateChooser1.getDate();
        fecha_sql = new java.sql.Date(fecha_cal.getTime());
        fecha = fecha_sql.toLocalDate();
        for(int i=0; i<8; i++){
            diasemana = fecha.getDayOfWeek().getValue();
            if (diasemana == 6){
                sabado = fecha;
                break;
            }
            if (diasemana == 7){
                sabado = fecha.minusDays(1);
                break;
            }
            fecha = fecha.plusDays(1);
        }
        semana.add(lunes);
        for(int i=0; i<8; i++){
            if(semana.get(i).getDayOfWeek().getValue() == sabado.getDayOfWeek().getValue()){
                break;
            }
            semana.add(semana.get(i).plusDays(1));
        }
        
    }
    public void llenar_table(){
        defsemana();
        coldias="";
        for (LocalDate diasem : semana) {
            if(diasem.isBefore(LocalDate.now()) || diasem.isEqual(LocalDate.now())){   
                if(verificar.siToca2(tempsecc.getDias(), diasem)){
                    coldias = coldias + "IFNULL((select a.Asistio from Asistencia where a.Fecha = "
                            + "'"+String.valueOf(diasem)+"'), 'N') as "+verificar.dia_letra(diasem.getDayOfWeek().getValue())+",";
                }else{
                    coldias = coldias + "'-' as "+verificar.dia_letra(diasem.getDayOfWeek().getValue())+",";
                }
            }else{
                coldias = coldias + "'-' as "+verificar.dia_letra(diasem.getDayOfWeek().getValue())+",";
            }   
        }
        System.out.println(coldias);
        con.conectar();
        modelo = (DefaultTableModel)jTable1.getModel();
        modelo.setRowCount(0);
        jTable1.setModel(modelo);

        /*sent = "select al.Nombre, al.NumeroCuenta, "+coldias
                +"SUM(Asistio = 'N') as Faltas\n" +
                "from Alumno al\n" +
                "inner join Asistencia a\n" +
                "on al.NumeroCuenta = a.NumeroCuenta\n" +
                "inner join Asignatura ag\n" +
                "on a.AsignaturaID = ag.AsignaturaID\n" +
                "where a.AsignaturaID = '"+tempsecc.getId_asig()+"' "
                + "and a.SeccionID = '"+tempsecc.getNumseccion()+"'\n" +
                "group by a.NumeroCuenta\n"
                + "order by al.Nombre asc";*/
        sent = "select al.Nombre, \n" +
"       al.NumeroCuenta,\n" +
"       L.Asistio as Lu,\n" +
"       Mar.Asistio as Ma,\n" +
"       Mie.Asistio as Mi,\n" +
"       J.Asistio as Ju,\n" +
"       '-' as Vi,\n" +
"       '-' as Sa,\n" +
"       SUM(a.Asistio = 'N') as Faltas\n" +
"from Alumno al\n" +
"inner join Asistencia a\n" +
"on al.NumeroCuenta = a.NumeroCuenta\n" +
"inner join Asignatura ag\n" +
"on a.AsignaturaID = ag.AsignaturaID\n" +
"inner join(select NumeroCuenta, Asistio from Asistencia where Fecha = '2021-04-19') as L on L.NumeroCuenta = al.NumeroCuenta\n" +
"inner join(select NumeroCuenta, Asistio from Asistencia where Fecha = '2021-04-20') as Mar on Mar.NumeroCuenta = al.NumeroCuenta\n" +
"inner join(select NumeroCuenta, Asistio from Asistencia where Fecha = '2021-04-21') as Mie on Mie.NumeroCuenta = al.NumeroCuenta\n" +
"inner join(select NumeroCuenta, Asistio from Asistencia where Fecha = '2021-04-22') as J on J.NumeroCuenta = al.NumeroCuenta\n" +
"where a.AsignaturaID = 'IS702' and a.SeccionID = '2000'\n" +
"group by a.NumeroCuenta\n" +
"order by al.Nombre asc";
        con.seleccionar_jtable(sent, jTable1);
    }
    public void asigStyle(XSSFCell col,String a, XSSFCellStyle si, XSSFCellStyle no, XSSFCellStyle cross){
        /*if(col.getStringCellValue().equals("S")){
            col.setCellStyle(si);
        }
        if(col.getStringCellValue().equals("N")){
            col.setCellStyle(no);
        }
        if(col.getStringCellValue().equals("X")){
            col.setCellStyle(cross);
        }*/
        if(a.equals("S")){
            col.setCellStyle(si);
        }
        if(a.equals("N")){
            col.setCellStyle(no);
        }
        if(a.equals("X")){
            col.setCellStyle(cross);
        }
    }
    
    public XSSFWorkbook crear_libro(){
        //plantilla del archivo
        abrir = new File("C:\\Plantillas\\AsistenciaAsigSemana.xlsx");
        try (FileInputStream entrada = new FileInputStream(abrir)){
            libro= new XSSFWorkbook(entrada);
            verde = new XSSFColor(new java.awt.Color(0,35,102),null);
            rojo = new XSSFColor(new java.awt.Color(255,223,0),null);
            blanco = new XSSFColor(new java.awt.Color(255,255,255),null);
            gris = new XSSFColor(new java.awt.Color(217,217,217),null);
            sheet = libro.getSheetAt(0);
            modelo = (DefaultTableModel) jTable1.getModel();
            //Estilo de celda
            style = libro.createCellStyle();
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            
            //Estilo de celda "S"
            styleS = libro.createCellStyle();
            styleS.cloneStyleFrom(style);
            styleS.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleS.setFillForegroundColor(verde);
            
            //Estilo de celda "N"
            styleN = libro.createCellStyle();
            styleN.cloneStyleFrom(style);
            styleN.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleN.setFillForegroundColor(rojo);
            
            
            //Estilo de celda "X"
            styleX = libro.createCellStyle();
            styleX.cloneStyleFrom(style);
            styleX.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleX.setFillForegroundColor(gris.getIndex());
            
            //Escribiendo la fecha
            /*fila = sheet.getRow(6);
            celda = fila.getCell(6);
            celda.setCellValue("Fecha: " + hoy.toString());*/
            fila = sheet.getRow(8);
            celda = fila.getCell(0);
            celda.setCellValue(String.valueOf(jComboBox1.getSelectedItem()));
            celda = fila.getCell(2);
            celda.setCellValue("Catedrático: "+sesion.getNombre());
            
            //Escribiendo la semana
            fila = sheet.getRow(6);
            celda = fila.getCell(0);
            celda.setCellValue("Semana del Lunes "+lunes.toString()+" al Sábado "+sabado.toString());
            
            //Creando el resto de filas
            for(int i=0; i<modelo.getRowCount();i++){
                filainicial = 10; 
                fila = sheet.createRow(i+filainicial);
                
                //Aplicando estilo a celdas
                for(int j=0; j<9; j++){
                    celda = fila.createCell(j);
                    celda.setCellStyle(style);
                }
                
                //Nombre
                celda = fila.getCell(0);
                celda.setCellValue(String.valueOf(modelo.getValueAt(i, 0)));
                
                //No. Cuenta
                celda = fila.getCell(1);
                celda.setCellValue(String.valueOf(modelo.getValueAt(i, 1)));
                
                //Lu
                celda = fila.getCell(2);
                //celda.setCellValue(String.valueOf(modelo.getValueAt(i, 2)));
                asigStyle(celda, String.valueOf(modelo.getValueAt(i, 2)), styleS, styleN, styleX);
                
                //Ma
                celda = fila.getCell(3);
                //celda.setCellValue(String.valueOf(modelo.getValueAt(i, 3)));
                asigStyle(celda, String.valueOf(modelo.getValueAt(i, 3)),styleS, styleN, styleX);
                
                //Mi
                celda = fila.getCell(4);
                //celda.setCellValue(String.valueOf(modelo.getValueAt(i, 4)));
                asigStyle(celda,String.valueOf(modelo.getValueAt(i, 4)), styleS, styleN, styleX);
                
                //Ju
                celda = fila.getCell(5);
                //celda.setCellValue(String.valueOf(modelo.getValueAt(i, 5)));
                asigStyle(celda,String.valueOf(modelo.getValueAt(i, 5)), styleS, styleN, styleX);
                
                //Vi
                celda = fila.getCell(6);
                //celda.setCellValue(String.valueOf(modelo.getValueAt(i, 6)));
                asigStyle(celda, String.valueOf(modelo.getValueAt(i, 6)),styleS, styleN, styleX);
                
                //Sa
                celda = fila.getCell(7);
                //celda.setCellValue(String.valueOf(modelo.getValueAt(i, 7)));
                asigStyle(celda,String.valueOf(modelo.getValueAt(i, 7)), styleS, styleN, styleX);
                
                //Faltas
                celda = fila.getCell(8);
                celda.setCellValue(String.valueOf(modelo.getValueAt(i, 8)));
            }
            
            fila = sheet.createRow(filainicial+modelo.getRowCount()+1);
            celda = fila.createCell(0);
            celda.setCellStyle(style);
            celda.setCellValue("Clases no impartidas");
            
            celda = fila.createCell(1);
            celda.setCellStyle(style);
            celda.setCellValue(0);

            entrada.close(); //Cerrando el FileInputStream
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ListClases.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ListClases.class.getName()).log(Level.SEVERE, null, ex);
        }
        return libro;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"DANIEL ALEJANDRO ROJO MEJIA", "20172000111", "S", "-", "S", "S", "-", "-", "0"},
                {"JOHN PAUL HUGHES MEDINA", "20162000822", "S", "-", "S", "N", "-", "-", "1"},
                {"CARLOS AUGUSTO VELEZ PONCE", "20152005987", "N", "-", "N", "S", "-", "-", "2"}
            },
            new String [] {
                "Estudiante", "No.Cuenta", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa", "Faltas"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(128, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(199, Short.MAX_VALUE))
        );

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Leelawadee UI Semilight", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(65, 105, 225));
        jLabel1.setText("Listado semanal de asistencia de la clase");

        jButton3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(65, 105, 225));
        jButton3.setText("Generar reporte");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(65, 105, 225));
        jLabel6.setText("Seleccione la semana que desea consultar:");

        jLabel2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(65, 105, 225));
        jLabel2.setText("(Año-Mes-Día)");

        jComboBox1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Instalaciones Electricas para IS-2000", " " }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(65, 105, 225));
        jLabel3.setText("Clase:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(98, 98, 98)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(29, 29, 29)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(269, 269, 269)
                        .addComponent(jLabel2)))
                .addContainerGap(76, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(237, 237, 237)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(233, 233, 233))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel1)
                .addGap(63, 63, 63)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54)
                .addComponent(jLabel6)
                .addGap(32, 32, 32)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        if(String.valueOf(jComboBox1.getSelectedItem()).equals("Elija una Clase...") == false){
            llenar_table();
            file = new JFileChooser();
            file.showSaveDialog(this);
            File guardar = file.getSelectedFile();
            if(guardar!=null){
                XSSFWorkbook aqui = crear_libro();
                FileOutputStream fileOuS;
                try {
                    if(guardar.getPath().contains("xlsx")){
                         fileOuS= new FileOutputStream(guardar);
                    }else{
                        fileOuS= new FileOutputStream(guardar+".xlsx");
                    }

                    if (guardar.exists()) {// si el archivo existe se elimina
                        guardar.delete();
                        System.out.println("Archivo eliminado");
                    }
                    aqui.write(fileOuS);
                    fileOuS.flush();
                    fileOuS.close();
                    JOptionPane.showMessageDialog(this,"Informe generado con éxito");                
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,"Error al reemplazar archivo: La hoja de cálculo está abierta.");
                }
            }
            this.dispose();
        }else{
            JOptionPane.showMessageDialog(this, "Por favor, elija una clase");
        }
        
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED){
            tempasign = new asignatura();
            tempsecc = new seccion(); 
            temporal_string = String.valueOf(jComboBox1.getSelectedItem());
            if(temporal_string.equals("Elija una Clase...")){
                
            }
            else{
                nsec = temporal_string.substring(0, 4).trim();
                temporal_string = temporal_string.substring(6).trim();
                for(asignatura asign : asigns){
                    if(temporal_string.equals(asign.getNombre_asig())){
                        tempasign = asign;
                        break;
                    }
                }
                for(seccion secc : seccs){
                    if(secc.getNumseccion().equals(nsec) && tempasign.getCodigo_asig().equals(secc.getId_asig())){
                        tempsecc = secc;
                        break;
                    }
                }
            }
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ListadoSemanal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ListadoSemanal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ListadoSemanal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ListadoSemanal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new ListadoSemanal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
