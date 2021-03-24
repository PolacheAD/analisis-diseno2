/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.JFileChooser;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import org.apache.poi.ss.usermodel.BorderStyle;
import javax.swing.table.DefaultTableModel;
        
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
/**
 *
 * @author Arnold
 */

public class Informe extends javax.swing.JInternalFrame {
    DefaultTableModel model;
    String temp, temp2, sent;
    libreria_sql.Libreria_sql con;
    ResultSet resul;
    catedratico sesion;
    InternoMain asistencia;
    LocalDate hoy, fecha_lunes, fecha_sabado;
    LocalDateTime hoy_hora;
    asuntosDeTiempo verificar;
    seccion tempsecc;
    FileNameExtensionFilter filtro;
    asistenteCorreo.AsistenteCorreo enviar;
    /**
     * Creates new form Informe
     * @param sesion
     * @param asistencia
     */
    public Informe(catedratico sesion, InternoMain asistencia) {
        initComponents();
        Boton1.setEnabled(false);
        jButton1.setEnabled(false);
        model = (DefaultTableModel)jTable1.getModel();
        this.sesion = sesion;
        this.asistencia = asistencia;
        con = new libreria_sql.Libreria_sql();
        hoy = LocalDate.now();
        hoy_hora = LocalDateTime.now();
        verificar = new asuntosDeTiempo();
        fecha_lunes = hoy.minusDays(hoy.getDayOfWeek().getValue()-1);
        fecha_sabado = fecha_lunes.plusDays(5);
        filtro = new FileNameExtensionFilter("Hoja de Cálculo de Excel 2007 (.xlsx)", "xlsx");
        jLabel7.setText("(Semana del Lunes "+fecha_lunes+" al Sábado "+fecha_sabado+")");
        enviar = new asistenteCorreo.AsistenteCorreo();
    }
    
    public void llenar_combo(){
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Elija una clase...");
        for(seccion secc : asistencia.seccs){
            for(asignatura asig : asistencia.asigns){
                if(secc.getId_catedratico() == sesion.getId_cated() && secc.getId_asig().equals(asig.getCodigo_asig())){
                    jComboBox1.addItem(secc.getNumseccion()+" - "+asig.getNombre_asig());
                    break;
                }
            }
        }
    }
    public void set_faltasCat(String secc){
        int contador_faltas = 0;
        String [] valores;
        valores = secc.split("-");
        for(seccion sc : asistencia.seccs){
            if(sc.getId_asig().equals(valores[0]) && sc.getNumseccion().equals(valores[1])){
                tempsecc = sc;
                break;
            }
        }
        for(int i = 2; i<model.getColumnCount(); i++){
            if(model.getColumnName(i).equals(verificar.dia_letra(hoy.getDayOfWeek().getValue()))){
                break;
            }
            if(String.valueOf(model.getValueAt(0, i)).equals("-") 
                    && verificar.siToca_faltas(tempsecc.getDias(), model.getColumnName(i))){
                contador_faltas++;
            }    
        }
        if(hoy_hora.getHour() > tempsecc.getHi() && hoy.getDayOfWeek().getValue() != 7){
            contador_faltas++;
        }
        tempsecc.setFaltas_cated(contador_faltas);
        System.out.println(contador_faltas);
        System.out.println(verificar.dia_letra(hoy.getDayOfWeek().getValue()));
        con.conectar();
        sent = "update seccion set faltas_cated = "+tempsecc.getFaltas_cated() 
                +" where seccion = '"+tempsecc.getNumseccion()+"' "
                + "and id_asig = '"+tempsecc.getId_asig()+"'";
        con.insertar(sent);
        con.cerrar();
    }
    
