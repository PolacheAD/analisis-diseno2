
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
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
public class InfDiario extends javax.swing.JFrame {
    
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
    int asistencia;
    int inasistencia;
    
    /**
     * Creates new form NewJFrame
     */
    public InfDiario() {
        initComponents();
        hoy = LocalDate.now();
        jLabel7.setText("Fecha: " + hoy.toString());
        jTextField2.setText(hoy.toString());
        modelo = (DefaultTableModel) jTable1.getModel();
        contarN();
    }
    
    public void contarN() {
        inasistencia = 0;
        for(int i=0; i<modelo.getRowCount();i++){
                if ( String.valueOf(jTable1.getModel().getValueAt(i, 2)).equals("N") ) {
                    inasistencia = inasistencia + 1;
                }
            }
        jTextField3.setText(String.valueOf(inasistencia));
        
        inasistencia = 0;
    }
    
    public XSSFWorkbook crear_libro(){
        hoy = LocalDate.now();
        
        //plantilla del archivo
        abrir = new File("C:\\Plantillas\\ResDiario.xlsx");
        try (FileInputStream entrada = new FileInputStream(abrir)){
            libro= new XSSFWorkbook(entrada);
            sheet = libro.getSheetAt(0);
            //Estilo de celda
            style = libro.createCellStyle();
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            
            //Escribiendo el contenido de la tabla en el documento
            
            fila = sheet.getRow(43);
            celda = fila.getCell(1);
            celda.setCellValue("Fecha: " + hoy.toString());
            
            fila = sheet.getRow(7);
            celda = fila.getCell(0);
            celda.setCellValue(String.valueOf(jComboBox1.getSelectedItem()));
            celda = fila.getCell(3);
            celda.setCellValue("Fecha: " + hoy.toString());
            
            fila = sheet.getRow(41);
            celda = fila.getCell(0);
            celda.setCellValue(String.valueOf(jComboBox1.getSelectedItem()));
            celda = fila.getCell(3);
            celda.setCellValue("Fecha: " + hoy.toString());
            
            
            //Obtener Asistencias e Inasistencias
            asistencia = 0;
            inasistencia = 0;
            
            for(int i=0; i<modelo.getRowCount();i++){
                if ( String.valueOf(jTable1.getModel().getValueAt(i, 2)).equals("S") ) {
                    asistencia = asistencia + 1;
                } else if ( String.valueOf(jTable1.getModel().getValueAt(i, 2)).equals("N") ) {
                    inasistencia = inasistencia + 1;
                }
            }
            
            fila = sheet.getRow(10); 
            celda = fila.getCell(3);
            celda.setCellValue(Double.parseDouble(String.valueOf(asistencia)));
            
            fila = sheet.getRow(11);
            celda = fila.getCell(3);
            celda.setCellValue(Double.parseDouble(String.valueOf(inasistencia)));
            
            //Modificando la tabla de Inasistentes
            int cont = 0;
            for(int i=0; i<modelo.getRowCount();i++){
                filainicial = 46; //Primera fila de registros de la tabla de excel
                if ( String.valueOf(jTable1.getModel().getValueAt(i, 2)).equals("N") ) {
                    fila = sheet.createRow(cont+filainicial);
                    //Aplicando estilo a celdas
                    for(int j=1; j<5; j++){
                        celda = fila.createCell(j);
                        celda.setCellStyle(style);
                    }
                    //Celda No.
                    celda = fila.getCell(1);
                    celda.setCellValue(i+1);

                    //Celda Nombre de Alumno
                    celda = fila.getCell(2);
                    //Combinar celdas
                    sheet.addMergedRegion(new CellRangeAddress(cont+filainicial,cont+filainicial,2,3)); 
                    //(primerafila, ultimafila, primeracolumna, ultimacolumna)
               
                    celda.setCellValue(String.valueOf(modelo.getValueAt(i, 0)));
                    
                     //Celda NumeroCuenta (IMPORTANTE: Considerar las columnas que abarca la celda combinada anterior)
                    celda = fila.getCell(4);
                    sheet.addMergedRegion(new CellRangeAddress(cont+filainicial,cont+filainicial,5,6));
                    celda.setCellValue(String.valueOf(modelo.getValueAt(i, 1)));
                    cont++;
                }
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
        jTextField3 = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        Boton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(81, 152, 224));

        jTextField3.setEditable(false);
        jTextField3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jTextField3.setText("0");

        jLabel6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(81, 152, 224));
        jLabel6.setText("U.V.");

        jLabel1.setFont(new java.awt.Font("Leelawadee UI Semilight", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(81, 152, 224));
        jLabel1.setText("Listado/Resumen diario");

        jTextField4.setEditable(false);
        jTextField4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jTextField4.setText("3");

        jLabel2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(81, 152, 224));
        jLabel2.setText("Código: ");

        jLabel3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(81, 152, 224));
        jLabel3.setText("Clase:");

