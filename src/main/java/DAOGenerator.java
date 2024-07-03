import java.sql.*;
import java.io.*;
import java.util.*;

public class DAOGenerator {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java DAOGenerator <caminho para o arquivo de configuração>");
            return;
        }

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(args[0])) {
            props.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", new String[] {"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                generateEntityClass(metaData, tableName);
                generateDaoClass(tableName);
                generateExampleClass(tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void generateEntityClass(DatabaseMetaData metaData, String tableName) {
        try {
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            PrintWriter writer = new PrintWriter(new FileWriter(tableName + ".java"));

            writer.println("public class " + tableName + " {");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnType = columns.getString("TYPE_NAME");
                String javaType = sqlTypeToJavaType(columnType);
                writer.println("    private " + javaType + " " + columnName + ";");
            }
            writer.println("}");
            writer.close();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateDaoClass(String tableName) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(tableName + "Dao.java"));
            writer.println("import java.sql.*;");
            writer.println("import java.util.*;");
            writer.println();
            writer.println("public class " + tableName + "Dao {");
            writer.println("    private Connection connection;");
            writer.println();
            writer.println("    public " + tableName + "Dao(Connection connection) {");
            writer.println("        this.connection = connection;");
            writer.println("    }");
            writer.println();
            writer.println("    public void insert(" + tableName + " " + decapitalize(tableName) + ") {");
            writer.println("        // Implementação do método de inserção");
            writer.println("    }");
            writer.println();
            writer.println("    public " + tableName + " findById(int id) {");
            writer.println("        // Implementação do método de busca por ID");
            writer.println("        return null;");
            writer.println("    }");
            writer.println();
            writer.println("    public List<" + tableName + "> findAll() {");
            writer.println("        // Implementação do método de busca de todos os registros");
            writer.println("        return null;");
            writer.println("    }");
            writer.println("}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateExampleClass(String tableName) {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(tableName + "Example.java"));
            writer.println("import java.sql.*;");
            writer.println();
            writer.println("public class " + tableName + "Example {");
            writer.println("    public static void main(String[] args) {");
            writer.println("        // Configurar a conexão com o banco de dados");
            writer.println("        try (Connection conn = DriverManager.getConnection(\"<url>\", \"<user>\", \"<password>\")) {");
            writer.println("            " + tableName + "Dao dao = new " + tableName + "Dao(conn);");
            writer.println();
            writer.println("            // Criar um novo objeto " + tableName);
            writer.println("            " + tableName + " " + decapitalize(tableName) + " = new " + tableName + "();");
            writer.println("            // Preencher o objeto com dados");
            writer.println("            // Exemplo: " + decapitalize(tableName) + ".setField(value);");
            writer.println();
            writer.println("            // Inserir o objeto no banco de dados");
            writer.println("            dao.insert(" + decapitalize(tableName) + ");");
            writer.println();
            writer.println("            // Buscar um objeto pelo ID");
            writer.println("            " + tableName + " found = dao.findById(1);");
            writer.println("            System.out.println(found);");
            writer.println();
            writer.println("            // Buscar todos os objetos");
            writer.println("            List<" + tableName + "> all = dao.findAll();");
            writer.println("            System.out.println(all);");
            writer.println("        } catch (SQLException e) {");
            writer.println("            e.printStackTrace();");
            writer.println("        }");
            writer.println("    }");
            writer.println("}");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String sqlTypeToJavaType(String sqlType) {
        switch (sqlType) {
            case "INT":
            case "INTEGER":
                return "int";
            case "FLOAT":
            case "DOUBLE":
                return "double";
            case "VARCHAR":
            case "CHAR":
                return "String";
            default:
                return "String";
        }
    }

    private static String decapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}