    public void llenar_tabla(String secc){
        model.setRowCount(0);
        con.conectar();
        sent = "select "
                + "nombre_alumno,"
                + "cuenta,"
                + "L,Ma,Mi,J,V,S,"
                + "faltas"
                + " from ["+secc+"]";
        con.seleccionar_jtable(sent, jTable1);
        model = (DefaultTableModel)jTable1.getModel();
        con.cerrar();
    }
    public XSSFWorkbook crear_libro(){
        String hoja="Hoja1";
            XSSFWorkbook libro= new XSSFWorkbook();
            XSSFSheet hoja1 = libro.createSheet(hoja); 
            String [] header= new String[]{"Estudiante","N° Cuenta", "L","Ma","Mi","J","V","S","Faltas"};
            /*String [][] document= new String [][]{
                {"Aaaa","12345567","Si","Si","No","Si","Si"},
                {"Bbbb","12345684","Si","No","Si","Si","No"}
            };*/
            CellStyle style = libro.createCellStyle();
            CellStyle cuerpo = libro.createCellStyle();
            CellStyle cuerpo_si = libro.createCellStyle();
            CellStyle cuerpo_no = libro.createCellStyle();
            CellStyle cabecera = style;
            
            Font font = libro.createFont();
            font.setBold(true);
            font.setFontHeightInPoints((short)13);
            style.setFont(font);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            
            cuerpo.setBorderTop(BorderStyle.THIN);
            cuerpo.setBorderBottom(BorderStyle.THIN);
            cuerpo.setBorderLeft(BorderStyle.THIN);
            cuerpo.setBorderRight(BorderStyle.THIN);
            
            cuerpo_si.setBorderTop(BorderStyle.THIN);
            cuerpo_si.setBorderBottom(BorderStyle.THIN);
            cuerpo_si.setBorderLeft(BorderStyle.THIN);
            cuerpo_si.setBorderRight(BorderStyle.THIN);
            
            cuerpo_no.setBorderTop(BorderStyle.THIN);
            cuerpo_no.setBorderBottom(BorderStyle.THIN);
            cuerpo_no.setBorderLeft(BorderStyle.THIN);
            cuerpo_no.setBorderRight(BorderStyle.THIN);
            
            style.setFillForegroundColor(IndexedColors.GOLD.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cuerpo_si.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            cuerpo_si.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cuerpo_no.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cuerpo_no.setFillForegroundColor(IndexedColors.CORAL.getIndex());
            
            cabecera.setAlignment(HorizontalAlignment.CENTER);
            cabecera.setBorderTop(BorderStyle.THIN);
            cabecera.setBorderBottom(BorderStyle.THIN);
            cabecera.setBorderLeft(BorderStyle.THIN);
            cabecera.setBorderRight(BorderStyle.THIN);
            for (int i = 0; i <= model.getRowCount()+2; i++) {  //aqui iba document.length()
                XSSFRow row=hoja1.createRow(i);
                if(i==0){
                   XSSFCell cabecera1 = row.createCell(0);
                   cabecera1.setCellValue("Asistencia "+ jLabel7.getText());
                   cabecera1.setCellStyle(cabecera);
                   hoja1.addMergedRegion(new CellRangeAddress(0,0,0,8));
                   continue;
                }
                if(i==1){
                   XSSFCell cabecera2 = row.createCell(0);
                   cabecera2.setCellValue(jTextField2.getText()+"-"+temp2.substring(0, 4).trim());
                   cabecera2.setCellStyle(cabecera);
                   hoja1.addMergedRegion(new CellRangeAddress(1,1,0,8));
                   continue;
                }
                for (int j = 0; j < header.length; j++) {
                    if (i==2) {//para la cabecera
                        XSSFCell cell= row.createCell(j);//se crea las celdas para la cabecera, junto con la posición
                        cell.setCellStyle(style); // se añade el style crea anteriormente 
                        cell.setCellValue(header[j]);//se añade el contenido
                    }
                    else{
                        XSSFCell cell= row.createCell(j);//se crea las celdas para la contenido, junto con la posición
                        temp = String.valueOf(model.getValueAt(i-3, j));
                        /*if(document[i-1][j].equals("No")){
                            cell.setCellStyle(cuerpo_no);
                        }else if(document[i-1][j].equals("Si")){
                            cell.setCellStyle(cuerpo_si);
                        }else{
                            cell.setCellStyle(cuerpo);
                        }*/
                        if(temp.equals("N")){
                            cell.setCellStyle(cuerpo_no);
                        }else if(temp.equals("S")){
                            cell.setCellStyle(cuerpo_si);
                        }else{
                            cell.setCellStyle(cuerpo);
                            if(temp.isEmpty() || temp.equals("")){
                                temp = "-";
                            }
                        }
                        //cell.setCellValue(document[i-1][j]); //se añade el contenido
                        cell.setCellValue(temp);   
                    }
                }
            }
            
            XSSFRow row=hoja1.createRow(model.getRowCount()+4);
            XSSFCell cell1= row.createCell(0);
            cell1.setCellValue("Clases no impartidas: ");
            XSSFCell cell2 = row.createCell(1);
            cell2.setCellValue(jTextField3.getText());
            cell1.setCellStyle(cuerpo);
            cell2.setCellStyle(cuerpo);
            hoja1.autoSizeColumn(0);
            hoja1.autoSizeColumn(1);
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
        Boton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setTitle("Informes");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(81, 152, 224));

        jTextField3.setEditable(false);
        jTextField3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jTextField3.setText("0");

        jLabel6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(81, 152, 224));
        jLabel6.setText("U.V.");

        jLabel1.setFont(new java.awt.Font("Leelawadee UI Semilight", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(81, 152, 224));
        jLabel1.setText("Generar Informes");

        jTextField4.setEditable(false);
        jTextField4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jTextField4.setText("5");

        jLabel2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(81, 152, 224));
        jLabel2.setText("Código: ");

