
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
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
public class ResumenPeriodo extends javax.swing.JFrame {
    XSSFWorkbook libro;
    XSSFSheet sheet;
    XSSFRow fila;
    XSSFCell celda;
    CellStyle style;
    
    
    File abrir;
    JFileChooser file;
    DefaultTableModel modelo;
    LocalDate hoy;
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
     * Creates new form ResumenPeriodo
     */
    public ResumenPeriodo(catedratico sesion) {
        initComponents();
        jPanel2.setVisible(false);
        this.sesion = sesion;
        con = new libreria_sql.Libreria_sql();
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
    
    public XSSFWorkbook crear_libro(){
        hoy = LocalDate.now();
        
        //plantilla del archivo
        abrir = new File("C:\\Plantillas\\ResPer.xlsx");
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
            
            //Escribiendo la fecha
            /*fila = sheet.getRow(6);
            celda = fila.getCell(6);
            celda.setCellValue("Fecha: " + hoy.toString());*/
           
            fila = sheet.getRow(6);
            celda = fila.getCell(0);
            celda.setCellValue(String.valueOf(jComboBox1.getSelectedItem()));
            celda = fila.getCell(8);
            celda.setCellValue(String.valueOf(jComboBox1.getSelectedItem()));
            
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
        jComboBox1 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(65, 105, 225));
        jLabel1.setText("Seleccione la clase de la cual desea obtener un resumen:");

        jComboBox1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Instalaciones Electricas" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(42, 126, 211));
        jButton1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Generar resumen");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(65, 105, 225));
        jLabel2.setText("Resumen de asistencia del período por estudiante");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(40, 40, 40))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(175, 175, 175)
                        .addComponent(jButton1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(26, 26, 26)
                .addComponent(jLabel1)
                .addGap(35, 35, 35)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(37, 37, 37))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Abigail Sanchez", "20201235495", "90", "0", "0"},
                {"Arnold Polanco", "20175123689", "75", "6", "9"},
                {"Astrid", "20206598312", "90", "0", "0"},
                {"Bayron", "20018920235", "62", "24", "4"},
                {"Brenedin", "20162920489", "84", "6", "0"},
                {"Brenedin Gomez", "20156891155", "78", "12", "0"},
                {"Elena", "20146569985", "79", "9", "2"},
                {"Fernando", "20178458465", "87", "3", "0"},
                {"Fredy", "20192038668", "65", "21", "4"},
                {"Jose", "20171556987", "89", "0", "1"},
                {"Juan Perez", "20147659856", "90", "0", "0"},
                {"Paola Garcia", "20119956536", "83", "6", "1"},
                {"Pedro Cruz", "20186655653", "83", "6", "1"},
                {"Ramon", "20182937155", "77", "12", "1"},
                {"Roberto", "20195511602", "72", "18", "0"},
                {"Santiago", "20172016556", "87", "3", "0"},
                {"Selvin", "20156689318", "85", "3", "2"},
                {"Sergio Vasquez", "20179963326", "74", "15", "1"},
                {"Teresa", "20192020223", "70", "15", "5"},
                {"Yorleny Ramos", "20148896632", "87", "3", "0"}
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(70, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(62, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        if(String.valueOf(jComboBox1.getSelectedItem()).equals("Elija una Clase...") == false){
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
        }else{
            JOptionPane.showMessageDialog(this, "Por favor, elija una clase");
        }
        
    }//GEN-LAST:event_jButton1ActionPerformed

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
                con.conectar();
                modelo = (DefaultTableModel)jTable1.getModel();
                modelo.setRowCount(0);
                jTable1.setModel(modelo);

                sent = "select Nombre, NumeroCuenta, Asistencias, Inasistencias, Excusas from vResPer "
                        + "where AsignaturaID = '"+tempsecc.getId_asig()+"' and SeccionID = '"
                        +tempsecc.getNumseccion()+"'";
                con.seleccionar_jtable(sent, jTable1);
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
            java.util.logging.Logger.getLogger(ResumenPeriodo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ResumenPeriodo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ResumenPeriodo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ResumenPeriodo.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new ResumenPeriodo().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
