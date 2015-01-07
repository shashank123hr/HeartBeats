package cs.uml.edu.heartbeats;

    import java.util.ArrayList;
    import java.util.List;

    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;




    public class DBHelper extends SQLiteOpenHelper {


        private static final int Version =1;
        private static final String firstName="FIRST_NAME";
        private static final String email="EMAIL";
        private static final String phoneNumber="PHONE_NUMBER";
        private static final String password="PASSWORD";
        private static final String age="AGE";
        private static final String weight="WEIGHT";
        private static final String gender="GENDER";
        private static final String databaseName="USER DATA";
        private static final String tableName="USER_RECORDS";
        private static final String hrtableName="USER_HR_RECORDS";
        private static final String hrRate="HEART_RATE";
        private static final String calorieBurned="CALORIES_BURNED";
        private static final String date="DATE";
        private static final String id = "ID";
        private String createTableSQL;
        public String usr_age="22";
        public String usr_weight="170";
        public String usr_gender="Male";


        //A constructor to initialise the database
        public DBHelper(Context context) {

            super(context, databaseName, null, Version);

        }



        @Override
        public void onCreate(SQLiteDatabase database) {
            //Create a table for user records
            createTableSQL = "CREATE TABLE " + tableName + " (" + id +" INTEGER NOT NULL PRIMARY KEY, " + firstName

                    + " TEXT, " +email + " TEXT, " + phoneNumber + " TEXT, " + password + " TEXT, " + age + " TEXT, " + weight + " TEXT, " + gender +" TEXT);";

            String createhrTableSQL = "CREATE TABLE " + hrtableName + " (" + id +" INTEGER NOT NULL PRIMARY KEY, " + hrRate

                    + " TEXT, " +calorieBurned + " TEXT, " + date + " TEXT);";

            //execute the user records table
            database.execSQL(createTableSQL);
            //execute the user Heart rate records table
            database.execSQL(createhrTableSQL);
        }


        //Sometime the creating table will cause issues so as directed by developer.android.com we called oncreate again to make sure tables are created
        @Override
        public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
            database.execSQL("DROP TABLE IF EXISTS " + tableName);
            onCreate(database);

        }


        //function add the user records to table
        public void addRecord(String firstname, String emailAddress, String phone,String pword, String ages,String weights,String genders ) {

            String insertSQL = "INSERT INTO " + tableName + " (" + firstName + "," + email + " ," + phoneNumber + " ," + password + "," + age +"," + weight + "," + gender + ") "

                    + "VALUES" + " ('" + firstname + "', '" + emailAddress + "', '" + phone + "', '" + pword + "','"+ ages +"','"+ weights +"','"+ genders +"')" ;

            SQLiteDatabase dataBase = this.getWritableDatabase();
            dataBase.execSQL(insertSQL);
            dataBase.close();
        }

        //function add the user heartrate records to table
        public void addhrRecord(String hr_Rate, String caloriesBurned, String dates ) {

            String inserthrSQL = "INSERT INTO " + hrtableName + " (" + hrRate + "," + calorieBurned + " ," + date + ") "

                    + "VALUES" + " ('" + hr_Rate + "', '" + caloriesBurned + "', '" + dates + "')" ;

            SQLiteDatabase dataBase = this.getWritableDatabase();
            dataBase.execSQL(inserthrSQL);
            dataBase.close();
        }

        //get the user records using cursor from the database
        public List<String> getRecord(String uname, String pword) {

            List<String> recordList = new ArrayList<String>();
            SQLiteDatabase dataBase = this.getReadableDatabase();
            String getSQL = "SELECT * FROM " + tableName + " WHERE " + email + " = '" + uname + "' AND " + password + " = '" + pword + "'";
            Cursor cursor = dataBase.rawQuery(getSQL , null);
            cursor.moveToFirst();
            String fName = cursor.getString(1);
            String eMail = cursor.getString(3);
            String ph = cursor.getString(4);
            usr_age = cursor.getString(5);
            usr_weight = cursor.getString(6);
            usr_gender = cursor.getString(7);
            recordList.add(fName);
            recordList.add(eMail);
            recordList.add(ph);

            dataBase.close();
            return recordList;
        }

        //get the hr records to display the status menu using cursor
        public Cursor gethrRecord()
        {
            SQLiteDatabase dataBase = this.getReadableDatabase();
            String getSQL = "SELECT * FROM " + hrtableName;
            Cursor cursor = dataBase.rawQuery(getSQL , null);
            cursor.moveToFirst();
            String fName = cursor.getString(1);
            String eMail = cursor.getString(2);
            String ph = cursor.getString(3);
            dataBase.close();
            return cursor;
        }

    }