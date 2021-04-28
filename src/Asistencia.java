
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arnold
 */
public class Asistencia extends javax.swing.JInternalFrame {
   catedratico sesion;
   ArrayList <asignatura> asigns;
   ArrayList <seccion> seccs;
   asignatura tempasign;
   seccion tempsecc;
   libreria_sql.Libreria_sql con;
   String temporal_string, sent;
   ResultSet regreso;
   LocalDateTime locaDate;
   LocalDate fecha_hoy, fecha_general, inicio, fin;
   int hours, minutes, seconds, hora_i,hora_f,asistieron;
   Estudiante tempestud;
   ArrayList<Estudiante> estuds;
   DateTimeFormatter formato;
   asuntosDeTiempo verificar;
   Webcam webcam;
   Login iniciador;
   Principal iniciado;
   confirmacion_instruccion confinst;
   confirmacion_exitosa confexito;
   confirmacion_error conferror;
   boolean claseactiva, encontrado, encontrado2;
    /**
     * Creates new form Asistencia
     */
    public Asistencia(catedratico sesion, Login iniciador, Principal iniciado) {
        initComponents();
        this.iniciador = iniciador;
        this.iniciado = iniciado;
        verificar = new asuntosDeTiempo();
        fecha_hoy = LocalDate.now();
        this.sesion = sesion;
        con = new libreria_sql.Libreria_sql();
        this.asigns = new ArrayList();
        this.seccs = new ArrayList();
        jTextField7.setText(sesion.getNombre());
        locaDate = LocalDateTime.now();
        jButton1.setEnabled(false);
        jTextField1.setEditable(false);
        asistieron=0;
        formato= DateTimeFormatter.ofPattern("uuuu-MM-dd")
                .withResolverStyle(ResolverStyle.STRICT);
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
    
    public void añadir_estuds(){
        estuds = new ArrayList();
        con.conectar();
        sent = "select NumeroCuenta, Nombre from vAlumnosSeccion where AsignaturaID = '"
                +tempasign.getCodigo_asig()+"' and SeccionID = '"+tempsecc.getNumseccion()+"'";
        regreso = con.seleccionar(sent);
        try {
            while(regreso.next()){
                tempestud = new Estudiante();
                tempestud.setCuenta_es(regreso.getString("NumeroCuenta"));
                tempestud.setNombre_es(regreso.getString("Nombre"));
                estuds.add(tempestud);
            }
            System.out.println("Estudiantes añadidos");
        } catch (SQLException ex) {
            Logger.getLogger(InternoMain.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            con.cerrar();
        }
    }
    
    public boolean verificar_run(LocalDate dia){
        try {
            con.conectar();
            sent = "select * from Asistencia where AsignaturaID = '"+tempasign.getCodigo_asig()+"' "
                    + "and SeccionID = '"+tempsecc.getNumseccion()+"' "
                    + "and Fecha = '"+String.valueOf(dia)+"'";
            regreso = con.seleccionar(sent);
            int contador = 0;

            while (regreso.next()) {
                contador++;
            }
            System.out.println(contador);
            System.out.println(estuds.size());
            return contador == estuds.size();
        } catch (SQLException ex) {
            Logger.getLogger(InternoMain.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }finally{
            con.cerrar();
        }
    }
    
    public void cerrar_asistencia(){
        for(int i=0; i<estuds.size();i++){
            if(estuds.get(i).getAsis_hoy().equals("-")){
                estuds.get(i).setAsis_hoy("N");
                estuds.get(i).setFaltas(estuds.get(i).getFaltas()+1);
            }
        }
        con.conectar();
        for(Estudiante estud : estuds){
            sent = "insert into Asistencia values ('"
                    +estud.getCuenta_es()+"','"
                    +tempsecc.getNumseccion()+"','"
                    +tempsecc.getId_asig()+"','"
                    +estud.getAsis_hoy()+"','"
                    +String.valueOf(LocalDate.now())+"')";
            con.insertar(sent);
        }
        con.cerrar();
    }
    
    public void initWebcam() {
        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());

        WebcamPanel panel = new WebcamPanel(webcam);
        //panel = new WebcamPanel(webcam);
        panel.setFPSDisplayed(false);
        panel.setDisplayDebugInfo(false);
        panel.setImageSizeDisplayed(false);
        //panel.setMirrored(true);
        panel.setSize(webcam.getViewSize());
        this.add(panel);
        panel.setBounds(0, 0, 320, 240);
        jPanel4.setSize(panel.getSize());
        this.jPanel4.add(panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 320, 240));
        panel.setVisible(true);
        //Thread.sleep(1000);
        leerqr(webcam);
    }
    
    public void leerqr(Webcam webcam){
        Runnable runnable = () -> {
            do{
                try {
                    Thread.sleep(300);
                    //System.out.println("ola");
                    Result result = null;
                    BufferedImage image = null;
                    image = webcam.getImage();
                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    try {
                        result = new MultiFormatReader().decode(bitmap);
                    } catch (NotFoundException e) {
                        //No result...
                    }
                    if (result != null) {
                        encontrado=false;
                        for(int i=0; i<estuds.size();i++){
                            if(result.getText().equals(estuds.get(i).getCuenta_es())){
                                //AQUÍ SE DEBERÍA MOSTRAR CONFIRMACION_EXITOSA
                                if(estuds.get(i).getAsis_hoy().equals("-")){
                                    asistieron++;
                                    System.out.println("Ingresado");
                                }
                                estuds.get(i).setAsis_hoy("S");
                                jLabel4.setText(asistieron + " de " +estuds.size()+ " estudiantes presentes");
                                encontrado=true;
                                confexito = new confirmacion_exitosa();
                                confexito.Nombre(estuds.get(i).getNombre_es());
                                confexito.setVisible(true);
                                Thread.sleep(1500);
                                confexito.setVisible(false);
                                confexito.dispose();
                            }
                        }
                        if(encontrado=false){
                            System.out.println(result.getText()+": Estudiante no registrado");
                            conferror = new confirmacion_error();
                            conferror.setVisible(true);
                            Thread.sleep(1500);
                            conferror.setVisible(false);
                            conferror.dispose();
                        }
                    }
                    if(hours==hora_f || asistieron == estuds.size()){
                        jButton4.doClick();
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Asistencia.class.getName()).log(Level.SEVERE, null, ex);
                    //No result...
                } catch (NullPointerException e){
                    System.out.println("nullpointerexception pero probablemente no sea grave.");
                }
            }while(claseactiva == true);
        };
        Thread hilo = new Thread(runnable);
        hilo.start();
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
        jLabel3 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(65, 105, 225));
        jLabel3.setText("Inicio de clases");

        jLabel19.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(65, 105, 225));
        jLabel19.setText("Fecha final");

