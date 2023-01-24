import java.sql.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import javax.crypto.spec.SecretKeySpec;



public class Main {
    Scanner sc = new Scanner(System.in);
    String dff;

    public static Integer getNumber(int uid) {
        //AUTOIncrement
        System.out.print("The UID is: ");
        int a = uid + 1;
        System.out.println(a);
        return a;



    }
    //Hashing the password

    private static final String ALGORITHM = "AES";
    private static final byte[] KEY = "MySuperSecretKey".getBytes(StandardCharsets.UTF_8);
    public static String HashPwd(String pwd) throws Exception{
        //Creating KeyPair generator object
//        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
//        //Generate the pair of keys
//        KeyPair pair = keyPairGen.generateKeyPair();
//        //Getting the public key from the key pair
//        PublicKey publicKey = pair.getPublic();
//        //Creating a Cipher object
//        Cipher cipher = Cipher.getInstance("RSA");
//        //Initializing a Cipher object
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//        //Add data to the cipher
//        byte[] input = pwd.getBytes();
//        cipher.update(input);
//        //encrypting the data
//        byte[] cipherText = cipher.doFinal();
//        System.out.println( new String(cipherText, "UTF8"));
//
//        PrivateKey privateKey = pair.getPrivate();
//        cipher.init(Cipher.DECRYPT_MODE, privateKey);
//        byte[] decipheredText = cipher.doFinal(cipherText);
//        String dff = new String(decipheredText);
//        System.out.println( dff);

        Key key = new SecretKeySpec(KEY, ALGORITHM);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);

        byte[] encValue = c.doFinal(pwd.getBytes());
        byte[] encryptedValue64 = Base64.getEncoder().encode(encValue);
        String sd =  new String(encryptedValue64);
        System.out.println(sd);
        return sd;



    }

    //Decrypting text

    public static String Decrypt(String encrypt) throws Exception{
        Key key = new SecretKeySpec(KEY, ALGORITHM);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedValue64 = Base64.getDecoder().decode(encrypt.getBytes());
        byte[] decValue = c.doFinal(decodedValue64);
        return new String(decValue);

    }


    //Password Validation
    public static String getPassword() throws Exception{
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the Password:");
        String password = sc.next();
        if (password.length() >= 8) {
            int num = 0;
            int spcl = 0;
            int cap = 0;
            int small = 0;

            for (int i = 0; i < password.length(); i++) {
                char f = password.charAt(i);
                if(Character.isDigit(f)){
                    num++;
                }
                else if(Character.isUpperCase(f)){
                    cap++;
                }
                else if(Character.isLowerCase(f)){
                    small++;
                }
                else {
                    spcl++;
                }

            }
            if(num>2&&cap>1&&small>1&&spcl>1){

                String lastpass =  new String(HashPwd(password));
                return lastpass;
            }
            else {
                System.out.println("The password should contain a minimum of 3 numericals, 2 special characters, 2 upper case letters and 2 lower case letters");

                getPassword();
            }
        }
        else {
            System.out.println("The min lenght should be 8");
            getPassword();
        }
        return "";

    }


    public static int getWalletAmount(){
        System.out.println("This is the procedure to add money!!");
        System.out.println("Enter the amount:");
        Scanner sc = new Scanner(System.in);
        int wallet = sc.nextInt();

        return wallet;

    }

    //Username validation

    public static String getUserName(){

        //username should be unique
        getUserName2();
        return "";
    }
    public static String getUserName2(){
        System.out.println("Enter the Username:");
        Scanner sc = new Scanner(System.in);
        String usrname = sc.next();


        int len = usrname.length();
        int dig = 0;

        if(len>=3){
            for(int i=0;i<len;i++) {
                if (Character.isDigit(usrname.charAt(i))) {
                    dig++;
                }
            }

            if(dig>0){
                return usrname;
            }
            else {
                System.out.println("min one digit!!");
                getUserName2();
            }

        }
        else {
            System.out.println("The length should be min 3");
            getUserName2();
        }


        return "";
    }
    //
    public static String getName(){
        System.out.println("Enter your full name:");
        Scanner sc = new Scanner(System.in);
        String name = sc.next();

        return name;
    }
    //Method to get Currency
    static String getCurrency(){


        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Your Choice of Currency: 1:USD, 2:INR, 3:POUND, 4:WON, 5:YUAN");

        int choice = sc.nextInt();
        ArrayList<String> currencies = new ArrayList<String>(
                Arrays.asList("USD", "INR", "POUND", "WON", "YUAN")
        );
        if(choice<1 || choice>5){
            System.out.println("Please enter the correct input!!");
            return getCurrency();
        }

        return currencies.get(choice).toString();

    }
