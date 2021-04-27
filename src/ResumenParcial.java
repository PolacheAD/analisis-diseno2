
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
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
public class ResumenParcial extends javax.swing.JFrame {
    XSSFWorkbook libro;
    XSSFSheet sheet;
    XSSFRow fila;
    XSSFCell celda;
    CellStyle style;
    
    File abrir;
    JFileChooser file;
    DefaultTableModel modelo;
    LocalDate hoy, fechai, fechaf;
    int filainicial;
    
    seccion tempsecc;
    ArrayList <seccion> seccs;
    libreria_sql.Libreria_sql con;
    ResultSet regreso;
    catedratico sesion;
    asignatura tempasign;
    ArrayList <asignatura> asigns;
    String sent, temporal_string, nsec;
    /**
     * Creates new form ResumenParcial
     */
    public ResumenParcial(catedratico sesion) {
        initComponents();
        jPanel2.setVisible(false);
        this.sesion = sesion;
        con = new libreria_sql.Libreria_sql();
        modelo = (DefaultTableModel) jTable1.getModel();
        modelo.setRowCount(0);
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
    
    public XSSFWorkbook crear_libro(){
        hoy = LocalDate.now();
        
        //plantilla del archivo
        abrir = new File("C:\\Plantillas\\ResParc.xlsx");
        try (FileInputStream entrada = new FileInputStream(abrir)){
            libro= new XSSFWorkbook(entrada);
            sheet = libro.getSheetAt(0);
            modelo = (DefaultTableModel) jTable1.getModel();
            //Estilo de celda
            style = libro.createCellStyle();
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            
            //Escribiendo la clase
            fila = sheet.getRow(4);
            celda = fila.getCell(0);
            celda.setCellValue("Resumen de Asistencia en el Parcial por Estudiante");
            
            fila = sheet.getRow(6);
            celda = fila.getCell(0);
            celda.setCellValue(String.valueOf(jComboBox1.getSelectedItem()));
            celda = fila.getCell(8);
            celda.setCellValue(String.valueOf(jComboBox1.getSelectedItem()));
            
            //Escribiendo la fechas
            fila = sheet.getRow(7);
            celda = fila.getCell(1);
            celda.setCellValue("Parcial (" + jTextField1.getText() + " / " + jTextField2.getText() +")");
           
            //Llenando la primera fila
            fila = sheet.createRow(10);
            //Aplicando estilo a celdas
            for(int j=1; j<7; j++){
                celda = fila.createCell(j);
                celda.setCellStyle(style);
            }
            //No.
            celda = fila.getCell(1);
            celda.setCellValue(1);
            //Nombre
            celda = fila.getCell(2);
            celda.setCellValue(String.valueOf(modelo.getValueAt(0, 0)));
            //No. Cuenta
            celda = fila.getCell(3);
            celda.setCellValue(Double.parseDouble(String.valueOf(modelo.getValueAt(0, 1))));
            //Asistencias
            celda = fila.getCell(4);
            celda.setCellValue(Double.parseDouble(String.valueOf(modelo.getValueAt(0, 2))));            
            //Inasistencias
            celda = fila.getCell(5);
            celda.setCellValue(Double.parseDouble(String.valueOf(modelo.getValueAt(0, 3))));
            //Excusas
            celda = fila.getCell(6);
            celda.setCellValue(Double.parseDouble(String.valueOf(modelo.getValueAt(0, 4))));           
            
            //Creando el resto de filas
            for(int i=1; i<modelo.getRowCount();i++){
                filainicial = 10; 
                fila = sheet.createRow(i+filainicial);
                
                //Aplicando estilo a celdas
                for(int j=1; j<7; j++){
                    celda = fila.createCell(j);
                    celda.setCellStyle(style);
                }
                
                //No.
                celda = fila.getCell(1);
                celda.setCellValue(i+1);
                
                //Nombre
                celda = fila.getCell(2);
                celda.setCellValue(String.valueOf(modelo.getValueAt(i, 0)));
                
                //No.Cuenta
                celda = fila.getCell(3);
                celda.setCellValue(Double.parseDouble(String.valueOf(modelo.getValueAt(i, 1))));
                
                //Asistencias
                celda = fila.getCell(4);
                celda.setCellValue(Double.parseDouble(String.valueOf(modelo.getValueAt(i, 2))));
                
                //Inasistencias
                celda = fila.getCell(5);
                celda.setCellValue(Double.parseDouble(String.valueOf(modelo.getValueAt(i, 3))));
                
                //Excusas
                celda = fila.getCell(6);
                celda.setCellValue(Double.parseDouble(String.valueOf(modelo.getValueAt(i, 4))));
            } 
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(65, 105, 225));
        jLabel1.setText("Seleccione el rango de fechas del parcial:");

        jLabel2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(65, 105, 225));
        jLabel2.setText("Desde:");

        jTextField1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(65, 105, 225));
        jLabel4.setText("(Año-Mes-Dia)");

