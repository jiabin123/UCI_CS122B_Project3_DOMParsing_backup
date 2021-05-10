import java.util.List;

public class movieGenres {
    private List<String> genres;
    private final String movieId;

    public movieGenres(String movieId, List<String> genres){
        this.genres = genres;
        this.movieId = movieId;
    }

    public List<String> getGenres(){
        return genres;
    }

    public String getMovieId(){
        return movieId;
    }

    public String toString() {

        return "Genre:" + getGenres() + ", " +
                "movieId:" + getMovieId() + ". ";
    }
}
