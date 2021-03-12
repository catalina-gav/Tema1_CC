public class SQLBuilder {
    private StringBuilder tableQuery = new StringBuilder();
    private StringBuilder whereQuery = new StringBuilder();
    private StringBuilder setQuery = new StringBuilder();

    public SQLBuilder( String table) {
        tableQuery.append("Update ").append(table);
    }
    public SQLBuilder addSetClause(String parameter, String value){
        if (setQuery.length() == 0) {
            setQuery.append("SET ");
        }
        else {
            setQuery.append(" , ");
        }
        setQuery.append(parameter).append(" = ").append("\'").append(value).append("\'");
        return this;
    }
    public SQLBuilder addSetClauseInt(String parameter, int value){
        if (setQuery.length() == 0) {
            setQuery.append("SET ");
        }
        else {
            setQuery.append(" , ");
        }
        setQuery.append(parameter).append(" = ").append(Integer.toString(value));
        return this;
    }
    public SQLBuilder addWhereClause(String parameter, int value) {
        if (whereQuery.length() == 0) {
            whereQuery.append(" WHERE ");
        }
        else {
            whereQuery.append(" AND ");
        }
        whereQuery.append(parameter).append(" = ").append(Integer.toString(value));
        return this;
    }


    public String toString() {
        return tableQuery.toString()+ " " + setQuery.toString()+ whereQuery.toString();
    }
}
