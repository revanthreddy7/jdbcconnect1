import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Main {
    //User Id (Auto Increment)
    public static Integer GetUserid(Connection con) throws Exception{

        String query  = "SELECT TOP 1 * FROM Users ORDER BY user_id DESC";
        int UserId;

        PreparedStatement st = con.prepareStatement(query);
        ResultSet entry = st.executeQuery();
        if(entry.next()){
            int PrevUserid = entry.getInt("user_id");
            UserId = PrevUserid + 1;
        }
        else{
            UserId = 1;

        }
        System.out.print("The Current User ID is: ");
        System.out.println(UserId);

        return UserId;
    }

    //Username
    public static String GetUsername(Connection con, Scanner sc) throws Exception{

        System.out.println("Enter the Username:");
        String UserName = sc.next();

        int len = UserName.length();
        int duplicates = 0;

        if(len>=3){
            String Query = "SELECT user_name FROM USERS WHERE user_name LIKE (?)";
            PreparedStatement st = con.prepareStatement(Query);

            st.setString(1,UserName);
            ResultSet rs = st.executeQuery();

            while(rs.next()){
                duplicates++;
            }

            if(duplicates == 0){
                return UserName;
            }
            else {
                System.out.println("This User Name Already Exists");
                GetUsername(con, sc);
            }
        }
        else {
            System.out.println("The User Name Should Contain a Minimum of three characters");
            GetUsername(con, sc);
        }

        return "";
    }

    //UserName
    public static String GetUsername2(Scanner sc){

        System.out.println("Enter the Username:");
        String UserName = sc.next();

        int len = UserName.length();

        if(len>=3){
            return UserName;
        }
        else{
            GetUsername2(sc);
        }

        return "";
    }

    //Password Encryption (RSA)
    static byte[] HashPwd(String pwd, PublicKey publicKey, PrivateKey privateKey) throws Exception{

        //Creating a Cipher object
        Cipher cipher = Cipher.getInstance("RSA");
        //Initializing a Cipher object
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        //Encrypting
        byte[] input = pwd.getBytes();
        cipher.update(input);
        byte[] cipherText = cipher.doFinal();
        System.out.println( new String(cipherText, StandardCharsets.UTF_8));

        //Decrypting
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decipheredText = cipher.doFinal(cipherText);
        System.out.println(new String(decipheredText));

        return cipherText;
    }

    //AES Encrypting Password
    public static String EncPwd(String pwd, byte[] KEY, String alg) throws Exception{

        Key key = new SecretKeySpec(KEY, alg);

        Cipher cipher = Cipher.getInstance(alg);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] encValue = cipher.doFinal(pwd.getBytes());
        byte[] encryptedValue64 = Base64.getEncoder().encode(encValue);
        String pass =  new String(encryptedValue64);
        System.out.println(pass);

        return pass;

    }

    //AES Decrypting Password
    public static String DecPwd(String pwd, byte[] KEY, String alg) throws Exception{

        Key key = new SecretKeySpec(KEY, alg);
        Cipher c = Cipher.getInstance(alg);
        c.init(Cipher.DECRYPT_MODE, key);

        byte[] decodedValue64 = Base64.getDecoder().decode(pwd.getBytes());
        byte[] decValue = c.doFinal(decodedValue64);
        return new String(decValue);
    }

    //    Password Validation
    public static String GetPassword(Scanner sc, byte[] KEY, String alg) throws Exception{
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

                return EncPwd(password, KEY, alg);
            }
            else {
                System.out.println("The password should contain a minimum of 3 numbers, 2 special characters, 2 upper case letters and 2 lower case letters");
                GetPassword(sc, KEY, alg);
            }
        }
        else {
            System.out.println("The password should contain a minimum of 8 characters");
            GetPassword(sc, KEY, alg);
        }

        return "";
    }

    // Name
    public static String GetName(){
        System.out.println("Enter your full name:");
        Scanner sc = new Scanner(System.in);

        return sc.next();
    }

    //Wallet Money
    public static int GetWalletAmount(){
        System.out.println("Please Enter the amount to be added into the wallet:");

        Scanner sc = new Scanner(System.in);

        return sc.nextInt();
    }

    //Currency
    static String GetCurrency(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Your Choice of Currency: 1:USD, 2:INR, 3:POUND, 4:WON, 5:YUAN");

        int choice = sc.nextInt();
        ArrayList<String> currencies = new ArrayList<>(
                Arrays.asList("USD", "INR", "POUND", "WON", "YUAN")
        );

        if(choice<1 || choice>5){
            System.out.println("Please enter the correct input!!");
            return GetCurrency();
        }

        return currencies.get(choice);
    }

    public static void InsertData(Connection con, Scanner sc, byte[] KEY, String algo){
        try{

            String Query = "INSERT INTO Users VALUES(?,?,?,?,?,?)";
            PreparedStatement st = con.prepareStatement(Query);

            st.setInt(1, GetUserid(con));
            st.setString(2, GetUsername2(sc));
            st.setString(3, GetName());
            st.setString(6, GetPassword(sc, KEY, algo));
            st.setInt(4, GetWalletAmount());
            st.setString(5, GetCurrency());

            st.executeUpdate();

        }
        catch(Exception e){
            e.printStackTrace();
            System.out.println("The UserName Already Exists");
            InsertData(con, sc, KEY, algo);
        }
    }
    public static void main (String[]args) throws Exception{

        Scanner sc = new Scanner(System.in);

        Connection con = null;
        PreparedStatement st;
        ResultSet rs;

        final String alg = "AES";
        final byte[] KEY = "MySuperSecretKey".getBytes(StandardCharsets.UTF_8);


        //Creating a Cipher Object for RSA Encryption
        Cipher cipher = Cipher.getInstance("RSA");

        //Creating KeyPair generator object
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");

        //Generate the pair of keys
        KeyPair pair = keyPairGen.generateKeyPair();

        //Getting the public key from the key pair
        PublicKey publicKey = pair.getPublic();

        PrivateKey privateKey = pair.getPrivate();

        //Connection URL
        String ConUrl = "jdbc:sqlserver://localhost:1433;" + "databaseName=TASK;" + "user=sa; password=Password@10;" + " encrypt=true;trustServerCertificate=true";

        System.out.print("Connecting to SQL Server ... ");

        try {

            //Establishing Connection to MSSQL Server
            con = DriverManager.getConnection(ConUrl);
            System.out.println("Connection has been established");


            //Insert Into Table
            InsertData(con, sc, KEY, alg);

            //Displaying the User Inserted data
            String DisplayQuery = "SELECT TOP 1 * FROM Users ORDER BY user_id DESC";

            st = con.prepareStatement(DisplayQuery);

            rs = st.executeQuery();
            rs.next();

            int uid = rs.getInt("user_id");
            String username = rs.getString("user_name");
            String name = rs.getString("name");
            String pass = rs.getString("pwd");
            String curr = rs.getString("currency");
            int wall = rs.getInt("wallet_amt");

            System.out.println("#-------------------------------------------------------#");
            System.out.println("User ID: "+ uid);
            System.out.println("User Name: "+username);
            System.out.println("Name: "+name);
            System.out.println("Hashed Password: "+pass);
            System.out.println("Decrypted password: "+ DecPwd(pass, KEY, alg));
            System.out.println("Wallet Amount: "+wall);
            System.out.println("Currency: "+curr);
            System.out.println("#--------------------------------------------------------#");

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (con != null) try {
                con.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

    }
}