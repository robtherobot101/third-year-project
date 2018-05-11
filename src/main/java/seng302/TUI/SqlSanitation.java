package seng302.TUI;

public class SqlSanitation {

    protected static boolean sanitizeSqlStringDelete(String sqlCommand){
        return sqlCommand.toLowerCase().contains("delete");
    }

    protected static boolean sanitizeSqlStringUpdate(String sqlCommand){
        return sqlCommand.toLowerCase().contains("update");
    }

    protected static boolean sanitizeSqlStringCreate(String sqlCommand){
        return sqlCommand.toLowerCase().contains("create");
    }

    protected static boolean sanitizeSqlStringPassword(String sqlCommand){
        return sqlCommand.toLowerCase().contains("password");
    }

    protected static boolean sanitizeSqlStringDrop(String sqlCommand){
        return sqlCommand.toLowerCase().contains("drop");
    }
}