//Currency

    public static String getData(Connection connection) throws Exception{
        PreparedStatement p = null;
        ResultSet rs = null;
        int usrname = 0;
        String Username = getUserName2();
        String sql2 = "SELECT USERNAME FROM USERS WHERE USERNAME LIKE '"+Username+"'";
        p = connection.prepareStatement(sql2);
        rs = p.executeQuery();
        while(rs.next()){
            usrname++;
        }
        if(usrname>0){
            System.out.println("The Username already exists!! Try a different one :)");
            getUserName();
        }
        return Username;
    }



    public static void main (String[]args){

        Connection con = null;
        PreparedStatement p = null;
        PreparedStatement p1 = null;
        ResultSet rs = null;

//        Connection con = null;
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=jdbctry1;user=sa;password=Prevanth123;encrypt=true;trustServerCertificate=true;  ";


        System.out.print("Connecting to SQL Server ... ");
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement stmt = connection.createStatement();


        ) {
//                String sql1 = "INSERT INTO USERS VALUES (86405742,'62kdkmech7','qerivhuluq','ksprcchdeh!042',86405742)";
//                String sql1 = "SELECT * FROM USERS";
//                stmt.executeUpdate(sql1);
            //-------------------------------------------------------------------------
            //fetching the UID


            String sql1 = "SELECT TOP 1 UID FROM users ORDER BY UID DESC ";
            p = connection.prepareStatement(sql1);
            rs = p.executeQuery();
            rs.next();
            int id = rs.getInt("uid");
//                System.out.println(id);
//                System.out.println(getNumber(id));

//                int usrname = 0;
//                String Username = getUserName2();
//                String sql2 = "SELECT USERNAME FROM USERS WHERE USERNAME LIKE '"+Username+"'";
//                p = connection.prepareStatement(sql2);
//                rs = p.executeQuery();
//                while(rs.next()){
//                    usrname++;
//                }
//                if(usrname>0){
//                    System.out.println("The Username already exists!! Try a different one :)");
//                    getUserName();
//                }


//                while (rs.next()) {


//                }/

            //System.out.println((Object)stmt.executeUpdate(sql1)).getClass().getSimpleName();

//                String sql = "CREATE TABLE Users " +
//                        "(uid INTEGER , " +
//                        " username VARCHAR(255), " +
//                        " name VARCHAR(255), " +
//                        " password VARCHAR(255), " +
//                        " wallet_amount INTEGER, " +
//                        " PRIMARY KEY ( uid ))";



            System.out.println("DONE!!.");
//                String sql = "INSERT INTO USERS VALUES( "+getNumber(id)+",'"+getData(connection)+"','"+getName()+"','?',"+getWalletAmount()+",'"+getCurrency()+"');";

            String sql = "INSERT INTO USERS VALUES(?,?,?,?,?,?);";
            p1 = connection.prepareStatement(sql);

            p1.setInt(1,getNumber(id));
            p1.setString(2,getData(connection));
            p1.setString(3,getName());
            p1.setString(4,getPassword());
            p1.setInt(5,getWalletAmount());
            p1.setString(6,getCurrency());
            p1.executeUpdate();

//                pstatement = connection.prepareStatement(queryString);
//                pstatement.setString(1, search);
//                rs = pstatement.executeQuery();

            System.out.println("Check your input:");
            String sql11 = "SELECT TOP 1 * FROM USERS ORDER BY UID DESC";
            PreparedStatement p2 = connection.prepareStatement(sql11);
            rs = p2.executeQuery();
            rs.next();
            int uid = rs.getInt("uid");
            String usrname = rs.getString("username");
            String name = rs.getString("name");
            String pass = rs.getString("password");
            String curr = rs.getString("currency");
            int wall = rs.getInt("wallet_amount");

            System.out.println("the details are below:");
            System.out.println("-------------------------------------------------------");
            System.out.println(uid);
            System.out.println(usrname);
            System.out.println(name);
            System.out.println(Decrypt(pass));
            System.out.println(wall);
            System.out.println(curr);
            System.out.println("----------------------------------------------------------");










        } catch (Exception e) {
            System.out.println();
            e.printStackTrace();

        }
    }}