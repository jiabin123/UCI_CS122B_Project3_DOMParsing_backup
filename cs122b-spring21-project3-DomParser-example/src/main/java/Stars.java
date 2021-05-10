
public class Stars {
    private final String id;
    private final String name;
    private final String birthYear;

    public Stars(String id, String name, String birthYear){
        this.id = id;
        this.name = name;
        this.birthYear = birthYear;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getBirthYear(){
        return birthYear;
    }

    public String toString() {

        return "starId:" + getId() + ", " +
                "name:" + getName() + "," +
                "birthYear: " + getBirthYear();
    }
}
