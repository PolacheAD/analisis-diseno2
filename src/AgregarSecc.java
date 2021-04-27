
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
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
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
public class AgregarSecc extends javax.swing.JInternalFrame {
    libreria_sql.Libreria_sql con;
    JFileChooser file;
    LocalDate localDate, inicio, fin;
    ResultSet regreso;
    String hoja;
    String temp_correo, temp_cuenta;
    catedratico sesion;
    Iterator<Row> rowIterator;
    Iterator<Cell> cellIterator;
    ArrayList<String> cuentas;
    Row row;
    Cell cell;
    String temporal_string, temporal_string2;
    int temporal_int, temporal_int2, hi, hf;
    boolean estud, horas;
    DefaultTableModel model;
    seccion temporal_s;
    ArrayList<javax.swing.JCheckBox> botones;
    DateTimeFormatter formato;
    FileNameExtensionFilter filtro;
    asistenteCorreo.AsistenteCorreo enviar;
    /**
     * Creates new form AgregarSecc
     */
    public AgregarSecc(catedratico sesion) {
        initComponents();
        enviar = new asistenteCorreo.AsistenteCorreo();
        model = (DefaultTableModel)jTable1.getModel();
        con = new libreria_sql.Libreria_sql();
        filtro = new FileNameExtensionFilter("Hoja de Cálculo de Excel 2007 (.xlsx)", "xlsx");
        this.sesion = sesion;
        temporal_s = new seccion();
        botones = new ArrayList();
        formato= DateTimeFormatter.ofPattern("dd/MM/uuuu")
                .withResolverStyle(ResolverStyle.STRICT);
        localDate = LocalDate.now();
        botones.add(b1);
        botones.add(b2);
        botones.add(b3);
        botones.add(b4);
        botones.add(b5);
        botones.add(b6);
    }
    public String regresar_seccion(String asig_id, String hi){
        try {
            con.conectar();
            int Mayor=0, cont=0;
            temporal_string2 = "SELECT SeccionID FROM InfoSeccion WHERE "
                    + "AsignaturaID = '"+asig_id+"' and SeccionID like '%"+hi+"%';";
            regreso = con.seleccionar(temporal_string2);
            while(regreso.next()){
                temporal_int = Integer.parseInt(regreso.getString(1).substring(2,4));
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
            Logger.getLogger(AgregarSecc.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
        finally{
            con.cerrar();
        }
    }
    public void llenar_asigns(){
        try {
            jComboBox2.removeAllItems();
            con.conectar();
            temporal_string2 = "select AsignaturaID, Nombre from Asignatura";
            regreso = con.seleccionar(temporal_string2);
            while(regreso.next()){
                jComboBox2.addItem(regreso.getString("AsignaturaID")+" - "
                +regreso.getString("Nombre"));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AgregarSecc.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error en llenar_asigns");
        } finally{
            con.cerrar();
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
    public void leer_estud(File abrir){
        hoja = "Hoja1";
        model.setRowCount(0);
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
                    //System.out.println(cell.getStringCellValue());
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
            Logger.getLogger(AgregarSecc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AgregarSecc.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex){
            System.out.println(ex.getMessage());
        }
    }
    
    public void agregar_seccion(seccion secc){
        con.conectar();
        temporal_string2 = "insert into InfoSeccion values ('"
                +temporal_s.getNumseccion()+"', '"
                +temporal_s.getId_asig()+"', '"
                +temporal_s.getId_catedratico()+"', '"
                +temporal_s.getHi()+"', '"
                +temporal_s.getHf()+"', '"
                +temporal_s.getFecha_i()+"', '"
                +temporal_s.getFecha_f()+"', '"
                +temporal_s.getDias()+"', "
                +0+")";
        
        con.insertar(temporal_string2);
        con.cerrar();
    }
    
    public void llenar_seccion(){
        try {
            con.conectar();
            cuentas = new ArrayList();
            temporal_string2 = "select NumeroCuenta from Alumno";
            regreso = con.seleccionar(temporal_string2);
            while(regreso.next()){
                cuentas.add(regreso.getString("NumeroCuenta"));
            }
            
            for(int i=0; i<model.getRowCount(); i++){
                if(cuentas.contains(String.valueOf(model.getValueAt(i, 0))) == false){
                    temporal_string2 = "insert into Alumno (NumeroCuenta, Nombre, Correo) values("
                        + "'"+model.getValueAt(i, 0)+"',"
                        + "'"+model.getValueAt(i, 1)+"',"
                        + "'"+model.getValueAt(i, 2)+"')";
                    con.insertar(temporal_string2);
                }
            }
            for(int i=0; i<model.getRowCount(); i++){
                temporal_string2 = "insert into seccion values ("
                    + "'"+temporal_s.getNumseccion()+"',"
                    + "'"+temporal_s.getId_asig()+"',"
                    + "'"+model.getValueAt(i,0) + "')";
                con.insertar(temporal_string2);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AgregarSecc.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            con.cerrar();
        }
    }
    
    public void correos(){
        for(int i=0; i<model.getRowCount(); i++){
            temp_cuenta = String.valueOf(model.getValueAt(i, 0));
            temp_correo = String.valueOf(model.getValueAt(i, 2));
            if(cuentas.contains(temp_cuenta) == false){
                enviar.genQr(temp_cuenta);
                enviar.enviar(temp_correo);
            }
        }
    }
    public void borrar_ing(){
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
        jButton1 = new javax.swing.JButton();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        b1 = new javax.swing.JCheckBox();
        jComboBox3 = new javax.swing.JComboBox<>();
        jComboBox4 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        b2 = new javax.swing.JCheckBox();
        jTextField1 = new javax.swing.JTextField();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(65, 105, 225));
        jLabel3.setText("Asignatura");

        jTextField2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        b3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b3.setForeground(new java.awt.Color(65, 105, 225));
        b3.setText("Mi");

        jTextField3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(65, 105, 225));
        jLabel4.setText("Hora inicial");

        jLabel10.setForeground(new java.awt.Color(81, 152, 224));
        jLabel10.setText("/");

        b4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b4.setForeground(new java.awt.Color(65, 105, 225));
        b4.setText("Ju");

        jLabel11.setForeground(new java.awt.Color(81, 152, 224));
        jLabel11.setText("/");

        jLabel5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(65, 105, 225));
        jLabel5.setText("Hora final");

        jLabel12.setForeground(new java.awt.Color(81, 152, 224));
        jLabel12.setText("/");

        b5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b5.setForeground(new java.awt.Color(65, 105, 225));
        b5.setText("Vi");

        jLabel13.setForeground(new java.awt.Color(81, 152, 224));
        jLabel13.setText("/");

        jLabel6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(65, 105, 225));
        jLabel6.setText("Lista de estudiantes");

        b6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b6.setForeground(new java.awt.Color(65, 105, 225));
        b6.setText("Sá");
        b6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b6ActionPerformed(evt);
            }
        });

        jTextField4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jTextField5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jLabel7.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(65, 105, 225));
        jLabel7.setText("Días de clase");

