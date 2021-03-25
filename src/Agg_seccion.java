
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JFileChooser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import asistenteCorreo.AsistenteCorreo;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.time.format.*;
import javax.swing.filechooser.FileNameExtensionFilter;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arnold
 */

public class Agg_seccion extends javax.swing.JInternalFrame {
    libreria_sql.Libreria_sql con;
    ResultSet regreso;
    catedratico sesion;
    seccion temporal_s;
    InternoMain asistencia;
    String temporal_string, temporal_string2, temporal_string3;
    String hoja;
    int temporal_int,temporal_int2, hi,hf;
    depto temporal_depto;
    ArrayList<javax.swing.JCheckBox> botones;
    JFileChooser file;
    FileNameExtensionFilter filtro;
    Iterator<Row> rowIterator;
    Iterator<Cell> cellIterator;
    Row row;
    Cell cell;
    DefaultTableModel model;
    Calendar Cl = new GregorianCalendar();
    LocalDate localDate;
    LocalDate inicio,fin;
    
    DateTimeFormatter formato;
    boolean horas;
    boolean estud;
    asistenteCorreo.AsistenteCorreo enviar;
    
    /**
     * Creates new form Agg_seccion
     * @param sesion
     * @param asistencia
     */
    public Agg_seccion(catedratico sesion, InternoMain asistencia) {
        initComponents();
        model = (DefaultTableModel)jTable1.getModel();
        this.sesion = sesion;
        temporal_s = new seccion();
        this.asistencia = asistencia;
        this.con = new libreria_sql.Libreria_sql();
        botones = new ArrayList();
        botones.add(b1);
        botones.add(b2);
        botones.add(b3);
        botones.add(b4);
        botones.add(b5);
        botones.add(b6);
        Cl.setMinimalDaysInFirstWeek(1);
        localDate = LocalDate.now();
        formato= DateTimeFormatter.ofPattern("dd/MM/uuuu")
                .withResolverStyle(ResolverStyle.STRICT);
        temporal_string = new String();
        estud=false;
        jButton2.setEnabled(false);
        filtro = new FileNameExtensionFilter("Hoja de Cálculo de Excel 2007 (.xlsx)", "xlsx");
        enviar = new asistenteCorreo.AsistenteCorreo();
    }
    public void llenar_combos(){
        jComboBox1.removeAllItems();
        jComboBox2.removeAllItems();
        jComboBox1.addItem("Elija un Departamento...");
        jComboBox2.addItem("Elija una Asignatura...");
        for (depto dpt : asistencia.deptos) {
            this.jComboBox1.addItem(dpt.getNombre_depto());
        }
    }
    public void llenar_dias(){
        temporal_string = new String();
        temporal_int2=0;
        for(JCheckBox b : botones){
            if(b.isSelected()){
                temporal_string += b.getText()+ ",";
                temporal_int2++;
            }
        }
        if(temporal_string.isEmpty() == false){
            temporal_s.setDias(temporal_string.substring(0, temporal_string.length()-1));
        }   
    }
    public boolean comprobarUv(int uv){
        int horatotal = hf-hi;
        if(temporal_int2*horatotal == uv){
            return true;
        }
        return false;
    }
    public String regresar_sección(String secc, String hi){
        try {
            con.conectar();
            int Mayor=0, cont=0;
            temporal_string2 = "SELECT name FROM sqlite_master WHERE "
                    + "type = \"table\" and name like '%"+secc+"-"+hi+"%';";
            regreso = con.seleccionar(temporal_string2);
            while(regreso.next()){
                temporal_int = Integer.parseInt(regreso.getString(1).substring(8, 10));
                if(temporal_int > Mayor){
                    Mayor=temporal_int;
                }
                cont++;
            }
            if(cont>0){
                return hi+"0"+(Mayor+1);
            }
            else{
                return hi+"00";
            }
        } catch (SQLException ex) {
            Logger.getLogger(Agg_seccion.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
        finally{
            con.cerrar();
        }
    }
    public void leer_estud(File abrir){
        hoja = "Hoja1";
        try (FileInputStream entrada = new FileInputStream(abrir)) {
            XSSFWorkbook workbook = new XSSFWorkbook(entrada);
            XSSFSheet sheet = workbook.getSheetAt(0);
            rowIterator =sheet.iterator();
            rowIterator.next();
            while (rowIterator.hasNext()) {
		row = rowIterator.next();
                cellIterator = row.cellIterator();
                if(temporal_int != 1){
                    cell = cellIterator.next();
                    cell = cellIterator.next();
                    temporal_string = String.valueOf(BigDecimal.valueOf(cell.getNumericCellValue()));
                    cell = cellIterator.next();
                    temporal_string2 = cell.getStringCellValue();
                    cell = cellIterator.next();
                    model.addRow(new Object[]{temporal_string,temporal_string2,cell.getStringCellValue()});
                }
            }
            entrada.close();
            estud=true;
            jButton2.setEnabled(true);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Agg_seccion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Agg_seccion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void crear_tablaSec(String secc){
        try {
            con.conectar();
            temporal_string2 = "CREATE TABLE ["+secc+"] (\n" +
"    cuenta        VARCHAR (11) PRIMARY KEY,\n" +
"    nombre_alumno VARCHAR (80),\n" +
"    correo        VARCHAR (50),\n" +
"    L             VARCHAR (1) DEFAULT ('-'),\n" +
"    Ma            VARCHAR (1) DEFAULT ('-'),\n" +
"    Mi            VARCHAR (1) DEFAULT ('-'),\n" +
"    J             VARCHAR (1) DEFAULT ('-'),\n" +
"    V             VARCHAR (1) DEFAULT ('-'),\n" +
"    S             VARCHAR (1) DEFAULT ('-'),\n" +
"    faltas        INTEGER DEFAULT (0)\n" +
");";
            con.insertar(temporal_string2);
        } 
        finally{
            con.cerrar();
        }
    }
    public void llenar_tablaSec(String secc){
        con.conectar();
        for(int i=0; i<model.getRowCount(); i++){
            temporal_string2 = "insert into ["+secc+"] (cuenta, nombre_alumno, correo) values("
                    + "'"+model.getValueAt(i, 0)+"',"
                    + "'"+model.getValueAt(i, 1)+"',"
                    + "'"+model.getValueAt(i, 2)+"')";
            con.insertar(temporal_string2);
        }
        temporal_string2 = "insert into seccion (seccion,id_asig,id_catedratico,hi,hf,fecha_inicio,fecha_fin,dias) "
                + "values('"+temporal_s.getNumseccion()+"',"
                + "'"+temporal_s.getId_asig()+"',"
                + temporal_s.getId_catedratico()+","
                + temporal_s.getHi() +","
                + temporal_s.getHf() +","
                + "'"+temporal_s.getFecha_i()+"',"
                + "'"+temporal_s.getFecha_f()+"',"
                + "'"+temporal_s.getDias()+"')"; 
        con.insertar(temporal_string2);
        con.cerrar();
    }
    public void correos(){
        String temp_correo, temp_cuenta;
        for(int i=0; i<model.getRowCount(); i++){
            temp_cuenta = String.valueOf(model.getValueAt(i, 0));
            temp_correo = String.valueOf(model.getValueAt(i, 2));
            enviar.genQr(temp_cuenta);
            enviar.enviar(temp_correo);
        }
    }
    public void borrar_ing(){
        jComboBox1.setSelectedIndex(0);
        jComboBox2.setSelectedIndex(0);
        jComboBox3.setSelectedIndex(0);
        jComboBox4.setSelectedIndex(0);
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jTextField6.setText("");
        model.setRowCount(0);
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
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        b3 = new javax.swing.JCheckBox();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        b4 = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        b5 = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        b6 = new javax.swing.JCheckBox();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextField7 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        b1 = new javax.swing.JCheckBox();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        b2 = new javax.swing.JCheckBox();
        jTextField1 = new javax.swing.JTextField();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setTitle("Crear clase");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(81, 152, 224));
        jLabel3.setText("Asignatura");

        jTextField2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        b3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b3.setForeground(new java.awt.Color(81, 152, 224));
        b3.setText("Mi");

        jTextField3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(81, 152, 224));
        jLabel4.setText("Hora inicial");

        jLabel10.setForeground(new java.awt.Color(81, 152, 224));
        jLabel10.setText("/");

        b4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b4.setForeground(new java.awt.Color(81, 152, 224));
        b4.setText("Ju");

        jLabel11.setForeground(new java.awt.Color(81, 152, 224));
        jLabel11.setText("/");

        jLabel5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(81, 152, 224));
        jLabel5.setText("Hora final");

        jLabel12.setForeground(new java.awt.Color(81, 152, 224));
        jLabel12.setText("/");

        b5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b5.setForeground(new java.awt.Color(81, 152, 224));
        b5.setText("Vi");

        jLabel13.setForeground(new java.awt.Color(81, 152, 224));
        jLabel13.setText("/");

        jLabel6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(81, 152, 224));
        jLabel6.setText("Lista de estudiantes");

        b6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b6.setForeground(new java.awt.Color(81, 152, 224));
        b6.setText("Sá");
        b6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b6ActionPerformed(evt);
            }
        });

        jTextField4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jTextField5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(81, 152, 224));
        jLabel7.setText("Días de clase");

        jTextField6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(81, 152, 224));
        jButton2.setText("Agregar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(81, 152, 224));
        jLabel14.setText("U.V.");

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(81, 152, 224));
        jButton1.setText("Cargar lista...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField7.setEditable(false);
        jTextField7.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jComboBox1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Elija un Departamento..." }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jComboBox2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Elija una Asignatura..." }));
        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Leelawadee UI Semilight", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(81, 152, 224));
        jLabel1.setText("Crear Clase");

        b1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b1.setForeground(new java.awt.Color(81, 152, 224));
        b1.setText("Lu");
        b1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b1ActionPerformed(evt);
            }
        });

        jComboBox3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" }));

        jComboBox4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21" }));

        jTable1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "No. Cuenta", "Estudiante", "Correo personal"
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
        jScrollPane1.setViewportView(jTable1);

        jLabel8.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(81, 152, 224));
        jLabel8.setText("Fecha inicio (dd/mm/aa)");

        jLabel2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(81, 152, 224));
        jLabel2.setText("Departamento");

        jLabel9.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(81, 152, 224));
        jLabel9.setText("Fecha final (dd/mm/aa)");

        b2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b2.setForeground(new java.awt.Color(81, 152, 224));
        b2.setText("Ma");

        jTextField1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 657, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel6))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel7)
                                                .addGap(42, 42, 42))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGap(0, 326, Short.MAX_VALUE)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabel10)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabel11)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabel12)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jLabel13)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(350, 350, 350))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jButton1)
                                                    .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel2)
                                                    .addComponent(jLabel3))
                                                .addGap(54, 54, 54)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(18, 18, 18)
                                                        .addComponent(jLabel14)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(jLabel8))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(b5)
                                    .addComponent(b3)
                                    .addComponent(b1)
                                    .addComponent(b2)
                                    .addComponent(b4)
                                    .addComponent(b6))
                                .addGap(40, 40, 40))))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(513, 513, 513))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(35, 35, 35)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(b1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(b6)
                        .addGap(28, 28, 28))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7)
                            .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel12)
                                .addComponent(jLabel13))
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jButton2)
                .addContainerGap(63, Short.MAX_VALUE))
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

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        // TODO add your handling code here:
        if(evt.getStateChange() == ItemEvent.SELECTED){
            jComboBox2.removeAllItems();
            jComboBox2.addItem("Elija una Asignatura...");
            if (String.valueOf(jComboBox1.getSelectedItem()).equals("Elija un Departamento...") == false){
                temporal_string = String.valueOf(jComboBox1.getSelectedItem());
                for (depto dpt : asistencia.deptos) {
                    if(temporal_string.equals(dpt.getNombre_depto())){
                        temporal_depto = dpt;
                    }  
                }
                for (asignatura asig : asistencia.asigns){
                    if(asig.getId_depto() == temporal_depto.getId_depto()){
                        this.jComboBox2.addItem(asig.getCodigo_asig() + " - " + asig.getNombre_asig());
                    }
                }
            }
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        temporal_s.setId_catedratico(sesion.getId_cated());
        temporal_s.setId_asig(String.valueOf(jComboBox2.getSelectedItem()).substring(0,5).trim());
        temporal_s.setNumseccion(regresar_sección(temporal_s.getId_asig()
                    ,String.valueOf(jComboBox3.getSelectedItem())));
        llenar_dias(); //No tocar temporal_string
        hi=Integer.parseInt(String.valueOf(jComboBox3.getSelectedItem()));
        hf=Integer.parseInt(String.valueOf(jComboBox4.getSelectedItem()));
        horas = (hi >= hf);
        try{
            inicio = LocalDate.parse(jTextField1.getText()+"/"
                    +jTextField2.getText()+ "/"
                    +jTextField3.getText(), formato);
            fin = LocalDate.parse(jTextField4.getText()+"/"
                    +jTextField5.getText()+ "/"
                    +jTextField6.getText(), formato);
            if(temporal_s.getNumseccion().equals("")){
                JOptionPane.showMessageDialog(this, "Error al asignar sección");
            }
            else if(estud==false){
                JOptionPane.showMessageDialog(this, "Error: sin estudiantes");
            }
            else if(inicio.isAfter(fin) || inicio.isEqual(fin) || inicio.isBefore(localDate)){
                JOptionPane.showMessageDialog(this, "Error: fechas no permitidas");
            }               
            else if(horas){
                JOptionPane.showMessageDialog(this, "Error: horas no permitidas");
            }
            else if(comprobarUv(Integer.parseInt(jTextField7.getText()))==false){
                JOptionPane.showMessageDialog(this, "Error: el horario de la sección difiere de las Unidades Valorativas");
            }
            else{
                temporal_s.setHi(hi);
                temporal_s.setHf(hf);
                temporal_s.setFecha_i(String.valueOf(inicio));
                temporal_s.setFecha_f(String.valueOf(fin));
                crear_tablaSec(temporal_s.getId_asig()+"-"+temporal_s.getNumseccion());
                llenar_tablaSec(temporal_s.getId_asig()+"-"+temporal_s.getNumseccion());
                System.out.println(temporal_s.getId_asig()+"-"+temporal_s.getNumseccion());
                System.out.println("Dias: " + temporal_s.getDias());
                System.out.println(inicio);
                System.out.println(fin); 
                System.out.println("Hora Inicio: "+String.valueOf(jComboBox3.getSelectedItem()));
                System.out.println("Hora Final: "+String.valueOf(jComboBox4.getSelectedItem()));
                correos();
                JOptionPane.showMessageDialog(this, "Sección "+temporal_s.getNumseccion()+" - "
                + jComboBox2.getSelectedItem() + " creada con éxito.");
                borrar_ing();
                estud=false;
                asistencia.addSeccs();
                asistencia.addAsigns();
                asistencia.addDeptos();
                jButton2.setEnabled(false);
            }
            
        } 
        catch(DateTimeException x){
            JOptionPane.showMessageDialog(this, "Por favor revise la coherencia de las fechas ingresadas");
        }
        catch(NullPointerException e){
            JOptionPane.showMessageDialog(this, "Por favor seleccione: días de clase");          
        } 
        catch(NumberFormatException r){
            JOptionPane.showMessageDialog(this, "Error al guardar fechas. Revise e intente de nuevo");
        }
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.file = new JFileChooser();
        file.setFileFilter(filtro);
        file.showSaveDialog(this);
        File abrir = file.getSelectedFile();
        leer_estud(abrir);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED){
            temporal_string = String.valueOf(jComboBox2.getSelectedItem());
            temporal_string = temporal_string.substring(8).trim();
            if(String.valueOf(jComboBox2.getSelectedItem()).equals("Elija una Asignatura...")){
                jTextField7.setText("");
            }
            else{
                for (asignatura asig : asistencia.asigns){
                    if(asig.getNombre_asig().equals(temporal_string)){
                        jTextField7.setText(String.valueOf(asig.getUv()));
                    }
                }
            }
        }
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_b1ActionPerformed

    private void b6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_b6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox b1;
    private javax.swing.JCheckBox b2;
    private javax.swing.JCheckBox b3;
    private javax.swing.JCheckBox b4;
    private javax.swing.JCheckBox b5;
    private javax.swing.JCheckBox b6;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
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
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
