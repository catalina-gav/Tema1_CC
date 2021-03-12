import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArtistController {
    Connection connection=Database.getInstance().getConnection();
    public Boolean create(Artist artist) {

        try {
            String query = "INSERT INTO artists( name, country) VALUES(?,?)";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,artist.getName());
            ps.setString(2, artist.getCountry());
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            return false;
        }
        return true;

    }
    public Artist findById(int id){
        Artist artist=null;
        try {
            String query = "Select * from artists where artist_id=+?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1,id);
            ResultSet rs=ps.executeQuery();


            if(rs.next()){
                artist=new Artist(rs.getInt("artist_id"),rs.getString("name"),rs.getString("country"));
            }


            ps.close();
            rs.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artist;

    }
    public List<Artist> findAll(){
        List<Artist> artists= new ArrayList<>();
        try {
            String query = "Select * from artists";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs=ps.executeQuery();

            while (rs.next()) {

                artists.add(new Artist(rs.getInt("artist_id"),rs.getString("name"),rs.getString("country")));
            }

            ps.close();
            rs.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artists;

    }
    public Boolean deleteArtist(int id){
        try {
            String query = "delete from artists where artist_id=+?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1,id);


            int deleted=ps.executeUpdate();
            ps.close();
            if(deleted == 0){
                return false;
            }else{
                return true;
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Boolean updatePut(Artist artist){
        try {
            String query = "update artists set name=?, country=? where artist_id=?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,artist.getName());
            ps.setString(2,artist.getCountry());
            ps.setInt(3,artist.getArtistId());
            int updated=ps.executeUpdate();
            ps.close();
            if(updated == 0){
                return false;
            }else{
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int updatePutId(Artist artist,int newId){
        try {
            String query = "update artists set artist_id = ?,name=?, country=? where artist_id=?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1,newId);
            ps.setString(2,artist.getName());
            ps.setString(3,artist.getCountry());
            ps.setInt(4,artist.getArtistId());
            int updated=ps.executeUpdate();
            ps.close();
            if(updated == 0){
                return 404;
            }else{
                return 200;
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")){
                return 409;
            }else{
                return 500;

            }
        }
    }
    public Boolean updatePatch(Artist artist){
        try {
            SQLBuilder sqlBuilder=new SQLBuilder("artists");

            if(artist.getName() != null){
                sqlBuilder.addSetClause("name",artist.getName());
            }
            if(artist.getCountry() != null){
                sqlBuilder.addSetClause("country",artist.getCountry());
            }
            sqlBuilder.addWhereClause("artist_id",artist.getArtistId());

            String query = sqlBuilder.toString();
            System.out.println(query);
            Statement statement=connection.createStatement();
            int updated=statement.executeUpdate(query);
            statement.close();
            if(updated == 0){
                return false;
            }else{
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int updatePatchId(Artist artist,int newId){
        try {
            SQLBuilder sqlBuilder=new SQLBuilder("artists");

            if(artist.getName() != null){
                sqlBuilder.addSetClause("name",artist.getName());
            }
            if(artist.getCountry() != null){
                sqlBuilder.addSetClause("country",artist.getCountry());
            }
            sqlBuilder.addSetClauseInt("artist_id",newId);

            sqlBuilder.addWhereClause("artist_id",artist.getArtistId());

            String query = sqlBuilder.toString();
            System.out.println(query);
            Statement statement=connection.createStatement();
            int updated=statement.executeUpdate(query);
            statement.close();
            if(updated == 0){
                return 404;
            }else{
                return 200;
            }

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")){
                return 409;
            }else{
                return 500;

            }
        }
    }
}