        Boton1.setBackground(new java.awt.Color(255, 255, 255));
        Boton1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        Boton1.setForeground(new java.awt.Color(81, 152, 224));
        Boton1.setText("Generar Reporte");
        Boton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Boton1ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(81, 152, 224));
        jLabel3.setText("Clase");

        jLabel7.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(81, 152, 224));
        jLabel7.setText("(Semana del)");

        jLabel4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(81, 152, 224));
        jLabel4.setText("Asignatura:");

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(81, 152, 224));
        jButton1.setText("Enviar a mi Correo");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setEditable(false);
        jTextField1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jTextField2.setEditable(false);
        jTextField2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jComboBox1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(81, 152, 224));
        jLabel5.setText("Clases no Impartidas");

        jTable1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Estudiante", "N° Cuenta", "L", "Ma", "Mi", "J", "V", "S", "Faltas"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(0).setResizable(false);
            jTable1.getColumnModel().getColumn(0).setPreferredWidth(400);
            jTable1.getColumnModel().getColumn(1).setPreferredWidth(200);
            jTable1.getColumnModel().getColumn(2).setResizable(false);
            jTable1.getColumnModel().getColumn(2).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(2).setHeaderValue("L");
            jTable1.getColumnModel().getColumn(3).setResizable(false);
            jTable1.getColumnModel().getColumn(3).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(3).setHeaderValue("Ma");
            jTable1.getColumnModel().getColumn(4).setResizable(false);
            jTable1.getColumnModel().getColumn(4).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(4).setHeaderValue("Mi");
            jTable1.getColumnModel().getColumn(5).setResizable(false);
            jTable1.getColumnModel().getColumn(5).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(5).setHeaderValue("J");
            jTable1.getColumnModel().getColumn(6).setResizable(false);
            jTable1.getColumnModel().getColumn(6).setPreferredWidth(30);
            jTable1.getColumnModel().getColumn(6).setHeaderValue("V");
            jTable1.getColumnModel().getColumn(7).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setResizable(false);
            jTable1.getColumnModel().getColumn(8).setPreferredWidth(60);
            jTable1.getColumnModel().getColumn(8).setHeaderValue("Faltas");
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jLabel7))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(154, 154, 154)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel2))
                                .addGap(33, 33, 33)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jComboBox1, 0, 267, Short.MAX_VALUE)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(19, 19, 19)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(92, 92, 92)
                                .addComponent(jLabel6)
                                .addGap(19, 19, 19)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel1)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                    .addComponent(jSeparator1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Boton1)
                .addGap(119, 119, 119)
                .addComponent(jButton1)
                .addGap(159, 159, 159))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(21, 21, 21)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(Boton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    }// </editor-fold>//GEN-END:initComponents

    private void Boton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Boton1ActionPerformed
        // TODO add your handling code here:
        JFileChooser file = new JFileChooser();
        file.setFileFilter(filtro);
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
        if(evt.getStateChange() == ItemEvent.SELECTED){
            temp2 = String.valueOf(jComboBox1.getSelectedItem());
            if(temp2.equals("Elija una clase...")){
                jTextField1.setText("");
                jTextField2.setText("");
                jTextField3.setText("");
                jTextField4.setText("");
                Boton1.setEnabled(false);
                jButton1.setEnabled(false);
            }else{
                jTextField2.setText(temp2.substring(6).trim());
                for(asignatura asign : asistencia.asigns){
                    if(jTextField2.getText().equals(asign.getNombre_asig())){               
                        jTextField1.setText(asign.getCodigo_asig());
                        jTextField4.setText(String.valueOf(asign.getUv()));
                        break;
                    }
                }
                this.llenar_tabla(jTextField1.getText().trim()+"-"+temp2.substring(0, 4).trim());
                set_faltasCat(jTextField1.getText().trim()+"-"+temp2.substring(0, 4).trim());
                jTextField3.setText(String.valueOf(tempsecc.getFaltas_cated()));
                Boton1.setEnabled(true);
                jButton1.setEnabled(true);
            }
            
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        File xlsx_enviar = new File(String.valueOf(jComboBox1.getSelectedItem())+".xlsx");
        XSSFWorkbook xlsx_local = crear_libro();
        try {
            FileOutputStream escritor= new FileOutputStream(xlsx_enviar);
            xlsx_local.write(escritor);
            escritor.flush();
            escritor.close();
            enviar.enviar_reporte(sesion.getCorreo(), xlsx_enviar);
            JOptionPane.showMessageDialog(this,"Informe enviado con éxito"); 
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Informe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Informe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Boton1;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}