        jLabel7.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(81, 152, 224));
        jLabel7.setText("Fecha: 13/03/2021");

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jTextField1.setText("IS-2000");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jComboBox1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Instalaciones Electricas", "Analisis y Diseno" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(81, 152, 224));
        jLabel5.setText("Inasistencias");

        jTable1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Abigail Sanchez", "20201235495", "S"},
                {"Arnold  Polanco", "20175123689", "S"},
                {"Astrid", "20206598312", "N"},
                {"Bayron", "20018920235", "S"},
                {"Brenedin", "20162920489", "S"},
                {"Brenedin Gomez", "20156891155", "S"},
                {"Elena", "20146569985", "S"},
                {"Fernando", "20178458465", "S"},
                {"Fredy", "20192038668", "S"},
                {"Jose", "20171556987", "S"},
                {"Juan Perez", "20146659856", "S"},
                {"Paola Garcia", "20119956536", "S"},
                {"Pedro Cruz", "20186655653", "S"},
                {"Ramon", "20182937155", "S"},
                {"Roberto", "20195511302", "S"},
                {"Santiago", "20172016556", "N"},
                {"Selvin", "20156689318", "N"},
                {"Sergio Vasquez", "20179963326", "S"},
                {"Teresa", "20192020223", "S"},
                {"Yorleny Ramos", "20148896632", "S"}
            },
            new String [] {
                "Estudiante", "No. Cuenta", "Asistencia"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        jTable1.setRowHeight(22);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(352);
            jTable1.getColumnModel().getColumn(1).setResizable(false);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(250);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
        }

        jTextField2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(81, 152, 224));
        jLabel4.setText("Ingrese fecha:");

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(81, 152, 224));
        jButton2.setText("Buscar");

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(81, 152, 224));
        jButton3.setText("Clase anterior");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(81, 152, 224));
        jButton4.setText("Clase siguiente");

        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen/logo3.png"))); // NOI18N

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen/icons8-macos-close-32.png"))); // NOI18N

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagen/icons8-macos-minimize-32.png"))); // NOI18N

        Boton1.setBackground(new java.awt.Color(255, 255, 255));
        Boton1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 18)); // NOI18N
        Boton1.setForeground(new java.awt.Color(81, 152, 224));
        Boton1.setText("Generar Resumen");
        Boton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8)
                                .addGap(50, 50, 50))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel4))
                                        .addGap(81, 81, 81)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(41, 41, 41))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jButton3)
                                        .addGap(61, 61, 61)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton4)
                                    .addComponent(jButton2))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 931, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSeparator1))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(280, 280, 280)
                        .addComponent(jLabel5)
                        .addGap(19, 19, 19)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(119, 119, 119)
                        .addComponent(jLabel6)
                        .addGap(19, 19, 19)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(414, 414, 414)
                        .addComponent(Boton1)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel1)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel7)
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jButton2))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(Boton1)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void Boton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton1ActionPerformed
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
    }//GEN-LAST:event_Boton1ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged

    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
        hoy = LocalDate.now();
        jTextField2.setText(hoy.toString());
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

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
            java.util.logging.Logger.getLogger(InfDiario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InfDiario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InfDiario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InfDiario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InfDiario().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Boton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}
