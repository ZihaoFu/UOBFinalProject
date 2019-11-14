package fuzihao.test1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private Context mContext;
    public static final String CREATE_COLOR = "create table color (" +
            "id integer primary key autoincrement, " +
            "name text, " +
            "red1 integer, " +
            "red2 integer, " +
            "green1 integer, " +
            "green2 integer, " +
            "blue1 integer, " +
            "blue2 integer)";

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
        mContext = context;
    }

    public MySQLiteOpenHelper(Context context,String name){
        this(context,name,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String sql = "create table if not exists colorposition(name varchar(20) primary key  autoincrement, red1 integer,red2 integer, green1 integer,green2 integer, blue1 integer,blue2 integer)";
        db.execSQL(CREATE_COLOR);
        Toast.makeText(mContext, "创建数据库成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