        jTextField6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(65, 105, 225));
        jButton2.setText("Agregar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(65, 105, 225));
        jButton1.setText("Cargar lista...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
        jLabel1.setForeground(new java.awt.Color(65, 105, 225));
        jLabel1.setText("Crear Clase");

        b1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b1.setForeground(new java.awt.Color(65, 105, 225));
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
        jLabel8.setForeground(new java.awt.Color(65, 105, 225));
        jLabel8.setText("Fecha inicio (dd/mm/aa)");

        jLabel9.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(65, 105, 225));
        jLabel9.setText("Fecha final (dd/mm/aa)");

        b2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        b2.setForeground(new java.awt.Color(65, 105, 225));
        b2.setText("Ma");

        jTextField1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(621, 621, 621))
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
                                        .addGap(0, 0, Short.MAX_VALUE)
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
                                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(350, 350, 350))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel12)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel13)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(115, 115, 115))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jButton1)
                                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(76, 76, 76)
                                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(324, 324, 324))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(38, 38, 38)
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
                            .addComponent(jLabel3)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(jButton2)
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
    }// </editor-fold>//GEN-END:initComponents

    private void b6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_b6ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        temporal_s.setId_catedratico(sesion.getCatedraticoid());
        temporal_s.setId_asig(String.valueOf(jComboBox2.getSelectedItem()).substring(0,5).trim());
        temporal_s.setNumseccion(regresar_seccion(temporal_s.getId_asig()
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
            else if(temporal_s.getDias().equals("")){
                JOptionPane.showMessageDialog(this, "Por favor seleccione: días de clase"); 
            }
            else{
                temporal_s.setHi(hi);
                temporal_s.setHf(hf);
                temporal_s.setFecha_i(inicio);
                temporal_s.setFecha_f(fin);
                this.agregar_seccion(temporal_s);
                this.llenar_seccion();
                correos();
                JOptionPane.showMessageDialog(this, "Sección "+temporal_s.getNumseccion()+" - "
                + jComboBox2.getSelectedItem() + " creada con éxito.");
                borrar_ing();
                estud=false;
            }     
        } 
        catch(DateTimeException x){
            JOptionPane.showMessageDialog(this, "Por favor revise la consistencia de las fechas ingresadas");
        }
        catch(NullPointerException e){
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(this, "Por favor seleccione: días de clase");          
        } 
        catch(NumberFormatException r){
            JOptionPane.showMessageDialog(this, "Error al guardar fechas. Revise e intente de nuevo");
        }
        finally{
            temporal_s.setDias("");
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
        
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_b1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox b1;
    private javax.swing.JCheckBox b2;
    private javax.swing.JCheckBox b3;
    private javax.swing.JCheckBox b4;
    private javax.swing.JCheckBox b5;
    private javax.swing.JCheckBox b6;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    // End of variables declaration//GEN-END:variables
}