        jLabel3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(65, 105, 225));
        jLabel3.setText("Hasta:");

        jTextField2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(65, 105, 225));
        jLabel5.setText("(Año-Mes-Dia)");

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(65, 105, 225));
        jButton1.setText("Generar resumen");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jComboBox1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Instalaciones Electricas" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(65, 105, 225));
        jLabel6.setText("Clase:");

        jLabel7.setFont(new java.awt.Font("Leelawadee UI Semilight", 1, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(65, 105, 225));
        jLabel7.setText("Resumen de asistencia del parcial por estudiante");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(211, 211, 211)
                        .addComponent(jButton1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jLabel7))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(117, 117, 117)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGap(62, 62, 62)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel4)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel2)
                                            .addGap(18, 18, 18)
                                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(71, 71, 71)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel5)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jLabel3)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(69, 69, 69)
                                    .addComponent(jLabel1))))))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel7)
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(60, 60, 60)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 78, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(42, 42, 42))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Abigail Sanchez", "20201235495", "30", "0", "0"},
                {"Alejandra", "20156698216", "19", "2", "9"},
                {"Arnold Polanco", "20176658936", "30", "0", "0"},
                {"Bessy", "20018920235", "18", "8", "4"},
                {"Brenedin", "20162920111", "28", "2", "0"},
                {"Brenedin Gomez", "20176659863", "26", "4", "0"},
                {"Elena", "20117896532", "25", "3", "2"},
                {"Fernando", "20114789632", "29", "1", "0"},
                {"Gerson", "20192038556", "19", "7", "4"},
                {"Javier", "20176665893", "29", "0", "1"},
                {"Juan Perez", "20116698556", "30", "0", "0"},
                {"Paola Garcia", "20146688952", "27", "2", "1"},
                {"Pedro Cruz", "20205589663", "27", "2", "1"},
                {"Raul", "20156698254", "25", "4", "1"},
                {"Rodrigo", "20175589996", "24", "6", "0"},
                {"Santiago", "20172016669", "29", "1", "0"},
                {"Sergio Vasquez", "20165587741", "27", "1", "2"},
                {"Valentina", "20136698874", "24", "5", "1"},
                {"Yorleny Ramos", "20192020887", "20", "5", "5"},
                {"Zoe", "20205549623", "29", "1", "0"}
            },
            new String [] {
                "Nombre", "No. Cuenta", "Asistencias", "Faltas", "Excusas"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(365, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(328, Short.MAX_VALUE))
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try{
            fechai =LocalDate.parse(jTextField1.getText());
            fechaf =LocalDate.parse(jTextField2.getText());
            if(jTextField1.getText().equals("") && jTextField2.getText().equals("")){
                JOptionPane.showMessageDialog(null, "Por favor, ingrese ambas fechas");
            }
            else if(fechai.isAfter(fechaf)){
                JOptionPane.showMessageDialog(null, "Error: fechas no permitidas.");
            }
            else if(String.valueOf(jComboBox1.getSelectedItem()).equals("Elija una Clase...") == false){
                JOptionPane.showMessageDialog(this, "Por favor, elija una clase");
            }
            else{
                
                con.conectar();
                modelo = (DefaultTableModel)jTable1.getModel();
                modelo.setRowCount(0);
                jTable1.setModel(modelo);

                sent = "select al.Nombre, al.NumeroCuenta, SUM(a.Asistio = 'S') as Asistencias, SUM(a.Asistio = 'N') as Inasistencias, count(e.NumeroCuenta) as Excusas\n" +
                        "from Alumno al\n" +
                        "inner join (select * from Asistencia where Fecha between '"+jTextField1.getText()+"' and '"+jTextField2.getText()+"') a\n" +
                        "on al.NumeroCuenta = a.NumeroCuenta\n" +
                        "left join (select * from Excusa where Fecha between '"+jTextField1.getText()+"' and '"+jTextField2.getText()+"') e\n" +
                        "on al.NumeroCuenta = e.NumeroCuenta\n" +
                        "inner join InfoSeccion sd\n" +
                        "on  a.SeccionID = sd.SeccionID\n" +
                        "where a.SeccionID = '"+tempsecc.getNumseccion()+"' and a.AsignaturaID = '"+tempsecc.getId_asig()+"'\n" +
                        "group by sd.AsignaturaID, sd.SeccionID, al.Nombre, a.NumeroCuenta";
                con.seleccionar_jtable(sent, jTable1);

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
                        JOptionPane.showMessageDialog(null, "Hubo un error con la creación de la hoja. Intente nuevamente");
                    }
                }
                this.dispose();
            }
        }catch(DateTimeException x){
            JOptionPane.showMessageDialog(this, "Por favor revise la consistencia de las fechas ingresadas");
        }
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED){
            tempasign = new asignatura();
            tempsecc = new seccion(); 
            temporal_string = String.valueOf(jComboBox1.getSelectedItem());
            if(temporal_string.equals("Elija una Clase...")){
                jButton1.setEnabled(false);
                modelo = (DefaultTableModel)jTable1.getModel();
                modelo.setRowCount(0);
                jTable1.setModel(modelo);
            }
            else{
                jButton1.setEnabled(true);
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
            java.util.logging.Logger.getLogger(ResumenParcial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ResumenParcial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ResumenParcial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ResumenParcial.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new ResumenParcial().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