        jTextField2.setEditable(false);
        jTextField2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N

        jTextField12.setEditable(false);
        jTextField12.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(65, 105, 225));
        jLabel5.setText("Asignatura: ");

        jLabel20.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(65, 105, 225));
        jLabel20.setText("(aaaa-mm-dd)");

        jLabel21.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(65, 105, 225));
        jLabel21.setText("(aaaa-mm-dd)");

        jLabel10.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(65, 105, 225));
        jLabel10.setText("Información de la clase");

        jLabel6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(65, 105, 225));
        jLabel6.setText("Sección: ");

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(65, 105, 225));
        jButton3.setText("Cerrar sesión");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTextField3.setEditable(false);
        jTextField3.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jComboBox1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(65, 105, 225));
        jLabel15.setText("Mis clases");

        jLabel7.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(65, 105, 225));
        jLabel7.setText("Hora inicial");

        jTextField4.setEditable(false);
        jTextField4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(65, 105, 225));
        jLabel16.setText("Hora final");

        jLabel12.setFont(new java.awt.Font("Leelawadee UI Semilight", 1, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(65, 105, 225));
        jLabel12.setText("Información del catedrático");

        jLabel17.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(65, 105, 225));
        jLabel17.setText("Días de Clase");

        jTextField5.setEditable(false);
        jTextField5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N

        jTextField9.setEditable(false);
        jTextField9.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N

        jTextField10.setEditable(false);
        jTextField10.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N

        jLabel9.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(65, 105, 225));
        jLabel9.setText("Catedrático: ");

        jTextField7.setEditable(false);
        jTextField7.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(65, 105, 225));
        jLabel2.setText("Código:");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(250, 250, 250));
        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230)));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel1.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 320, 250));

        jLabel4.setText(" ");

        jButton5.setBackground(new java.awt.Color(255, 255, 255));
        jButton5.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(65, 105, 225));
        jButton5.setText("Iniciar clase");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(65, 105, 225));
        jLabel8.setText("Nombre:");

        jLabel11.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 16)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(65, 105, 225));
        jLabel11.setText("Ingreso Manual de Asistencia");

        jTextField6.setEditable(false);
        jTextField6.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(65, 105, 225));
        jButton4.setText("Finalizar Clase");
        jButton4.setToolTipText("");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTextField1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 11)); // NOI18N
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Leelawadee UI Semilight", 0, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(65, 105, 225));
        jButton1.setText("Registrar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jSeparator2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(28, 28, 28)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jTextField2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel20))
                            .addComponent(jTextField3)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel16))
                                .addGap(37, 37, 37)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField9, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                                    .addComponent(jTextField5)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel17))
                                .addGap(21, 21, 21)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel21)
                                        .addContainerGap(35, Short.MAX_VALUE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jTextField10)
                                        .addGap(100, 100, 100))))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(75, 75, 75)
                                .addComponent(jLabel15))
                            .addComponent(jLabel10)
                            .addComponent(jButton3)
                            .addComponent(jLabel12))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(134, 134, 134))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton5)
                                .addGap(46, 46, 46)
                                .addComponent(jButton4)
                                .addGap(355, 355, 355))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButton1)
                                .addGap(150, 150, 150))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel11)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jLabel2)
                                                .addComponent(jLabel8))
                                            .addGap(56, 56, 56)
                                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                                                .addComponent(jTextField1)))))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(95, 95, 95))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(jLabel10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19)
                        .addComponent(jLabel20)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton5))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        iniciador.cerrarsesion();
        iniciado.dispose();
        iniciador.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED){
            tempasign = new asignatura();
            tempsecc = new seccion(); 
            temporal_string = String.valueOf(jComboBox1.getSelectedItem());
            if(temporal_string.equals("Elija una Clase...")){
                jTextField2.setText("");
                jTextField3.setText("");
                jTextField4.setText("");
                jTextField5.setText("");
                jTextField9.setText("");
                jTextField10.setText("");
                jTextField12.setText("");
                jButton5.setEnabled(false);
            }
            else{
                jTextField3.setText(temporal_string.substring(6).trim());
                jTextField4.setText(temporal_string.substring(0, 4).trim());
                for(asignatura asign : asigns){
                    if(jTextField3.getText().equals(asign.getNombre_asig())){
                        tempasign = asign;
                        break;
                    }
                }
                temporal_string = tempasign.getCodigo_asig();
                for(seccion secc : seccs){
                    if(secc.getNumseccion().equals(jTextField4.getText()) && temporal_string.equals(secc.getId_asig())){
                        tempsecc = secc;
                        break;
                    }
                }
                jTextField5.setText(String.valueOf(tempsecc.getHi()));
                jTextField9.setText(String.valueOf(tempsecc.getHf()));
                jTextField10.setText(tempsecc.getDias());
                jTextField2.setText(String.valueOf(tempsecc.getFecha_i()));
                jTextField12.setText(String.valueOf(tempsecc.getFecha_f()));
                jButton5.setEnabled(true);
            }
        }
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        estuds = new ArrayList();
        locaDate = LocalDateTime.now();
        inicio = LocalDate.parse(jTextField2.getText(), formato);
        fin = LocalDate.parse(jTextField12.getText(), formato);
        hours = locaDate.getHour();
        hora_i = Integer.parseInt(jTextField5.getText());
        hora_f = Integer.parseInt(jTextField9.getText());
        añadir_estuds();
        if(String.valueOf(jComboBox1.getSelectedItem()).equals("Elija una Clase...")){
            JOptionPane.showMessageDialog(this, "Por favor seleccione una clase");
        }else if(fecha_hoy.isBefore(inicio)){
            JOptionPane.showMessageDialog(this, "Período de clase no iniciado");
        }else if(fecha_hoy.isAfter(fin)){
            JOptionPane.showMessageDialog(this, "Período de clase finalizado");
        }else if(hours != hora_i){
            JOptionPane.showMessageDialog(this, "Aún no es hora de la clase");
        }else if(verificar.siToca(tempsecc.getDias()) == false){
            JOptionPane.showMessageDialog(this, "Esta clase no se imparte hoy");
        }else if(verificar_run(LocalDate.now()) == true){
            JOptionPane.showMessageDialog(this, "Esta clase ya se impartió hoy");
        }
        else{
            jButton4.setEnabled(true);
            jButton3.setEnabled(false);
            jButton5.setEnabled(false);
            jComboBox1.setEnabled(false);
            System.out.println("Listo para empezar");
            //jLabel4.setText(asistieron + " de " +estuds.size()+ " estudiantes presentes");
            claseactiva = true;
            jLabel4.setText(asistieron + " de " +estuds.size()+ " estudiantes presentes");
            initWebcam();
            jButton1.setEnabled(true);
            jTextField1.setEditable(true);
            confinst = new confirmacion_instruccion();
            confinst.reloj();
            confinst.setVisible(true);
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        claseactiva = false;
        confinst.dispose();
        webcam.close();
        cerrar_asistencia();
        jLabel4.setText(jLabel4.getText()+" - Clase Finalizada");
        jLabel1.setText("");
        jButton3.setEnabled(true);
        jButton5.setEnabled(true);
        jComboBox1.setEnabled(true);
        jButton4.setEnabled(false);
        jButton1.setEnabled(false);
        jTextField1.setText("");
        jTextField6.setText("");
        jTextField1.setEditable(false);
        JOptionPane.showMessageDialog(null, "Clase finalizada.");
        asistieron=0;
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        encontrado2=false;
        for(int i=0; i<estuds.size();i++){
            if(jTextField1.getText().equals(estuds.get(i).getCuenta_es())){
                encontrado2 = true;
                if(estuds.get(i).getAsis_hoy().equals("-")){
                    asistieron++;
                    System.out.println("Ingresado");
                }
                jTextField6.setText(estuds.get(i).getNombre_es());
                estuds.get(i).setAsis_hoy("S");
                jLabel4.setText(asistieron + " de " +estuds.size()+ " estudiantes presentes");
                jLabel1.setText(estuds.get(i).getCuenta_es()+" Registrado correctamente.");
            }
        }
        if(encontrado2 == false){
            jLabel1.setText("Error: No se encontró al estudiante solicitado.");
            jTextField6.setText("");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
