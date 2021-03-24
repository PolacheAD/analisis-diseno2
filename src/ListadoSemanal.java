
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
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
    int diasemana;
    File abrir;
    JFileChooser file;
    DefaultTableModel modelo;
    int filainicial;
    Date hoy;
    Calendar calendario;
    /**
     * Creates new form ListadoSemanal
     */
    public ListadoSemanal() {
        initComponents();
        
        hoy = Date.valueOf(LocalDate.now());
        jDateChooser1.setDateFormatString("yyyy-MM-dd");
        jDateChooser1.setDate(hoy);
        jPanel2.setVisible(false);
        
        
    }
    public void defsemana(){
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
    }
    
    public void asigStyle(XSSFCell col, XSSFCellStyle si, XSSFCellStyle no, XSSFCellStyle cross){
        if(col.getStringCellValue().equals("S")){
            col.setCellStyle(si);
        }
        if(col.getStringCellValue().equals("N")){
            col.setCellStyle(no);
        }
        if(col.getStringCellValue().equals("X")){
            col.setCellStyle(cross);
        }
    }
    
    public XSSFWorkbook crear_libro(){
        defsemana();
        //plantilla del archivo
        abrir = new File("C:\\Plantillas\\AsistenciaAsigSemana.xlsx");
        try (FileInputStream entrada = new FileInputStream(abrir)){
            libro= new XSSFWorkbook(entrada);
            verde = new XSSFColor(new java.awt.Color(164,218,179),null);
            rojo = new XSSFColor(new java.awt.Color(255,128,128),null);
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
            celda = fila.getCell(1);
            celda.setCellValue("Catedrático: Brénedin Enrique Núñez");
            
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
                celda.setCellValue(String.valueOf(modelo.getValueAt(i, 2)));
                asigStyle(celda, styleS, styleN, styleX);
                
                //Ma
                celda = fila.getCell(3);
                celda.setCellValue(String.valueOf(modelo.getValueAt(i, 3)));
                asigStyle(celda, styleS, styleN, styleX);
                
                //Mi
                celda = fila.getCell(4);
                celda.setCellValue(String.valueOf(modelo.getValueAt(i, 4)));
                asigStyle(celda, styleS, styleN, styleX);
                
                //Ju
                celda = fila.getCell(5);
                celda.setCellValue(String.valueOf(modelo.getValueAt(i, 5)));
                asigStyle(celda, styleS, styleN, styleX);
                
                //Vi
                celda = fila.getCell(6);
                celda.setCellValue(String.valueOf(modelo.getValueAt(i, 6)));
                asigStyle(celda, styleS, styleN, styleX);
                
                //Sa
                celda = fila.getCell(7);
                celda.setCellValue(String.valueOf(modelo.getValueAt(i, 7)));
                asigStyle(celda, styleS, styleN, styleX);
                
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

        jLabel1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(81, 152, 224));
        jLabel1.setText("Listado Semanal de Asistencia de la Clase");

        jButton3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(81, 152, 224));
        jButton3.setText("Generar Reporte");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(81, 152, 224));
        jLabel6.setText("Seleccione una fecha dentro de la semana que desea consultar:");

        jLabel2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(81, 152, 224));
        jLabel2.setText("(Año-Mes-Día)");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Instalaciones Electricas para IS-2000", " " }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(81, 152, 224));
        jLabel3.setText("Clase:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(28, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel6))
                .addGap(24, 24, 24))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(144, 144, 144)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jLabel2))
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addComponent(jLabel1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel1)
                .addGap(52, 52, 52)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41)
                .addComponent(jButton3)
                .addGap(27, 27, 27))
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
                System.out.println("Error");
            }
        }
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

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
                new ListadoSemanal().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
