
public class starMovies {
    private final String starId;
    private final String movieId;

    public starMovies(String starId, String movieId){
        this.starId = starId;
        this.movieId = movieId;
    }

    public String getstarId(){
        return starId;
    }

    public String getmovieId(){
        return movieId;
    }

    public String toString() {

        return "starId: " + getstarId() + ", " +
                "movieId: " + getmovieId() + ".";
    }
}
