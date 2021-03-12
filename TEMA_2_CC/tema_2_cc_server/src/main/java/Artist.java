public class Artist {
    private int artistId;
    private String name;
    private String country;
    public Artist(String name,String country){

        this.name=name;
        this.country=country;

    }
    public Artist(int artistId,String name,String country){
        this.artistId=artistId;
        this.name=name;
        this.country=country;

    }

    public int getArtistId() {
        return artistId;
    }

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "{ \"artist_id\" : " + "\"" + getArtistId() + "\" ,"
                + "\n\"name\" : " + "\"" + getName() + "\" ," +
                "\n\"country\" : " + "\"" + getCountry() + "\"" + "}";
    }
}
