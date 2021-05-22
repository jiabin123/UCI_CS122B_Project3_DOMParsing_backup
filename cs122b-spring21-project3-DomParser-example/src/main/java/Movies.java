public class Movies {

    private final String id;

    private final String title;

    private final int year;

    private final String director;

    public Movies(String id, String title, int year, String director){
        this.id  = id;
        this.title = title;
        this.year = year;
        this.director = director;
    }

    public String getId(){ return  id;}

    public String getTitle(){return  title;}

    public int getYear(){return  year;}

    public String getDirector(){return  director;}

    public String toString() {

        return "Director:" + getDirector() + ", " +
                "Title:" + getTitle() + ", " +
                "ID:" + getId() + ", " +
                "Year:" + getYear() + ".";
    }

}
